import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // Whether the animal is infected or not.
    private boolean infected;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    //The animals gender 
    private String gender; 
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        // random variable of infection the probability is one out of ten (only if 0 
        // in range of 0 - 9) 
        Random randomInfectionNumber = new Random();
        int randomIfInfected = randomInfectionNumber.nextInt(10);
        if(randomIfInfected == 0) {
            infected = true;
        } else {
            infected = false;
        }
        this.field = field;
        setLocation(location);
        //random decider of a gender, because the range consists only of two numbers the probability of
        //each gender is the same = 0.5
        Random randomNumber = new Random();
        int randomGender = randomNumber.nextInt(2);
        if(randomGender == 0) {
            gender = "Male";
        } else {
            gender = "Female";
        }
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);
    
    /**
     * Check the gender of an animal.
     * @return gender 
     */
    protected String getGender()
    {
        return gender;
    }
    
    /**
     * Check if infected 
     * @return infected 
     */
    protected boolean isInfected()
    {
        return infected;
    }
    
    /**
     * The animal gets infected if it is not 
     */
    protected void setInfected()
    {
        if(!infected) {
            infected = true;
        }
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
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
}
