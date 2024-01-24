import java.util.Random;
import java.util.List;

/**
 * A model of a grass patch.
 * Grass age, reproduce, breed, die and absorb water .
 *
 * @version 2022.02.22
 */
public class Grass extends Plant
{
    // Characteristics shared by all grasss (class variables).
    // The age at which a grass can start to reproduce.
    private static final int REPRODUCE_AGE = 5;
    // The age to which a grass can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a grass reproducing.
    private static final double REPRODUCING_PROBABILITY = 0.99;
    //The maximum number of births. 
    private static final int MAX_LITTER_SIZE = 4;  
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //The maximum amount of water a grass can absorb.
    private static final int MAX_WATER_LEVEL = 10;

    // Individual characteristics (instance fields).
    // The grass's age.
    private int age;
    // The grass's water level, which is increased by absorbing water.
    private int waterLevel;

    /**
     * Create a grass patch. A grass can be created as a new born (age zero
     * and not thirsty) or with a random age and water level.
     * @param randomAge If true, the grass will have random age and water level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        waterLevel = rand.nextInt(MAX_WATER_LEVEL);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
    }

    /**
     * This is what the grass does most of the time: it absorbs water.
     * In the process, it might reproduce (only in the day time) or 
     * die of wilting, old age or overcrowding. 
     * @param newGrass A list to return newly born grass patches.
     */
    public void act(List<Organism> newGrasses)
    {
        incrementAge();
        if(isAlive()) {
            if (dayTime()){
                reproduce(newGrasses);
            }
            Location newLocation = getField().freeAdjacentLocation(getLocation()); 
            if (newLocation == null) {
                //Overcrowding
                setDead();
            }
        }      
    }

    /**
     * Return the age for this grass.
     * @return The grass's age.
     */
    protected int getAge() 
    {
        return age;
    }

    /**
     * Increase the age.
     * This could result in the grass's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this grass more thirsty. This could result in the grass's death
     * due to wilting.
     */
    public void incrementThirst()
    {
        waterLevel--;
        if(waterLevel < 0) {
            setDead();
        }
    }

    /**
     * Increment the water level of grass.
     */
    public void incrementWaterLevel()
    {
        waterLevel ++;
    }

    /**
     * Check whether or not this grass is to reproduce at this step.
     * New grass will be made in free adjacent locations.
     * @param newGrass A list to return newly born grass.
     */
    protected void reproduce(List<Organism> newGrasses)
    {
        // New grasses are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(); 
        for(int b = 0; free.size() > 0 && b < births; b++) {
            Location loc = free.remove(0);
            Grass young = new Grass(false, field, loc);
            newGrasses.add(young);
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
        if (canBreed()) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1; 
        }
        return births;
    }

    /**
     * A grass can breed if it has reached the breeding age.
     * @return true if the grass can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= REPRODUCE_AGE && rand.nextDouble() <= REPRODUCING_PROBABILITY;
    }
}