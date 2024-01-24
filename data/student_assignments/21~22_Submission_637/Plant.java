import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.03.02
 */
public abstract class Plant
{
    private static final Random rand = Randomizer.getRandom();
    // Whether the plant  is alive or not.
    private boolean alive;
    // The plant 's field.
    private Field field;
    // The plant 's position in the field.
    private Location location;
    // The wether or not the plant  is able to reproduce (is the plant  female)
    
    /**
     * Create a new plant  at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
        public Plant(Field field, Location location)
        {
        alive = true;
        this.field = field;
        setLocation(location);
        
        Random rand = Randomizer.getRandom();
    }
    
    /**
     * Make this plant  act during day - that is: make it do
     * whatever it wants/needs to do.
     * @param newplants A list to receive newly born plants.
     */
    abstract public void actDay(List<Plant> newPlants);
    
    abstract public void actNight(List<Plant> newPlants);
    
    /**
     * The plant will spread after a certain number of steps
     */
    abstract public void spread(List<Plant> newPlants);  
    
    
    /**
     * Check whether the plant  is alive or not.
     * @return true if the plant  is still alive.
     */
     protected boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Indicate that the plant  is no longer alive.
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
     * Return the plant 's location.
     * @return The plant 's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the plant  at the new location in the given field.
     * @param newLocation The plant 's new location.
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
     * Return the plant 's field.
     * @return The plant 's field.
     */
    protected Field getField()
    {
        return field;
    }
}
