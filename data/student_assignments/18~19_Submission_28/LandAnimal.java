import java.util.List;
import java.util.LinkedList;

/**
 * Blueprint for a terrestrial animal.
 *
 * @version 22/02/2019
 */
public abstract class LandAnimal extends Animal
{
    /**
     * Constructor for objects of class LandAnimal
     */
    public LandAnimal(Field field, Location location, boolean gender)
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
            if (field.getLand(next.getRow(), next.getCol()))
                suitable.add(next);
        }
        return suitable;
    }
    
}
