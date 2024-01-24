import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a rice.
 * Rice age, move, breed, and die.
 *
 * @version 2020.2.22
 */
public class Rice extends Plant
{
    // Characteristics shared by all rice (class variables).

    // The age at which a rice can start to breed.
    private static final int BREEDING_AGE = 7;
    // The age to which a rice can live.
    private static final int MAX_AGE = 35;
    // The likelihood of a rice breeding.
    private static final double BREEDING_PROBABILITY = 0.85;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The rice's age.
    private int age;

    /**
     * Create a new rice. A rice may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rice will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rice(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the rice does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRice A list to return newly born rice.
     */
    public void act(List<Plant> newRice)
    {
        incrementAge();
        if(isAlive()) {
               giveBirth(newRice);          

          
        }
    }

    /**
     * Increase the age.
     * This could result in the rice's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this rice is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRice A list to return newly born rice.
     */
    private void giveBirth(List<Plant> newRice)
    {
        // New rice are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocationsPlant(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Rice young = new Rice(false, field, loc);
            newRice.add(young);
            
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
     * A rice can breed if it has reached the breeding age.
     * @return true if the rice can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
