import java.util.List;
import java.util.Random;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal implements Aquatic
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    private int age;
    private Random rand = Randomizer.getRandom();
    // Whether the animal have the disease or not.
    protected boolean diseased;
    // the gender of the animal.
    private char gender;
        
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        age = 0;
        alive = true;
        diseased = false;
        this.field = field;
        setLocation (location);
        if(rand.nextBoolean() == true)
        {
           gender = 'M';
        }
         else{ 
           gender = 'F';
        }
     }
    /**
     *  Returns the age of the animal.
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
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
     public abstract void act(List<Aquatic> newAnimals);
     
    /**
     * A molly can breed if it has reached the breeding age.
     * @return true if the molly can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= getBreedingAge();
    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Increase the age.
     * This could result in the animals's death.
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
     * Check whether the animal is diseased or not.
     * @return true if the animal is diseased.
     */
    public boolean isDiseased()
    {
        return diseased;
    }
    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    public void setDiseased()
    {
        diseased = true;
    }
    /**
     * Return whether or not the animal is diseased
     * @return whether or not the animal is diseased
     */
    public boolean diseased() 
    {
        return diseased;
    }
    /**
     * Set the whether or not the animal is diseased
     * @param newDisease Whether or not the animal is diseased
     */
    public void setDiseased(boolean newDisease) 
    {
        diseased = newDisease;
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
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMollys A list to return newly born animals.
     */
    private void giveBirth(List<Aquatic> newAnimals)
    {
        // New mollys are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            newAnimals.add(getYoung(loc));
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
        
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) 
        {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births; 
    }
    /**
     * Sets the breeding probability.
     */
    abstract double  getBreedingProbability();
    /**
     * Sets the max litter size.
     */
    abstract int getMaxLitterSize();
    /**
     * Sets the max age of the plant.
     */
    abstract  int getMaxAge();
    /**
     * Sets the breeding age.
     */
    abstract protected int getBreedingAge();
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
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
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    /**
     * Return the young of this animal
      * @return The young of this animal
     */
    abstract protected Animal getYoung(Location loc);
    /**
     * Return the animals gender
     * @return The animals gender
     */
    public char getGender() 
    {
        return gender;
    }
}
