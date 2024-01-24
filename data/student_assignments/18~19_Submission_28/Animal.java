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
    protected boolean alive;
    // The animal's field.
    protected Field field;
    // The animal's position in the field.
    protected Location location;
    // Gender: true if male, false if female.
    protected boolean isMale;
    // Checks whether animals is sleeping.
    protected boolean sleeping;
    // Checks whether animal is healthy.
    protected boolean healthy;
    
    // Probability that an animal gets sick.
    protected static final double ANIMAL_GET_SICK_PROBABILITY = 0.01;
    // Probability that an animal spreads its disease.
    protected static final double DISEASE_SPREAD_PROBABILITY = 0.05;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location, boolean gender)
    {
        alive = true;
        this.field = field;
        this.isMale = gender;
        healthy = true;
        setLocation(location);
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);
    
    /**
     * Look for squirrels, kingfishers, or salmon adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected abstract Location findFood();

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Check whether the animal is healthy or not.
     * @return true if the animal is still healthy.
     */
    protected boolean isHealthy()
    {
        return healthy;
    }
    
    /**
     * Makes animal sick.
     */
    protected void makeSick()
    {
        healthy = false;
    }
    
    /**
     * Get a shuffled list of the suitable free adjacent locations.
     * @param location Location of animal.
     * @return A list of free adjacent locations.
     */
    protected List<Location> getSuitableLocations(Location location)
    {
        return field.getFreeAdjacentLocations(location);
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
    
    public void setAsleep()
    {   
        Random rand = new Random();
        double random = rand.nextDouble();
        if(random < 0.055){
            if(Time.getTime() > 175 && Time.getTime() < 325){
                sleeping = true;
            } else {
                sleeping = false;
            }
        } 
    }
    
    public boolean getAsleep() 
    {
        return sleeping;
    }
    
    /*
     * Returns a boolean representing whether it is a male or not
     */
    protected boolean getGender()
    {
        return isMale;
    }
}
