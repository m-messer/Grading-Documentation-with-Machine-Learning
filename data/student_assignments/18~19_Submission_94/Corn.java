import java.util.List;
import java.util.Random;

/**
 * A simple model of corn.
 * Corn grow, breed, and die.
 *
 * @version 2019.02.21
 */
public class Corn extends Species {
    // Characteristics shared by all mice (class variables).
    
    // The likelihood of corn growing.
    private static final double GROWING_PROBABILITY = 0.2;
    // The likelihood of corn growing when the weather is changed.
    private static double NEW_GROWING_PROBABILITY;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The age of corn.
    private static int life = 0;
    // The age to which corn can live.
    private static final int MAX_LIFE = 2;

    // A shared random number generator to control growing.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create new corn.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Corn(Field field, Location location) 
    {
        super(field, location);
    }
    
    /**
     * This is what corn does most of the time - it grows and breed.
     * When it grows enough, it dies.
     * 
     * @param newCorn A list to return newly born corn.
     * @param weather The weather corn is in.
     */
    public void act(List<Corn> newCorn, String weather)
    {
        incrementLifeTime();
        if(isAlive()) {
            weatherEffect(weather);
            giveBirth(newCorn);
        }
    }

    /**
     * Check whether or not this corn is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newCorn A list to return newly born corn.
     */
    public void giveBirth(List<Corn> newCorn) 
    {
        // New corn are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = null;
        int births = breed();
        if (null != field) {
            free = field.getFreeAdjacentLocations(getLocation());
        }
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Corn corn = new Corn(field, loc);
            newCorn.add(corn);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    public int breed() 
    {
        int births = 0;
        if (rand.nextDouble() <= NEW_GROWING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * According to the weather, corn get effected on the breeding probability.
     * 
     * @param weather The string of weather.
     */
    public void weatherEffect (String weather)
    {
        if (weather.equals("Sunny"))
        {
            NEW_GROWING_PROBABILITY = GROWING_PROBABILITY;
        }
        if (weather.equals("Cloudy"))
        {
            NEW_GROWING_PROBABILITY = GROWING_PROBABILITY * 0.8;
        }
        if (weather.equals("Rainy"))
        {
            NEW_GROWING_PROBABILITY = GROWING_PROBABILITY * 1.1;
        }
        if (weather.equals("Windy"))
        {
            NEW_GROWING_PROBABILITY = GROWING_PROBABILITY * 0.9;
        }
    }
    
    /**
     * Increase the age of corn.
     */
    private void incrementLifeTime()
    {
        life++;
        if(life > MAX_LIFE) {
            setDead();
        }
    }
}
