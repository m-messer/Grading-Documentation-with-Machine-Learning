import java.util.*;

/**
 * A simple model of an Acacia tree.
 * acacia trees age, grow when its raining, spreads and die.
 *
 * @version 2020.02.23
 */
public class Acacia extends Tree
{
    // Characteristics shared by all acacia trees (class variables).

    // The age at which a tree can start to spread.
    private static final int SPREADING_AGE = 100;
    // The age to which a tree can live.
    private static final int MAX_AGE = 1000;
    // The likelihood of a tree spreading.
    private static final double SPREADING_PROBABILITY = 0.005;
    // The maximum number of births.
    private static final int MAX_SEEDS = 10;
    // The growth ratio of the acacia tree
    private static final int GROWING_RATIO = 1;
    
    /**
     * Create an acacia tree. A tree can be created as a new tree (age zero and 
     * size 1)
     * or with a random age and size.
     * 
     * @param randomAge If true, the tree will have random age and size.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Acacia(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Get the maximum age of the of the tree
     * @return maximum age of tree
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * The trees spread an grow in age and size until they die
     * @param newAcacias A list to return newly born trees.
     */
    public void act(List<Actor> newAcacias)
    {
        incrementAge();
        if(isAlive()) {
            grow();
            doSpread(newAcacias);
        }
    }

    /**
     * Makes the trees live throughout the simulation
     * depending on the simulation steps, each time increasing their age and size
     */
    public void act() {
        incrementAge();
    }

    /**
     * Gets the spreading age of the trees
     */
    public int getSpreadingAge()
    {
        return SPREADING_AGE;
    }
    
    /**
     * Gets the maximum number of seeds the trees can spread
     */
    public int getMaxSeeds()
    {
        return MAX_SEEDS;
    }
    
    /**
     * Get the specific probability of the trees
     */
    public double getSpreadingProbability()
    {
        return SPREADING_PROBABILITY;
    }

    /**
     * Get growth ratio of the tree
     */
    public double getGrowingRatio() {
        return GROWING_RATIO;
    }
}
