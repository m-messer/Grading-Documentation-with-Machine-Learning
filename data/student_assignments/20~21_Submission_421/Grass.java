import java.util.Random;
import java.util.List;
/**
 * A simple model of the grass.
 * The grass can spread and be eaten by rabbits and deers.
 *
 * @version 2021.03.02
 */
public class Grass extends Plant 
{
    private static final double SPREADING_PROBABILITY = 1;
    
    private static final int MAX_AGE = 10;
    
    private static final Random rand = Randomizer.getRandom();
    
    private int grassAge;
    /**
     * Constructor for objects of class Grass
     */
    public Grass(Field field, Location location)
    {
        super(field,location);
       grassAge = 0;
    }
    
    /**
     * This method returns the current "age" of the plant.
     * @return grassLifeSpan;
     */
    protected int getPlantAge()
    {
        return grassAge;
    }
    
    /**
     * This method returns the maximum 'age'that a plant can reach.
     * @return int 
     */
    protected int getPlantMaxAge()
    {
        return MAX_AGE;
    }
}
