import java.util.*;

/**
 * A class representing shared characteristics of predator animals.
 *
 * @version 2020.02
 */
public abstract class Predator extends Animal
{
    /**
     * Create a new predator animal at location in field..
     * 
     * @param randomAge True if the animal is to be created with a random age, or false if not.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     */
    public Predator(boolean randomAge, Field field, Location location, int diseaseState)
    {
        super(randomAge, field, location, diseaseState);
    }
    
    /**
     * Return a list of adjacent locations from which to look for food.
     */
    protected List<Location> getAdjacentLocations()
    {
        Field field = getField();
        
        int sight = 2;
        if (field.getWeather().isFog()) sight = Randomizer.getRandom().nextInt(2); // predator's sight will limited to 0 or 1
        
        List<Location> adjacent = field.adjacentLocations(getLocation(), sight);    
        return adjacent;
    }
}
