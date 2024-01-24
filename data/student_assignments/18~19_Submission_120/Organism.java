import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of organisms.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Organism
{
    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;
    // the organism's age.
    private int age;
    // the organism's gender. 0 for females and 1 for males.
    private int gender;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new organism at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Organism(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        age = 0;
        gender = 0; //default gender female
    }
    
    /**
     * Make this organism act - that is: make it do
     * whatever it wants/needs to do.
     * @param newOrganisms A list to receive newly born organisms.
     */
    abstract public void act(List<Organism> newOrganisms);
    
    /**
     * Return the organism's age.
     * @return The age.
     */
    protected int getAge()
    {
        return age;
    }
    
    /**
     * Set the organism's age.
     * @param age The new age.
     */
    protected void setAge(int age)
    {
        this.age = age;
    }
    
    /**
     * Return the organism's gender.
     * @return The gender.
     */
    protected int getGender()
    {
        return gender;
    }
    
    /**
     * Set the organism's gender.
     * @param gender The new gender.
     */
    protected void setGender(int gender)
    {
        this.gender = gender;
    }
    
    /**
     * Return the age to which an organism can live.
     * @return The organism's max age.
     */
    abstract protected int getMaxAge();
    
    /**
     * Increment the organism's age.
     */
    protected void incrementAge()
    {
        age++;
        if(getAge() > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Return the age at which an organism can breed.
     * @return The organism's breeding age.
     */
    abstract protected int getBreedingAge();
    
    /**
     * An organism can breed if it has reached the breeding age 
     * @return true if the organism can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }
    
    /**
     * Return the likelihood of an organism breeding.
     * @return The organism's breeding probability.
     */
    abstract protected double getBreedingProbability();
    
    /**
     * Return the maximum number of births for an organism.
     * @return The organism's maximum number of births.
     */
    abstract protected int getMaxLitterSize();
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }
    
    /**
    * Create a new organism. An organism may be created with age
    * zero (a new born) or with a random age.
    *
    * @param randomAge If true, the organism will have a random age.
    * @param field The field currently occupied.
    * @param location The location within the field.
    * @param randomGender If true, the cat will have random gender. 0 for females and 1 for males.
    */
    abstract protected Organism createOrganism(boolean randomAge, Field field, Location location, boolean randomGender); 
    
    /**
    * Check whether or not this organism is to give birth at this step.
    * New births will be made into free adjacent locations.
    * @param newborn A list to add newly born organisms to.
    */
    protected void giveBirth(List<Organism> newborn)
    {
        // New organisms are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newborn.add(createOrganism(false, field, loc, true));
        }
    } 
    
    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the organism is no longer alive.
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
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
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
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }
}
