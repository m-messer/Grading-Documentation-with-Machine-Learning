import java.util.Random;
import java.util.List;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 2022.02.21
 */
public abstract class Plant extends Organism
{
    /**
     * Create a new plant at location in field.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
    }
    
    /**
     * Make this plant more thirsty. This could result in the plant's death
     * due to wilting.
     */
    abstract public void incrementThirst();
    
    /**
     * Increment the water level of this plant.
     */
    abstract public void incrementWaterLevel();
}
    

