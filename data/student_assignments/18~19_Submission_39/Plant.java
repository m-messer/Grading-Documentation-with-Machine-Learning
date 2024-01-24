import java.util.List;
import java.util.Random;
/**
 * Write a description of class Plant here.
 *
 * @version (a version number or a date)
 */
 public abstract class Plant implements Aquatic
{
     // Whether the plant is alive or not.
    private boolean alive;
    // The plant's field.
    private Field field;
    // The plant's position in the field.
    private Location location;
    private int age;
    private Random rand = Randomizer.getRandom();
    
    
    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        age=0;
        alive = true;
        this.field = field;
        setLocation(location);
        
    }
    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born plants.
     */
     public abstract void act(List<Aquatic> newPlants);
     
    /**
     * Check whether the plant is alive or not.
     * @return true if the plant is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Indicate that the plant is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) 
        {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Increase the age.
     * This could result in the plant's death.
     */
    protected  void incrementAge()
    {
        age++;
        if(age > getMaxAge()) 
        {
            setDead();
        }
    }
    /**
     * Sets the max age of the plant.
     */
    abstract  int getMaxAge();
    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The plant's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) 
        {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    /**
     * Return the plant's field.
     * @return The plant's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) 
        {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births; 
    }
    /**
     * A molly can breed if it has reached the breeding age.
     * @return true if the molly can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= getBreedingAge();
    }
    /**
     * Sets the breeding age.
     */
    abstract protected int getBreedingAge();
    
    /**
     * Sets the breeding probability.
     */
    abstract double  getBreedingProbability();
    
    /**
     * Sets the max litter size.
     */
    abstract int getMaxLitterSize();
    
    /**
     * Returns the age of the plant.
     */
    public int getAge()
    {
        return age ;
    }
    /**
     * Sets the age.
     */
    public void setAge(int age)
    {
        this.age = age;
    }
    /**
     * Check whether or not this plant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPlants A list to return newly born plantss.
     */
    private void giveBirth(List<Aquatic> newPlants)
    {
        // New mollys are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            newPlants.add(getYoung(loc));
        }
    }
    /**
     * Return the young of this plant
      * @return The young of this plant
     */
    abstract protected Plant getYoung(Location loc);
}
