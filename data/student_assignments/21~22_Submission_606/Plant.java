import java.util.List;
/**
 * A class representing shared characteristics of Plants.
 * All values of the fields can only be known when an actural Plant object is created.
 * All Plants can eat(photosynthesis),drink(get the rain) and grow
 *
 * @version 2022.03.01 (15)
 */
public abstract class Plant extends LivingThing
{
    //how much step can the plant act without sun light
    private int sunLevel;
    private int max_sunLevel;
    //how much step can the plant act without rain
    private int waterLevel;
    private int max_waterLevel;
    private double breeding_Probability;
    private int max_litter_size;
    private int breeding_age;
    /**
     * Create a new Plant
     */
    public Plant(Field field, Location location,DateTime dateTime)
    {
        super(field,location,dateTime);
    }

    /**
     * Make this plant act - that is to get water,get sun light and grow.
     * @param newPlants A list to receive newly born Plants.
     */
    public void act(List<LivingThing> newPlants)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        incrementAge();

        if(canAct()) { 
            if(isSunny()){
                sunLevel=max_sunLevel;
            }

            incrementWaterLevel();

            grow(newPlants);
        }
    }

    /**
     * Make this plant grow- reproduce same type of plants in the free adjacent grids.
     * @param newPlants A list to receive newly born Plants.
     */
    private void grow(List<LivingThing> newPlants)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        // New plants are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        DateTime dateTime = getDateTime();
        List<Location> free = field.getFreeAdjacentPlantLocations(getLocation());

        int births = breed();
        //all plants grow in the same way
        for(int b = 0; b < births && free.size() > 0&&free.size()-b>=births; b++) {
            Location loc = free.remove(0);
            Class[] paraType= new Class[4];
            paraType[0]=boolean.class;
            paraType[1]=Field.class;
            paraType[2]=Location.class;
            paraType[3]=DateTime.class;
            //only know the actural type of the plant at runtime
            //create a new object by the constructor
            Plant newPlant = this.getClass().getDeclaredConstructor(paraType).newInstance(false,field,loc,dateTime);
            newPlants.add(newPlant);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= breeding_Probability) {
            births = rand.nextInt(max_litter_size) + 1;

        }
        return births;
    }

    /**
     * @return true if breeding age is meeted and the weather is sunny in the grid.
     */
    protected boolean canBreed()
    {
        return getAge() >= breeding_age&&isSunny();
    }

    /**
     * @return true if the weather is sunny in the grid.
     */
    private boolean isSunny()
    {
        Field field = getField();
        WeatherTile weather = field.getWeatherAt(getLocation());
        return getDateTime().isDay()&&weather==null;
    }

    /**
     * Set the data of the plant once the type is known.
     */
    protected void setData(int foodValue,int max_age,int max_sunLevel, int max_waterLevel, int max_litter_size, double breeding_Probability)
    {
        setData(foodValue, max_age);
        this.max_sunLevel = max_sunLevel;
        this.max_waterLevel=max_waterLevel;
        this.max_litter_size=max_litter_size;
        this.breeding_Probability=breeding_Probability;
        breeding_age= (int) (max_age*0.1);
        sunLevel=max_sunLevel;
        waterLevel=max_waterLevel;
    }

    /**
     * Set random age for the simulation to start
     */
    protected void setRandomAge()
    {
        super.setRandomAge();
        sunLevel = rand.nextInt(max_sunLevel);
        waterLevel= rand.nextInt(max_waterLevel);
    }

    /**
     * Decrease the water and sun levels of the plant.
     * This can result in the plant's death.
     */
    private void decrementLevel()
    {
        waterLevel--;
        sunLevel--;
        if(sunLevel<=0||waterLevel<=0){
            setDead();
        }

    }

    /**
     * Simulate the plant gets water from the rain.
     */
    private void incrementWaterLevel()
    {
        Field field = getField();
        WeatherTile weather = field.getWeatherAt(getLocation());
        if(weather!=null){
            waterLevel+=weather.getRainFallValue();
            if(waterLevel>max_waterLevel){
                waterLevel=max_waterLevel;
            }
        }

    }
}
