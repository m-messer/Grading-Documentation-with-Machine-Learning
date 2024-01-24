import java.util.List;
/**
 *A class representing shared characteristics of animals.
 *
 * @version 2020.2.22
 */
public abstract class Plant extends Actor
{
    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
    }
    
    /**
     * Make this plants act - that is: make it do
     * whatever it wants/needs to do.
     * @param newPlant A list to receive newly born animals.
     */
    abstract public void act(List<Plant> newPlant);
}

