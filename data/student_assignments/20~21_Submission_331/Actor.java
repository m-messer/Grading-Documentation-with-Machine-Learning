import java.util.ArrayList;
import java.util.List;

/**
 * A class representing shared characteristics of actors.
 *
 * @version 2021.03.01
 */
public abstract class Actor
{
    // The actor's species.
    protected String species;
    // Whether the actor is alive or not.
    private boolean alive;
    // The actor's field.
    private Field field;
    // The actor's position in the field.
    private Location location;
    // The food sources that this actor can eat.
    protected ArrayList<String> foodSources;
    
    /**
     * Create a new actor at location in field.
     *
     * @param species The actor's species.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Actor(String species, Field field, Location location)
    {
        alive = true;  // All actors start alive.
        this.species = species;
        this.field = field;
        setLocation(location);
    }

    /**
     * Set the types of food source that this actor can eat.
     *
     * @param foodSources An array of food source names.
     */
    public void setFoodSources(ArrayList<String> foodSources) {
        this.foodSources = foodSources;
    }
    
    /**
     * Make this actor act - make it do whatever it wants to do.
     *
     * @param newActors A list to receive newly born actors.
     * @param time The current time.
     */
    abstract public void act(List<Actor> newActors, Time time);

    /**
     * Check whether the actor is alive or not.
     *
     * @return true if the actor is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the actor is no longer alive. It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if (location != null) {
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
     * Return the actor's species.
     *
     * @return The actor's species.
     */
    public String getSpecies()
    {
        return species;
    }
    
    /**
     * Place the actor at the new location in the given field.
     *
     * @param newLocation The actor's new location.
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

    /**e
     * Return the actor's base statistics text.
     *
     * @return The actor's formatted species.
     */
    protected String getStats() {
        return "Species: " + species;
    }
}
