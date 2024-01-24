import java.util.List;
/**
 * A simple model of a grass.
 * Provide the data for the plant class.
 *
 * @version 2022.03.01 (15)
 */
public class StarGrass extends Plant
{
    private static final int FOOD_VALUE = 30;
    private static final int MAX_AGE = 100;
    private static final int MAX_SUNLEVEL = 40;
    private static final int MAX_WATERLEVEL = 50;
    private static final double BREEDING_PROBABILITY = 1;
    private static final int MAX_LITTER_SIZE = 4;
    /**
     * Create a grass. A grass can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the grass will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public StarGrass(boolean randomAge, Field field, Location location,DateTime dateTime)
    {
        super(field, location,dateTime);
        setData(FOOD_VALUE, MAX_AGE, MAX_SUNLEVEL, MAX_WATERLEVEL, MAX_LITTER_SIZE, BREEDING_PROBABILITY);
        if(randomAge) {
            setRandomAge();
        }
    }
}
