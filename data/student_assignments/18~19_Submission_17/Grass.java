import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Grass.
 * Grasses age, move, eat Goats, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Grass extends Plants
{
    // Characteristics shared by all Grasses (class variables).
    
    // The age at which a Grass can start breeding.
    private static final int BREEDING_AGE = 5;
    // The age to which a Grass can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a Grass breeding growing a daughter grass.
    private static final double BREEDING_PROBABILITY = 0.99;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // the age of a grass 
    private int age; 
    
    /**
     * Create a Grass. A Grass can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Grass will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
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
     * This is what the Grass does most of the time. 
     * In the process, it might breed
     * or die of old age. 
     * @param field The field currently occupied.
     * @param newDragones A list to return newly born Dragones.
     */
    public void act(List<Plants> newGrass)
    {
        incrementAge();
        if(isAlive()) {
            // It just grows 
            giveBirth(newGrass); 
        }
    }

    /**
     * Increase the age. This could result in the Grass's death.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this Grass is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDragones A list to return newly born Dragones.
     */
    private void giveBirth(List<Plants> newGrass)
    {
         // New Grass are born into adjacent locations.
         // Get a list of adjacent free locations.
         Field field = getField();
         List<Location> free = field.getFreeAdjacentLocations(getLocation());
         int births = breed();
         for(int b = 0; b < births && free.size() > 0; b++) {
             Location loc = free.remove(0);
             Grass young = new Grass(false, field, loc);
             newGrass.add(young);
         }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A Grass can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
