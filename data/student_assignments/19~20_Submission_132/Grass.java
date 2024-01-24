import java.util.List;
import java.util.Iterator;
/**
 * A simple model of a Grass.
 * Grass is eaten, regrows and dies.
 *
 * @version 2016.02.29 (2)
 */
public class Grass extends Plant
{
    // The age to which a Grass can live.
    private static final int MAX_AGE = 10000;
    //The bnutrition value Grass provides to the consumer
    private static final int FOOD_VALUE = 300;
    // Time beofore Grass regrows and is edible again 
    private static final int REGROWTH_PERIOD = 10;

    /**
     * Create a new Grass. A Grass may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Grass will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(randomAge,field, location);
    }

    /**
     * This is what the Grass does most of the time - it grows
     * and ages
     * @param isRaining if it is raining or not, if true, grass grows 2
     * levels
     */
    public void actDay(boolean isRaining)
    {
        if(isAlive()){
            lifeProcesses();
        }
        if(isRaining)
        {
            growthLevel++;
        }
    }

    /**
     * This is what the Grass does most of the time - it grows
     * and ages
     * @param isRaining if it is raining or not, if true, grass grows 2
     * levels
     * */
    public void actNight(boolean isRaining)
    {
        if(isAlive()){
            lifeProcesses();
        }
        if(isRaining)
        {
            growthLevel++;
        }
    }
 
    /**
     * @return Food value Grass provides 
     */
    public int getFoodValue()
    {
            return FOOD_VALUE;
    }
    
    /**
     * @return Time before Grass regrows and is edible
     */
    public int getRegrowthPeriod()
    {
        return REGROWTH_PERIOD;
    }

    /**
     * @return Max age of Grass
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
}
