/**
 * A class representing the shared characteristics of all the 
 * actors in the simulation.
 *
 * @version 2020.02.22
 */
public abstract class Actor
{
    // To check whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;

    /**
     * Create a new actor at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Actor(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Check whether the actor is alive or not.
     * 
     * @return Returns true if the actor is alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicates that the actor is no longer alive.
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
     * Return the actor's location.
     * 
     * @return The actor's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the actor at the new location in the given field.
     * 
     * @param newLocation   The actor's new location.
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
     * Return the actor's field.
     * 
     * @return The actor's field.
     */
    protected Field getField()
    {
        return field;
    }
}
