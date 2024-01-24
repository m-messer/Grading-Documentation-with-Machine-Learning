import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

/**
 * A class representing shared characteristics of weather.
 *
 * @version 2022.02.26
 */
public abstract class Weather
{
    //the weather's field.
    private Field field;
    
    /**
     * Create a weather in field.
     * @param field The field currently occupied.
     */
    public Weather (Field field)
    {
        this.field = field; 
    }

    /**
     * This is what the weather do.
     */
    abstract public void act();

    /**
     * Return the corresponding display string for each weather.
     */
    abstract public String toString(); 

    /**
     * Return the weather's field.
     * @return The weather's field.
     */
    protected Field getField() 
    {
        return field;
    }
}
