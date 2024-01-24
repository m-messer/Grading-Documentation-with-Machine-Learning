import java.util.*;
/**
 * Write a description of class SmallPlant here.
 *
 * @version 2020.02.23
 */
abstract public class SmallPlant extends Plant
{
    public SmallPlant(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    abstract public int getMaxAge();
    
    abstract public void act(List<Actor> plants);
}
