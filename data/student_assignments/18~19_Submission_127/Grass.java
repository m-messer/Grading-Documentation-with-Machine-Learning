import java.util.List;
import java.util.Random;

/**
 * A simple model of grass.
 * Grass grow and die.
 *
 * @version 2019.02.22 
 */
public class Grass extends Plant
{
    // Characteristics shared by all grass (class variables).
    
    // The age to which grass can live.
    private static final int MAX_AGE = 40;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The grass's age.
    public int grassAge;
    
    /**
     * Create new grass. Grass may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the grass will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        grassAge = 0;
        
        if(randomAge) {
            grassAge = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the grass does most of the time during the day- it stays 
     * in one place. Sometimes it will breed or die of old age.
     * @param newGrasses A list to return newly born grasses.
     */
    public void act(List<Plant> newGrasses)
    {
        grow();
    }

    /**
     * Increase the age.
     * This could result in the grass's death.
     */
    private void grow()
    {
        if(isRain()) {
            grassAge = grassAge + 50;
        }
    }
}