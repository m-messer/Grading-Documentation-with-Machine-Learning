import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * A model of a Plant.
 *
 */
public class Plant extends Animal
{
    // Characteristics shared by all plants (class variables).

    // The age at which a plant can start to breed.
    private static final int BREEDING_AGE =3;
    // The age to which a plant can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a plant breeding.
    private double breeding_probability = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The plant's age.
    private int age;

    /**
     * Create a new plant. A plant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the plant will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the plant does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newPlants A list to return newly born plants.
     */
    public void act(List<Animal> newPlants)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newPlants);            
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    /**
     * Plants don't reproduce if there is no sunlight.
     */
    public void nightAct(List<Animal> newPlants)
    {
       
    }
    
    /**
     * Plants grow faster when it rains.
     */
    public void rainAct(List <Animal> newPlants)
    {
        breeding_probability = 0.2;
        act(newPlants);
        breeding_probability = 0.1;
    }
    
    /**
     * Plants don't reproduce if there is no sunlight.
     */
    public void fogAct(List <Animal> newPlants)
    {
        
    }
    /**
     * Plants behave normally during a storm.
     */
    public void stormAct(List <Animal> newPlants)
    {
        act(newPlants);     
    }
    
    /**
     * Increase the age.
     * This could result in the plant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this plant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPlants A list to return newly born plants.
     */
    private void giveBirth(List<Animal> newPlants)
    {
        // New plants are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = new Plant(false, field, loc);
            newPlants.add(young);
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
        if(canBreed() && rand.nextDouble() <= breeding_probability) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A plant can breed if it has reached the breeding age.
     * @return true if the plant can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}