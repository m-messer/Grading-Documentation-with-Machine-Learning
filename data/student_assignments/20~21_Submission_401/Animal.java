import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 3
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;

    // Whether or not this animal is a male.
    private boolean male;
    // Whether or not an animal is poisoned.
    private boolean poisoned;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);

        Random rand = new Random();
        if(rand.nextInt(2) == 1)
            male = false;
        else
            male = true;
    }
    
    /**
     * Checks if an animal is male
     * @return boolean of male
     */
    public boolean isMale()
    {
        return male;
    }
 
    /**
     * Checks if an animal is the same gender
     * @param the gender of the animal in comparison
     * @return boolean of same gender
     */
    public boolean isSameGender(boolean man)
    {
        return male == man ;
    }

    /**
     * Checks if an animal is poison
     * @return Poison status
     */
    public boolean isPoisoned()
    {
        return poisoned;
    }

    /**
     * Sets an animal to be poisoned
     * @param the poison boolean.
     */
    public void setPoisoned(boolean poison)
    {
        poisoned = poison;
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract void act(List<Animal> newAnimals, boolean isDay);

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
