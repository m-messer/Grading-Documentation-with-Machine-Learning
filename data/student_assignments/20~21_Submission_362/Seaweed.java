import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a seaweed.
 * Seaweedes age, grow and die. 
 * Seaweedes grow more during the sunny season. 
 *
 * @version 17.02.21
 */
public class Seaweed extends Plant 
{
    // Characteristics shared by all seaweeds (class variables).

    /// The age at which a seaweed can start to produce shoots.
    private static final int SHOOT_PRODUCTION_AGE = 2;
    // The age to which a seaweed can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a seaweed producing shoots during the rainy season. 
    private static final double RAINY_PRODUCTION_PROBABILITY = 0.03; 
    // The likelihood of a seaweed producing shoots during the sunny season. 
    private static final double SUNNY_PRODUCTION_PROBABILITY = 0.06; 
    // The maximum number of shoots that can be produced.
    private static final int MAX_SHOOT_PRODUCTION = 5;
    // The food value of a single seaweed. In effect, this is the
    // food value its predator gets when a seaweed is eaten.
    private static final int FOOD_VALUE = 9;
    
    // A shared random number generator to generare random ages.
    private static final Random rand = Randomizer.getRandom();

    //An object that keeps track of the weather in the field.
    private Weather weather = new Weather(); 

    // Individual characteristics (instance fields).
    // The seaweed's age.
    private int age;

    /**
     * Create a new seaweed. A seaweed may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the seaweed will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Seaweed(boolean randomAge, Field field, Location location)
    {
        super(field, location);

        if(randomAge) {
            age = rand.nextInt(MAX_AGE); 
        }
        else {
            age = 0;
        }
    }

    /**
     * This is what the seaweed does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newSeaweeds A list to return newly born seaweeds.
     */
    public void act(List<Plant> newSeaweeds)
    {
        incrementAge();
        if(isAlive()) {
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                giveBirth(newSeaweeds);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Check whether or not this seaweed is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeaweeds A list to return newly born seaweeds.
     */
    public void giveBirth(List<Plant> newSeaweeds)
    {
        // New seaweeds are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Seaweed young = new Seaweed(false, field, loc);
            newSeaweeds.add(young);
        }
    }

    /**
     * Increase the age. This could result in the seaweed's death.
     */
    public void incrementAge()
    {
        age++;
        dieOfAge(); 
    }

    //ACCESSOR METHODS 

    /**
     * Returns the age at which a seaweed can start to produce shoots.
     *
     * @return the age at which a seaweed can start to produce shoots
     */
    public int getShootProductionAge()
    {
        return SHOOT_PRODUCTION_AGE; 
    }

    /**
     * Returns the age to which a seaweed can live.
     *
     * @return the age to which a seaweed can live
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the likelihood of a seaweed producing shoots. 
     * The weather affects the production probability of seaweeds. 
     *
     * @return the likelihood of a seaweed producing shoots
     */
    public double getShootProductionProbability()
    {
        if(weather.getWeather().equalsIgnoreCase("rainy")){
            return RAINY_PRODUCTION_PROBABILITY; 
        }
        else{
            return SUNNY_PRODUCTION_PROBABILITY; 
        }
    }

    /**
     * Returns the maximum number of shoots that can be produced.
     *
     * @return the maximum number of shoots that can be produced
     */
    public int getMaxShootProduction()
    {
        return MAX_SHOOT_PRODUCTION; 
    }

    /**
     * Returns the food value of a single seaweed. 
     * In effect, this is the food value a plant-eater gets when a seaweed is eaten.
     *
     * @return the food value of a single seaweed
     */
    public int getFoodValue()
    {
        return FOOD_VALUE; 
    }

    /**
     * Returns the seaweed's age.
     *
     * @return the seaweed's age
     */
    public int getAge()
    {
        return age; 
    }
}
