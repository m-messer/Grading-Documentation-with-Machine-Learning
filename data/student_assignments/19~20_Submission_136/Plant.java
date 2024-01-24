import java.util.*;

/**
 * A abstract class representing shared characteristics of plants.
 *
 * @version 2020.02.23
 */
abstract public class Plant implements Actor
{
    // A shared random number generator to control age and spread probability.
    private static final Random rand = Randomizer.getRandom();
    //The plants age
    private int age;
    //The plant's position in the field.
    private Location location;
    //The plant's field.
    private Field field;
    //Whether the plant is alive or not.
    private boolean alive;
    
    /**
     * Create a new plant at location in field.
     * 
     * @param randomAge if True starts with random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        if(randomAge)
        {
            age = rand.nextInt(getMaxAge());
        }
        else
        {
            age = 0;
        }
    }
    
    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Increase the age. This could result in the plant's death.
     */
    public void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Check whether the plant is alive or not.
     * 
     * @return true if the plant is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Get current age of the plant
     * @return current age of plant
     */
    public int getAge()
    {
        return age;
    }
    
    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Return the plant's field.
     * @return The plant's field.
     */
    public Field getField()
    {
        return field;
    }

    /**
     * Generate a number representing the number of seeds,
     * if it can spread.
     * @return The number of new seeds (may be zero).
     */
    public int spread()
    {
        int seeds = 0;
        if(canSpread() && (rand.nextDouble() <= getSpreadingProbability())) {
            seeds = rand.nextInt(getMaxSeeds()) + 1;
        }
        return seeds;
    }

    /**
     * Check whether or not this plant can spread new plants at this step.
     * New seeds will be made into free adjacent locations.
     * @param newPlants A list to return newly born plants.
     */
    protected void doSpread(List<Actor> newPlants)
    {
        // New plants grow into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int seeds = spread();
        for(int b = 0; b < seeds && free.size() > 0; b++) {
            Location loc = free.remove(0);
            if(this instanceof Bush) {
                Bush young = new Bush(false, field, loc);
                newPlants.add(young);
            }
            else if(this instanceof Acacia) {
                Acacia young = new Acacia(false, field, loc);
                newPlants.add(young);
            }
        }
    }
    
    /**
     * A plant can spread if it has reached the spreading age
     * return True if the plant is old enough to spread
     */
    protected boolean canSpread()
    {
        return getAge() > getSpreadingAge();
    }
    
    /**
     * Abstract method - makes the actions that carry all types
     * of plants, although the trees and bushes do some actions
     * differently
     */
    abstract public void act(List<Actor> plants);

    /**
     * Abstract method - retrieves the maximum age of the 
     * type of plant
     */
    abstract public int getMaxAge();
    
    /**
     * Abstract method - retrieves the spreading age of the
     * type of plant
     */
    abstract public int getSpreadingAge();
    
    /**
     * Abstract method - retrieves the max seeds that the
     * plants can spread
     */
    abstract public int getMaxSeeds();
    
    /**
     * Abstract method - retrieves the probability of the 
     * type of plant
     */
    abstract public double getSpreadingProbability();
}