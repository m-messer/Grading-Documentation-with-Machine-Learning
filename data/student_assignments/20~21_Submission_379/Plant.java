import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 * Plants grow and reproduce when there is sufficient amount
 * of sunlight and water. Their behaviour may vary depending 
 * if there is too little or too much of sunlight or water.
 * Plants may compete with each other for resources.
 *
 * @version 2021.03.03
 */
public abstract class Plant
{
    // Whether the plant is alive or not.
    private boolean alive;
    // The plant's field.
    private Field field;
    // The plant's position in the field.
    private Location location;
    // The growth of the plant.
    private double growth;
    // The likelihood of a plant reproducing.
    private double reproductionProbability;
    
    // A shared random number generator to control reproduction.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new plant at location in field. A plant may be created
     * with growth one (a new seed) or with a random growth.
     * @param randomGrowth If true, the plant will have a random growth.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomGrowth, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        growth = 1;
        reproductionProbability = getDefaultReproductionProbability();

        if(randomGrowth){
            growth = rand.nextInt(getWhenMature());
        }
    }

    /**
     * Check whether the plant is alive or not.
     * @return true if the plant is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Return the plant's field.
     * @return The plant's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlants A list to receive newly grown plants.
     * @param isDay The time of day
     * @param weather The current weather.
     */
    public void act(List<Plant> newPlants, boolean isDay, Weather weather)
    {
        competition();
        
        if(alive && isDay && !weather.noRainAndSun()){
            grow();
            // Plants can only reproduce once they are mature.
            if(growth >= getWhenMature()){
                setReproductionProbability(weather);
                reproduce(newPlants);
            }
        }
    }
    
    /**
     * Increase the growth of the plant.
     */
    protected void grow()
    {
        growth = growth * getGrowthRate();
    }
    
    /**
     * Return the plant's growth rate.
     * @return The plant's growth rate.
     */
    abstract protected double getGrowthRate();

    /**
     * Return the plant's growth when it is considered mature.
     * @return The plant's mature growth.
     */
    abstract protected int getWhenMature();
    
    /**
     * Produce new seeds once it is mature.
     * New plants will be made into free surrounding locations.
     * @param newPlants A list to return new plants.
     */
    abstract protected void reproduce(List<Plant> newPlants);
    
    /**
     * Generate a number representing the number of seeds,
     * if it can reproduce.
     * @return The number of seeds (may be zero).
     */
    protected int seed()
    {
        int seeds = 0;
        if(rand.nextDouble() <= reproductionProbability) {
            seeds = rand.nextInt(getMaxSeedNum()) + 1;
        }
        return seeds;
    }

    /**
     * Return the maximum number of seeds a plant can produce.
     * @return the plant's maximum seed number.
     */
    abstract protected int getMaxSeedNum();
    
    /**
     * Increase reproduction probability if the weather is sunny or rainy,
     * otherwise, set to default value.
     * @param weather The current weather
     */
    private void setReproductionProbability(Weather weather)
    {
        String currentWeather = weather.getCurrent();
        double prob = getDefaultReproductionProbability();

        if(currentWeather.equals("rainy") || currentWeather.equals("sunny")) {
            reproductionProbability = prob * 1.3;
        } else {
            reproductionProbability = prob;
        }
    }
    
    /**
     * Return the likelihood of the plant reproducing when
     * it is not affected by external factors.
     * @return The plant's default reproduction probability.
     */
    abstract protected double getDefaultReproductionProbability();

    /**
     * Check if the plant is able to survive when competing for 
     * resources with adjacent plants. Plants only start competing
     * when there are many plants crowding around it.
     */
    public void competition()
    {
        if (alive) {
            List<Location> free = field.getFreeAdjacentLocations(location);
            
            // Make plants compete with each other if too many plants are around it
            if(free.size() <= 4){
                List<Location> adjacent = field.adjacentLocations(location);
                Iterator<Location> it = adjacent.iterator();
                while(it.hasNext()) {
                    Location where = it.next();
                    Object plant = field.getObjectAt(where);
                    if(plant instanceof Plant){
                        Plant currentPlant = (Plant) plant;
                        if(getSurvivalRate() <= currentPlant.getSurvivalRate()){
                            setDead();
                            return;
                        }
                    }
                }
            }
        }
    }

    /**
     * Return the survival rate of the plant when competing 
     * for resources with other plants.
     * @return The plant's survival rate.
     */
    abstract protected double getSurvivalRate();

    /**
     * Return the food value of the plant when it is eaten.
     * @return The plant's food value.
     */
    abstract protected int getFoodValue();

    /**
     * Check if the plant is poisonous.
     * @return true if the plant is poisonous, otherwise, return false.
     */
    abstract public boolean getIsPoisonous();
}