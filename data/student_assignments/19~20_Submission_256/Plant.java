import java.util.List;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 2020.02.20
 */
public abstract class Plant extends Actor
{
    // age of the plant
    private int age;
    
    /**
     * Constructor for objects of class Plants
     */
    public Plant(boolean randomAge, int maxAge, Location location, Field field)
    {
       super(randomAge , maxAge, location, field);
    }
    
    /**
     ** This is what the plant does most of the time 
     * @param newCows A list to return new plants.
     */
    public abstract void act(List<Actor> newPlants);
    
    /**
     * @return the max age that the flower can live to
     */
    public abstract int getMaxAge();
}
