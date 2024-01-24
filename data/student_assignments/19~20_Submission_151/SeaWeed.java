import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a seaWeed.
 * SeaWeeds age, move, breed, and die.
 *
 * @version 21.02.2020 (2)
 */
public class SeaWeed extends Plant
{
    // Characteristics shared by all seaWeeds (class variables).

    // The age at which a seaWeed can start to breed.
    private static final int BREEDING_AGE = 2;

    // The age to which a seaWeed can live.
    private static final int MAX_AGE = 10;

    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The seaWeed's age.
    private int age;

    // The seaweed's food level, which is increased by eating food.
    private int foodLevel;

    //calling the DayNight Class
    private DayNight dayNight;

    //calling the Weather Class
    private Weather weather;

    //calling the Location Class
    private Location staticLocation;
    /**
     * Create a new seaWeed. A seaWeed may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the seaWeed will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public SeaWeed(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        staticLocation = location;
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        dayNight = new DayNight();
        weather = new Weather();
    }

    /**
     * This is what the seaWeed does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newSeaWeeds A list to return newly born seaWeeds.
     */
    public void act(List<Plant> newSeaWeeds)
    {
        incrementAge();
        //incrementHunger();
        if(isAlive()) {
            giveBirth(newSeaWeeds);            
            if(staticLocation == null) {
                setLocation(staticLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Make this seaweed more hungry. This could result in the seaweed's death.
     */
    private void incrementHunger()
    {
        foodLevel--; //decreasing food level of fish by one
        //if the food level of fish is less than or equal to 0
        if(foodLevel <= 0) { 
            setDead(); //the fish will be dead
        }
    }

    /**
     * Increase the age.
     * This could result in the seaWeed's death.
     */
    private void incrementAge()
    {
        age++; //increment the age 
        //if the current age was grater than max age 
        if(age > MAX_AGE) {
            setDead(); //the fish will be dead
        }
    }

    /**
     * Check whether or not this seaWeed is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeaWeeds A list to return newly born seaWeeds.
     */
    private void giveBirth(List<Plant> newSeaWeeds)
    {
        // New seaWeeds are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            SeaWeed young = new SeaWeed(false, field, loc);
            newSeaWeeds.add(young);
        }
    }

    /**
     * Look for seaWeeds adjacent to the current location.
     * Only the first live seaWeed is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
        }
        return null;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed()) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A seaWeed can breed if it has reached the breeding age.
     * and it can only breed at day and when the weather is cloudy
     * @return true if the seaWeed can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return dayNight.isDay() && weather.isCloudy() && age >= BREEDING_AGE;
    }
}