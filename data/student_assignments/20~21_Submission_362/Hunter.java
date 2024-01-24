import java.util.List;
import java.util.Iterator;

/**
 * This class represents a hunter in the simulation whose only purpose is to hunt
 * and kill whales until they have reached their capacity.
 *
 * @version 27.02.21
 */
public class Hunter extends Organism
{
    // The maximum capacity of the hunter's net.
    private static final int CAPACITY = 10;

    // The number of whales already caught 
    private int net = 0;

    /**
     * When each hunter is created, it is allocated a location on the field.
     */
    public Hunter(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * This is what a hunter does. It tries to capture nearby whales.
     * The hunter's net has a capacity. When this capacity is 
     * reached or exceeded, the hunter is removed from the grid 
     * by setting it as dead.
     *
     */
    public void hunt()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Whale) {
                Whale whale = (Whale) animal;
                whale.setDead();
                net++;
                if(net >= CAPACITY){
                    setDead();
                }
            }
        }
    }
}

