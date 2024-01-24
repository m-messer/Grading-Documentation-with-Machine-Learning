import java.util.Random;
import java.util.List;

/**
 * A simple model of a berry bush.
 * Berries are eaten by all herbivores.
 *
 * @version 2021.03.03
 */
public class Berry extends Plant
{
    // The growth rate of a blueberry bush.
    private static final double GROWTH_RATE = 1.1;
    // The likelihood of a blueberry bush to reproduce.
    private static final double REPRODUCTION_PROBABILITY = 0.11;
    // The maximum number of seeds.
    private static final int MAX_SEED_NUMBER = 5;
    // The growth of a blueberry bush when it is considered mature.
    private static final int WHEN_MATURE = 4;
    // The survivalbility of a blueberry bush when competing for resources.
    private static final double SURVIVAL_RATE = 0.3;
    // The food value given when it is eaten by an animal.
    private static final int FOOD_VALUE = 15;
    // False if grass is not poisonous.
    private static final boolean IS_POISONOUS = false;
    // A shared random number generator to control reproduction.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new berry.
     * @param randomGrowth If true, the grass will have a random growth.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Berry(boolean randomGrowth, Field field, Location location)
    {
        super(randomGrowth, field, location);
    }
    
    /**
     * Produce new berries when it is mature. The new 
     * berries are made into free surrounding locations.
     * @param newPlants A list to return new plants.
     */
    protected void reproduce(List<Plant> newPlants)
    {
        Field field = getField();
        List<Location> surrounding = field.getFreeSurroundingLocations(getLocation(), 3); 

        int births = seed();
        for(int b = 0; b < births && surrounding.size() > 0; b++) {
            Location loc = surrounding.remove(0);
            Berry seedling = new Berry(false, field, loc);
            newPlants.add(seedling);
        }
    }
    
    /**
     * Return the maximum number of seeds berries can produce.
     * @return the berry's maximum seed number.
     */
    protected int getMaxSeedNum()
    {
        return MAX_SEED_NUMBER;
    }
    
    /**
     * Return the likelihood of a berry bush reproducing
     * when it is not affected by external factors.
     * @return The berry bush's default reproduction probability.
     */
    protected double getDefaultReproductionProbability()
    {
        return REPRODUCTION_PROBABILITY;
    }
    
    /**
     * Return the berry bush's growth rate.
     * @return The berry bush's growth rate.
     */
    protected double getGrowthRate()
    {
        return GROWTH_RATE;
    }
    
    /**
     * Return the berry bush's growth when it is 
     * considered mature.
     * @return The berry bush's mature growth.
     */
    protected int getWhenMature()
    {
        return WHEN_MATURE;
    }

    /**
     * Return the survival rate of the berry bush when competing
     * for resources with other plants.
     * @return The berry bush's survival rate.
     */
    protected double getSurvivalRate()
    {
        return SURVIVAL_RATE;
    }

    /**
     * Return the food value of the berry when it is eaten.
     * @return The berry bush's food value.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * Check if the berry is poisonous.
     * @return true if the berry is poisonous, otherwise, 
     * return false.
     */
    public boolean getIsPoisonous()
    {
        return IS_POISONOUS;
    }
}