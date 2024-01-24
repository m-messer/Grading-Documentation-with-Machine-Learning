import java.util.List;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.02.27 (4)
 */
public abstract class Species
{
    // Whether the animal is alive or not.
    private boolean alive = true;
    // The species's field.
    private Field field;
    // The species's position in the field.
    private Location location;
    
    // The species's gender in boolean form.
    public boolean genderIsMale;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species(Field field, Location location, boolean genderIsMale)
    {
        this.genderIsMale = genderIsMale;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Species> newSpecies, Clock clock);

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
    
    /**
     * Return the animal's gender, in boolean form.
     * True meaning that the animal is a male.
     */
    protected boolean getGenderIsMale()
    {
        return(genderIsMale);
    }
}
