import java.util.Random;
import java.util.List;

/**
 * A class representing shared characteristics of all organisms (either plant or animal).
 *
 * @version 2022.02.21
 */
public abstract class Organism
{
    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;

    /**
     * Create a new organism at location in field.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     */
    public Organism(Field field, Location location) 
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * This is what the organims do.
     * @param newOrganims A list containing all newly born animals and plants
     */
    abstract public void act(List<Organism> newOrganisms);

    /**
     * Return the age for a specific type of organism
     */
    abstract protected int getAge();

    /**
     * Check whether or not this organism is to reproduce at this step.
     * New organism will be made in free adjacent locations.
     * @param newOrganism A list to return newly born organism.
     */
    abstract protected void reproduce(List<Organism> newOrganism);

    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    public boolean isAlive()
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
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField() {
        return this.field;
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
     * Check whether it is day time in the given field 
     * @return True if day time, false if not.  
     */
    protected boolean dayTime() 
    {
        return field.getCurrentTime().equals(Time.DAY); 
    }
    
    /**
     * Check whether it is night time in the given field. 
     * @return True if night time, false if not.
     */
     protected boolean nightTime() 
    {
        return field.getCurrentTime().equals(Time.NIGHT); 
    }
}