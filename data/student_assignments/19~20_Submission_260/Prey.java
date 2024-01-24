import java.util.*;

/**
 * A class representing shared characteristics of prey animals.
 *
 * @version 2020.02
 */
public abstract class Prey extends Animal
{
    /**
     * Create a new prey animal at location in field..
     * 
     * @param randomAge True if the animal is to be created with a random age, or false if not.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     */
    public Prey(boolean randomAge, Field field, Location location, int diseaseState)
    {
        super(randomAge, field, location, diseaseState);
    }
    
    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey animal is eaten.
     * 
     * @return A list of where food was found, or null if it wasn't.
     */
    protected List<Location> getAdjacentLocations()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation()); 
        return adjacent;
    }
}
