package src;

import java.util.List;
import java.util.Random;

/**
 * Represents any object which needs to act on a given step for the simulation.
 * This includes animals, plants and anything else which should be implemented in the simulation.
 *
 * @version 2021.03.03
 */
abstract public class Actor {
    // Whether the actor is alive or not.
    private boolean alive;
    // The actor's field.
    private Field field;
    // The actor's position in the field.
    private Location location;
    // Provides a randomizer for some of the actor's methods.
    protected final static Random rand = Randomizer.getRandom();

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
     * An abstract method which needs to be implemented by any instance of Actor, called once every step of the simulation.
     * @param newActors The newborn actors to be added to the simulation
     */
    abstract public void act(List<Actor> newActors);

    /**
     * Check whether the actor is alive or not.
     * @return true if the actor is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the actor is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.remove(location,this);
            location = null;
            field = null;
        }
    }

    /**
     * Return the actor's location.
     * @return The actor's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the actor at the new location in the given field.
     * @param newLocation The actor's new location.
     */
    protected void setLocation(Location newLocation)
    {
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Move the actor to a new location (remove it from the old location)
     * @param newLocation the new location
     */
    protected void move(Location newLocation){
        field.remove(location,this);
        this.setLocation(newLocation);
    }

    /**
     * Return the actor's field.
     * @return The actor's field.
     */
    protected Field getField()
    {
        return field;
    }


}
