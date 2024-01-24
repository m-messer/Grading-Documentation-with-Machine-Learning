import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * All values of the fields can only be known when an actural Animal object is created.
 * All animals can eat,drink,reproduce and run.
 *
 * @version 2022.03.01 (15)
 */
public abstract class Animal extends LivingThing
{
    //things the animal can eat
    private HashSet<Class> foodSource;
    //Animals that eat this animal
    private HashSet<Class> predators;
    //Current food level(energy)
    private int foodLevel;
    private int max_foodLevel;
    //How many steps can this animal take without drinking water
    private int thirstyLevel;
    private int max_thirstyLevel;
    private Gender gender;
    //How far can this animal see while hunting
    private int currentHuntingDistance;
    private int bestHuntingDistance;

    /**
     * Create a new animal
     */
    public Animal(Field field, Location location,DateTime dateTime)
    {
        super(field,location,dateTime);
        foodSource = new HashSet<>();
        predators = new HashSet<>();

    }

    /**
     * Make this animal act - that is to eat,drink,breed,run
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<LivingThing> newAnimals)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        incrementThristy();
        incrementAge();
        incrementHunger();
        if(getDisease()!=null){
            getDisease().act(this);
        }
        currentHuntingDistance=bestHuntingDistance;
        if(canAct()) {      
            affectedbyFog();
            Location newLocation;
            Location predatorLocation = senseDanger();
            if(predatorLocation!=null){
                newLocation = runAwayFrom(predatorLocation);
            }
            else if(isThristy()){
                //Move towards a source of water if found.
                newLocation = findWater();
            }
            else if(isHungry()){
                // Move towards a source of food if found.
                newLocation= findFood();
            }
            else{
                newLocation =gender.behave(this,newAnimals);
            }
            if(newLocation == null) { 
                // No movement - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Look for all living things within the hunting distance to the current location.
     * Only the first living thing is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        for(int i =1;i<=currentHuntingDistance;i++){
            List<Location> adjacent = field.adjacentLocations(getLocation(),i);
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Animal animal = field.getAnimalAt(where);
                Plant plant = field.getPlantAt(where); 
                if(canEat(animal)) {
                    if(i==1){
                        return eat(animal);
                    }
                    else{
                        //Move towards the animal
                        return field.shortestWayFirstStep(getLocation(), animal.getLocation(), currentHuntingDistance);
                    }
                }
                else if(canEat(plant)){
                    if(i==1){
                        return eat(plant);
                    }
                    else{
                        //Move towards the plant
                        return field.shortestWayFirstStep(getLocation(), plant.getLocation(), currentHuntingDistance);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Look for waters within the hunting distance*3 to the current location.
     * Only the first water found will be located
     * @return Where water was found, or null if it wasn't.
     */
    private Location findWater()
    {
        Field field = getField();
        for(int i =1;i<=currentHuntingDistance*3;i++){
            List<Location> adjacent = field.adjacentLocations(getLocation(),i);
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                WaterTile water = field.getWaterAt(where);
                if(water!=null){
                    if(i==1){
                        thirstyLevel=max_thirstyLevel;
                        if(water.getDisease()!=null){
                            setDisease(water.getDisease());
                        }
                    }
                    else{
                        //Move towards the water source
                        return field.shortestWayFirstStep(getLocation(), where, currentHuntingDistance*3);
                    }
                }

            }
        }
        return null;
    }

    /**
     * Look for predator within the aware distance to the current location.
     * Only the first predator found will be located
     * @return Where predator was found, or null if it wasn't.
     */
    private Location senseDanger()
    {
        Field field = getField();
        //it is less likely for the prey to see the predator
        int awareDistance = rand.nextInt(currentHuntingDistance+1)/2;
        for(int i =1;i<=awareDistance;i++){
            List<Location> adjacent = field.adjacentLocations(getLocation(),i);
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Animal animal = field.getAnimalAt(where);
                if(animal!=null&&predators.contains(animal.getClass())){
                    return animal.getLocation();
                }

            }
        }
        return null;
    }

    /**
     * Find the best possible location to stay away from the predator
     * @return a free location that has the greatest distance from the predator.
     */
    private Location runAwayFrom(Location predatorLocation)
    {
        List<Location> possibleMoves = getField().getFreeAdjacentLocations(getLocation());
        if(possibleMoves.size()==0){
            //all adjacent grids are occupied
            //try to see if the animal can eat the adjacent animals to run
            return findFood();
        }
        double greatestDistance = 0;
        int index=0;
        for(int i=0;i<possibleMoves.size();i++){
            double distance= getField().getDistance(possibleMoves.get(i), predatorLocation);
            if(distance>greatestDistance){
                index = i;
                greatestDistance = distance;
            }
        }
        return possibleMoves.get(index);
    }

    /**
     * Check if the animal can eat the given living entity.
     * @param A living entity (can be null)
     * @return true if it can eat the thing.
     */
    private boolean canEat(LivingThing thing)
    {
        if(thing==null){
            return false;
        }

        return foodSource.contains(thing.getClass());
    }

    /**
     * Simulate the eat action of an animal.
     * Kill the living thing and increase the food level of the animal.
     * @param A living entity
     * @return the location of the living thing
     */
    private Location eat(LivingThing thing)
    {
        if(thing.canAct()) { 
            thing.setDead();
            if(thing.getDisease()!=null){
                setDisease(thing.getDisease());
            }
            foodLevel += thing.getFoodValue();
            if(foodLevel> max_foodLevel){
                foodLevel = max_foodLevel;
            }

            return thing.getLocation();
        }
        return null;
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();

        }
    }

    /**
     * add a food source for the animal
     * @param the class of an living entity that the animal will eat
     */
    protected void addFoodSource(Class thingClass)
    {
        foodSource.add(thingClass);
    }

    /**
     * add a predator for the animal
     * @param the class of an living entity that will eat the animal
     */
    protected void addPredator(Class predatorClass)
    {
        predators.add(predatorClass);
    }

    /**
     * @return the gender of the animal
     */
    protected Gender getGender()
    {
        return gender;
    }

    /**
     * @return true if the animal is hungry
     */
    protected boolean isHungry()
    {
        return (foodLevel<=0.6*max_foodLevel);
    }

    /**
     * @return true if the animal is thristy
     */
    protected boolean isThristy()
    {
        return (thirstyLevel<=0.3*max_thirstyLevel);
    }

    /**
     * Make this animal more thristy. This could result in the animal's death.
     */
    protected void incrementThristy()
    {
        thirstyLevel--;
        if(thirstyLevel<=0){
            setDead();

        }
    }

    /**
     * Set the gender of the animal.
     * 50% chance being a male,50% chance being a female.
     * @param the maximum children an animal can have, the probability of breeding
     */
    private void setGender(int max_litter_size,double breeding_probability)
    {
        int breeding_age= (int)Math.round(getMaxAge()*0.2);
        int breeding_end= (int)Math.round(getMaxAge()*0.8);
        if(rand.nextInt(2)==0){
            gender = new Male( breeding_age,breeding_end);
        }
        else{
            gender = new Female(breeding_age,breeding_end,max_litter_size,breeding_probability);
        } 
    }

    /**
     * Set up randomAge for the simulation starting point
     */
    protected void setRandomAge()
    {
        super.setRandomAge();
        foodLevel = rand.nextInt(max_foodLevel);
        thirstyLevel=  rand.nextInt(max_thirstyLevel);
    }

    /**
     * Set the data of the animal once the type is known.
     */
    protected void setData(int foodValue,int max_age,int max_foodLevel,int max_thirstyLevel,int huntingDistance,int max_litter_size,double breeding_probability)
    {
        setData(foodValue, max_age);
        this.max_foodLevel=max_foodLevel;
        this.max_thirstyLevel=max_thirstyLevel;
        this.bestHuntingDistance= huntingDistance;
        currentHuntingDistance=bestHuntingDistance;
        setGender(max_litter_size,breeding_probability);
        foodLevel = max_foodLevel;
        thirstyLevel= max_thirstyLevel;

    }

    /**
     * Allow movement in the field by
     * setting the animal's location.
     */
    protected void setLocation(Location newLocation)
    {
        if(getLocation() != null) {
            getField().clearAnimal(getLocation());
        }
        super.setLocation(newLocation);
    }

    /**
     * @return the hunting distance of the animal.
     */
    protected int getHuntingDistance()
    {
        return currentHuntingDistance;
    }

    /**
     * Allow changing the hunting distance of the animal.
     */
    protected void setHuntingDistance(int huntingDistance)
    {
        this.bestHuntingDistance= huntingDistance;
    }
    protected void decrementHuntingDistance()
    {
        bestHuntingDistance--;
        if(bestHuntingDistance<0){
            bestHuntingDistance=0;
        }
    }
    /**
     * Decrease the hunting distance by the fog
     */
    private void affectedbyFog()
    {
        Field field =getField();
        Location location = getLocation();
        WeatherTile weather = field.getWeatherAt(location);
        if(weather !=null){
            currentHuntingDistance=bestHuntingDistance-weather.getFogValue();
            if(currentHuntingDistance<1){
                currentHuntingDistance=1;
            }
        }
    }
}
