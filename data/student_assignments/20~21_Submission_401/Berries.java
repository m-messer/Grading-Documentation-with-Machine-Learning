import java.util.Random; 
import java.util.List;
/**
 * Berries are a food source and an extension of type Plant.
 * They grow and behave according to weather and time conditions
 * They may be poisonous. They cannot die of age, but can be eaten by animals
 *
 * @version 1 
 */

public class Berries extends Plant
{

    // The likelihood of a Berries breeding.
    private static final double BREEDING_PROBABILITY = 0.025;
    // The maximum number of shrubs.
    private static final int MAX_LITTER_SIZE = 2;
    // The age at which berries reproduce
    private static final int BREEDING_AGE = 5;
    // The probability a shrub is poisonous
    private static final double POISONOUS_PROBABILITY = 0.05;
    // The probability a shrub dies if frozen
    private static final double FROZEN_DEATH_PROBABILITY = 0.001;

    Random rand = new Random();
    
    // Individual characteristics of berries
    private int age;
    private boolean isPoisonous;

    /**
     * Create a new Berries. Berries are created with age
     * zero.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Berries(Field field, Location location)
    {
        super(field, location);
        if(rand.nextDouble() <= POISONOUS_PROBABILITY){
            isPoisonous = true;
        }
        else
            isPoisonous = false;
         
        age = 0;
    }

    /**
     * Berries grow in the rain and daytime. They might die in the snow.
     * 
     * @param newBerries: The new berries to be planted
     * @param isDay checks daytime
     * @param weather checks weather
     */
    public void grow(List<Plant> newBerries,boolean isDay, String weather)
    {
        age++;

        if(isAlive()) {             
            if(isDay && weather.equals("Rain")) {
                plantSeed(newBerries);
            }
            
            if (weather.equals("Snow")) {
                freeze();
            }
        }
    }

    
    /**
     * Check whether or not this Berries is to plant seeds at this step.
     * New Berries will be made into free adjacent locations.
     * @param newBerries A list to return newly born Berries.
     */
    private void plantSeed(List<Plant> newBerries)
    {
        // New Hares are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Berries young = new Berries(field, loc);
            newBerries.add(young);
        }
    }

    /**
     * Checks if this berries is poisonous
     * @return poison boolean of berries
     */
    public boolean isPoisonous()
    {
        return isPoisonous;
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
     * Berries can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * If berries freeze, they may die
     */
    private void freeze()
    {
        if(rand.nextDouble() <= FROZEN_DEATH_PROBABILITY){
            setDead();
        }
    }
}
