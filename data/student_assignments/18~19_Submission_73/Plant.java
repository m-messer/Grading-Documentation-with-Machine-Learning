import java.util.Random;
import java.util.List;
/**
 * A class representing plants
 *
 */
public class Plant
{
    // The rate at which a plant grows.
    private static final double GROWTH_RATE = 0.5;
    // The maximum length of a plant.
    private static final double MAX_LENGTH = 8.0;
    // The current length of a plant.
    private double length;
    // The plant's field.
    private Field field;
    // The plant's position in the field.
    private Location location;
    
    private static final Random rand = Randomizer.getRandom();
    
    
    /**
     * Constructor for objects of class Plant
     * @param field Field on which the plant grows
     * @param location Location at which the plant grows
     */
    public Plant(Field field, Location location)
    {
        length = rand.nextDouble() * 6;   
        this.field = field;
        setLocation(location);
    }

    /**
     * Place the plant at the given location
     * @param newLocation the location on which plant should be placed
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clearPlant(location);
        }
        location = newLocation;
        field.placePlant(this, newLocation);
    }

    /**
     * Increment the plant's length by the growth rate specified,
     * only if the plant hasn't reached max length.
     */
    public void grow()
    {
        // put your code here
        if (length < MAX_LENGTH)
            length += GROWTH_RATE;
    }

    /**
     * Decrement the plants length by the specified amount
     * after getting eaten.
     * @param amount eaten amount
     */
    protected void getEaten(int amount)
    {
        length -= amount;
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Return the plant's length.
     * @return the plant's length
     */
    protected double getLength(){
        return length;
    }
}
