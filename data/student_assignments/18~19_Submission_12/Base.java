import java.util.Random;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Contains the methods that all the species have in commen, i.e the base methods.
 *
 */
public abstract class Base
{
    // The sex of the animal.
    protected boolean sexMale;
    // Whether the base is alive or not.
    protected boolean alive;
    // The bases's field.
    protected Field field;
    // The bases's position in the field.
    protected Location location;
    // The bases breding age.
    protected int BREEDING_AGE;
    //The bases max age.
    protected int MAX_AGE;
    //The breeding probability.
    protected double BREEDING_PROBABILITY;
    //The max litter size.
    protected int MAX_LITTER_SIZE;
    //The age of the base.
    protected int age;
    // A shared random number generator to control breeding.
    protected Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Base.
     */
    public Base(Field field, Location location)
    {
        alive = true;
        this.field = field;
    }

    /**
     * Check to see if the sex is male.
     */
    public boolean getSexMale() 
    {
        return this.sexMale;
    }

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
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
     * Indicate that the animal is no longer alive.
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
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Check whether or not the animal has met the condtions to give birth at this step. 
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    protected void giveBirth(List<Base> newBases)
    {
        // Get a list of adjacent free locations.
        // Then places the new born in the location.

        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0 && canBreed(); b++) {
            Location loc = free.remove(0);
            newBases.add(returnMyType(false, field, loc));

        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    abstract Base returnMyType(boolean randomAge, Field field, Location location);
    
    abstract void act(List<Base> newBases, Time time);
}
