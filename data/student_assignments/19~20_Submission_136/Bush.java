import java.util.*;

/**
 * A simple model of a Bush.
 * bushes age, grow when its raining, spreads and die.
 *
 * @version 2020.02.23
 */
public class Bush extends SmallPlant
{
    // Characteristics shared by all bushes (class variables).

    // The age at which a bushes can start to spread.
    private static final int SPREADING_AGE = 2;
    // The age to which a bushes can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a bushes spread.
    private static final double SPREADING_PROBABILITY = 0.8;
    // The maximum number of births.
    private static final int MAX_SEEDS = 8;
    
    /**
     * Create a bush. A bush can be created as a new plant (age zero)
     * or with a random age.
     * 
     * @param randomAge If true, the bush will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bush(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Get the maximum age of the of the bush
     * @return maximum age of bush
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * The bushes spread an grow in age until they die
     * @param newBushes A list to return newly born bushes.
     */
    public void act(List<Actor> newBushes)
    {
        super.incrementAge();
        if(isAlive()) {
            doSpread(newBushes);
        }
    }

    /**
     * Makes the bushes live throughout the simulation
     * depending on the simulation steps, each time increasing their age
     */
    public void act() {
        incrementAge();
    }
    
    /**
     * Gets the spreading age of the bushes
     */
    public int getSpreadingAge()
    {
        return SPREADING_AGE;
    }
    
    /**
     * Gets the maximum number of seed the bushes can spread
     */
    public int getMaxSeeds()
    {
        return MAX_SEEDS;
    }
    
    /**
     * Get the specific probability of the Bushes
     */
    public double getSpreadingProbability()
    {
        return SPREADING_PROBABILITY;
    }
}
