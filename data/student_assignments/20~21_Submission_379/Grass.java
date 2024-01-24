import java.util.Random;
import java.util.List;

/**
 * A simple model of grass.
 * Grass is the most basic source of food for herbivores.
 *
 * @version 2021.03.03
 */
public class Grass extends Plant
{
    // The growth rate of grass.
    private static final double GROWTH_RATE = 1.2;
    // The likelihood of grass to reproduce. 
    private static final double REPRODUCTION_PROBABILITY = 0.13;
    // The maximum number of seeds.
    private static final int MAX_SEED_NUMBER = 7;
    // The growth of grass when it is considered mature.
    private static final int WHEN_MATURE = 3;
    // The survivalbility of grass when competing for resources.
    private static final double SURVIVAL_RATE = 0.2;
    // The food value given when it is eaten by an animal.
    private static final int FOOD_VALUE = 12;
    // False if grass is not poisonous.
    private static final boolean IS_POISONOUS = false;
    // A shared random number generator to control reproduction.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create new grass.
     * @param randomGrowth If true, the grass will have a random growth.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomGrowth, Field field, Location location)
    {
        super(randomGrowth, field, location);
    }
    
    /**
     * Produce new grass when it is mature. The new grass are
     * made into free surrounding locations.
     * @param newPlants A list to return new plants.
     */
    protected void reproduce(List<Plant> newPlants)
    {
        Field field = getField();
        List<Location> free = field.getFreeSurroundingLocations(getLocation(), 6); 

        int births = seed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Grass seedling = new Grass(false, field, loc);
            newPlants.add(seedling);
        }
    }
    
    /**
     * Return the maximum number of seeds grass can produce.
     * @return the grass' maximum seed number.
     */
    protected int getMaxSeedNum()
    {
        return MAX_SEED_NUMBER;
    }
    
    /**
     * Return the likelihood of grass reproducing when it is
     * not affected by external factors.
     * @return The grass' default reproduction probability.
     */
    protected double getDefaultReproductionProbability()
    {
        return REPRODUCTION_PROBABILITY;
    }
    
    /**
     * Return the grass' growth rate.
     * @return The grass' growth rate.
     */
    protected double getGrowthRate()
    {
        return GROWTH_RATE;
    }
    
    /**
     * Return the grass' growth when it is considered mature.
     * @return The grass' mature growth.
     */
    protected int getWhenMature()
    {
        return WHEN_MATURE;
    }

    /**
     * Return the survival rate of grass when competing for
     * resources with other plants.
     * @return The grass' survival rate.
     */
    protected double getSurvivalRate()
    {
        return SURVIVAL_RATE;
    }

    /**
     * Return the food value of grass when it is eaten.
     * @return The grass' food value.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * Check if the grass is poisonous.
     * @return true if the grass is poisonous, otherwise, return false.
     */
    public boolean getIsPoisonous()
    {
        return IS_POISONOUS;
    }
}