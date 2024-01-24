import java.util.List;
import java.util.Random;

/**
 * A simple model of water.
 * Water appear randomly in the field every day.
 *
 * @version 2019.02.21
 */
public class Water extends Species 
{
    // The dirty level of the water.
    private static int dirtyLevel  = 0;
    // The maximum dirty level of the water.
    private static final int MAX_DIRTY_LEVEL = 2;

    /**
     * Create new water.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Water(Field field, Location location) 
    {
        super(field, location);
    }
    
    /**
     * This is what water do most of the time - it appears and gets dirty.
     * if it left over some days, it rots.
     * 
     * @param newWater A list to return newly born corn.
     */
    public void act(List<Water> newWater)
    {
        turnDirty();
    }

    /**
     * The water get dirty. If it left over some days, it rots(dies).
     */
    private void turnDirty()
    {
        dirtyLevel++;
        if(dirtyLevel > MAX_DIRTY_LEVEL){
            setDead();
        }
    }
}
