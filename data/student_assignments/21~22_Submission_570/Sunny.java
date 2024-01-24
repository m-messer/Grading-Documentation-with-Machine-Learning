import java.util.Random;

/**
 * A model of sunny weather. 
 * Rain increments the thirst of plants.
 *
 * @version 2022.02.27
 */
public class Sunny extends Weather 
{
    //Display string associated with sun
    private static final String displayString = "Sunny";  

    /**
     * Create sunny weather in field.
     * @param field The field currently occupied.
     */
    public Sunny(Field field)
    {  
        super(field);
    }

    /**
     * This is what  does.
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
                        plant.incrementThirst();
                    }
                }
            }
        }            
    }

    /**
     * Return display string for rain. 
     * @return The sunny word as a string
     */
    public String toString() 
    {
        return displayString; 
    }
}
