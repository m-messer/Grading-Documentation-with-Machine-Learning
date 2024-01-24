import java.util.List;
import java.util.Random;
/**
 * A class representing shared characteristics of Actors
 *
 * @version 2022.03.01 (15)
 */
public abstract class ActingThing 
{
    // The actor's field.
    private Field field;
    //The current date and time of the simulation
    private DateTime dateTime;
    // The actor's position in the field.
    private Location location;
    // a shared randomizer for all actors.
    protected static final Random rand = Randomizer.getRandom();
    /**
     * Create a new actor at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param dateTime The current date and time of the simulation.
     */
    public ActingThing(Field field, Location location,DateTime dateTime)
    {
        this.field = field;
        setLocation(location);
        this.dateTime =dateTime;
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
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    /**
     * Remove the actor
     */
    protected void remove()
    {
        location = null;
        field = null;
    }
    /**
     * place the actor to a new location.
     * Update the location in the field aswell.
     */
    protected void setLocation(Location newLocation)
    {
        location = newLocation;
        field.place(this, newLocation);

    }

    /**
     * @return the current date and time
     */
    protected DateTime getDateTime()
    {
        return dateTime;
    }
    
    /**
     * @return ture if the actor is still in the field.
     */
    protected boolean canAct()
    {
        return field!=null;
    }
    
    /**
     * Every actors have its own action that will be determined in the subclass.
     */
    public abstract void act(List<LivingThing> things)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException;
}
