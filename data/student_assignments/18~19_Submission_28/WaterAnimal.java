import java.util.List;
import java.util.LinkedList;

/**
 * An extended class of Animal representing the characteristics of aquatic animals
 *
 * @version 20/02/2019
 */
public abstract class WaterAnimal extends Animal
{
    /**
     * Constructor for objects of class WaterAnimal
     * It will inherit the field, the location and the gender from the Animal class
     */
    public WaterAnimal(Field field, Location location, boolean gender)
    {
        super(field, location, gender);
    }
    
    /**
     * Get a shuffled list of the suitable free adjacent locations.
     * @param location Location of animal.
     * @return A list of free adjacent locations.
     */
    protected List<Location> getSuitableLocations(Location location)
    {
        List<Location> free = field.getFreeAdjacentLocations(location);
        
        List<Location> suitable = new LinkedList<>();
        for(Location next : free) {
            if (!field.getLand(next.getRow(), next.getCol()))
                suitable.add(next);
        }
        return suitable;
    }
    
}
