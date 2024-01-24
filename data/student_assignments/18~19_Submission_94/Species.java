import java.util.List;

/**
 * A class representing shared characteristics of species including both animals and plants.
 *
 * @version 2019.02.21
 */
public class Species
{
    // Whether the species is alive or not.
    private boolean alive;
    // The species's field.
    private Field field;
    // The species's position in the field.
    private Location location;
   
    /**
     * Create a new species at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species(Field field, Location location)
    {
        alive = true;
        this.field = field;
        this.location = location;
        setLocation(location);
    }

    /**
     * Check whether the species is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the species is no longer alive.
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
     * Return the species's location.
     * @return The species's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the species at the new location in the given field.
     * @param newLocation The species's new location.
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
     * Return the species's field.
     * @return The species's field.
     */
    protected Field getField()
    {
        return field;
    }
    
}
