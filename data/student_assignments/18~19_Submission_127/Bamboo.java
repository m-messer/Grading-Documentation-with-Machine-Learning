import java.util.List;
import java.util.Random;

/**
 * A simple model of bamboo.
 * Bamboos can grow and die.
 *
 * @version 2019.02.22 
 */
public class Bamboo extends Plant
{
    // Characteristics shared by all bamboos (class variables).
    
    // The age to which bamboo can live.
    private static final int MAX_AGE = 40;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The bamboo's age.
    public int bambooAge;

    /**
     * Create new bamboo. A bamboo may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the bamboo will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bamboo(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        bambooAge = 0;
        if(randomAge) {
            bambooAge = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the bamboo does most of the time during the day- it stays
     * in one place.
     * Sometimes it will breed or die of old age.
     * @param newGazelles A list to return newly born gazelles.
     */
    public void act(List<Plant> newBamboos)
    {
        grow();
    }
    
    /**
     * Check whether or not this bamboo is to breed at this step.
     * New plants will be made into free adjacent locations.
     * @param newGazelles A list to return newly bred bamboos.
     */
    private void grow()
    {
        if(isRain()) {
            bambooAge = bambooAge + 50;
        }
    }
}