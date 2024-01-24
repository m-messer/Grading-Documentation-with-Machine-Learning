import java.util.Random;
import java.util.List;

/**
 * A simple model of a castor plant.
 * Castor plant is a poisonous plant, hence, when eaten,
 * it will have a negative effect on the animal.
 *
 * @version 2021.03.03
 */
public class Castor extends Plant
{
    // The growth rate of a castor plant.
    private static final double GROWTH_RATE = 1.2;
    // The likelihood of a castor plant to reproduce.
    private static final double REPRODUCTION_PROBABILITY = 0.04;
    // The maximum number of seeds.
    private static final int MAX_SEED_NUMBER = 2;
    // The growth of castor plant when it is considered mature.
    private static final int WHEN_MATURE = 10;
    // The survivalbility of the castor plant when competing for resources.
    private static final double SURVIVAL_RATE = 0.4;
    // The food value given when it is eaten by an animal.
    // Food value is zero as it causes negative effects.
    private static final int FOOD_VALUE = 0;
    // True if the castor plant is poisonous.
    private static final boolean IS_POISONOUS = true;
    // A shared random number generator to control reproduction.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new castor plant.
     * @param randomGrowth If true, the castor plant will 
     * have a random growth.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Castor(boolean randomGrowth, Field field, Location location)
    {
        super(randomGrowth, field, location);
    }
    
    /**
     * Produce new castor plants when it is mature. The new 
     * castor plants are made into free surrounding locations.
     * @param newPlants A list to return new plants.
     */
    protected void reproduce(List<Plant> newPlants)
    {
        Field field = getField();
        List<Location> free = field.getFreeSurroundingLocations(getLocation(), 2); 

        int births = seed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Castor seedling = new Castor(false, field, loc);
            newPlants.add(seedling);
        }
    }
    
    /**
     * Return the maximum number of seeds a castor plant can produce.
     * @return the castor plant's maximum seed number.
     */
    protected int getMaxSeedNum()
    {
        return MAX_SEED_NUMBER;
    }
    
    /**
     * Return the likelihood of a castor plant reproducing when
     * it is not affected by external factors.
     * @return The castor plant's default reproduction probability.
     */
    protected double getDefaultReproductionProbability()
    {
        return REPRODUCTION_PROBABILITY;
    }
    
    /**
     * Return the castor plant's growth rate.
     * @return The castor plant's growth rate.
     */
    protected double getGrowthRate()
    {
        return GROWTH_RATE;
    }
    
    /**
     * Return the castor plant's growth when it is considered mature.
     * @return The castor plant's mature growth.
     */
    protected int getWhenMature()
    {
        return WHEN_MATURE;
    }

    /**
     * Return the survival rate of the castor plant when competing 
     * for resources with other plants.
     * @return The castor plant's survival rate.
     */
    protected double getSurvivalRate()
    {
        return SURVIVAL_RATE;
    }

    /**
     * Return the food value of the castor bean when it is eaten.
     * @return The castor plant's food value.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * Check if the castor bean is poisonous.
     * @return true if the castor plant is poisonous, otherwise, 
     * return false.
     */
    public boolean getIsPoisonous()
    {
        return IS_POISONOUS;
    }
}
