import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * This class contains the unique methods of the plants
 *
 */
public abstract class Plants extends Base
{

    /**
     * Constructor for objects of class Plants
     */
    public Plants(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        alive = true;
        this.field = field;
        setLocation(location);

    }

    abstract Base returnMyType(boolean randomAge, Field field, Location location);

    /**
     * This is what the plant does,- 
     * Sometimes it will breed or die of old age.
     * @param newPlant A list to return new plant.
     */
    public void act(List<Base> newPlants, Time time)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newPlants);
        }

    }
}
