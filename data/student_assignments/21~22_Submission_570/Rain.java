import java.util.Random;
import java.util.List;

/**
 * A model of rain.
 * Rain increments the water level of plants.
 *
 * @version 2022.02.24
 */
public class Rain extends Weather
{
    //Display string associated with rain 
    private static final String displayString = "Rainy";    
    
    /**
     * Create rain in field.
     * @param field The field currently occupied.
     */
    public Rain(Field field)
    {  
        super(field);       
    }

    /**
     * This is what rain does.
     */
    public void act()
    {
        for(int row = 0; row < getField().getDepth(); row++) {
            for(int col = 0; col < getField().getWidth(); col++) {
                Location where = new Location(row, col);
                Object object = getField().getObjectAt(where);
                if(object instanceof Plant) {
                    Plant plant = (Plant) object;
                    if (plant.isAlive()) {
                        plant.incrementWaterLevel();
                    }
                }
            }
        }            
    } 

    /**
     * Return display string for rain. 
     * @return The rainy word as a string
     */
    public String toString() 
    {
        return displayString; 
    }
}
