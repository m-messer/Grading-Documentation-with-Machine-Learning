import java.awt.Color;
import java.util.*;

/**
 * Sand Class - represents tiles of type Sand.
 * @version 2020.02
 */
public class Sand extends NonPlantTile
{
    // the different color values that Sand can have
    private static final Color COLOR1 = new Color(249, 240, 212);
    private static final Color COLOR2 = new Color(236, 227, 198);
    private static final Color COLOR3 = new Color(253, 248, 229);
    
    private static List<Color> colorArray = Arrays.asList(COLOR1, COLOR2, COLOR3);
    
    /**
     * Constructor for class Sand
     */
    public Sand()
    {
        
    }
    
    /**
     * Determine the color of the Sand Tile
     */
    protected Color getColor()
    {
        Collections.shuffle(colorArray);
        return colorArray.get(0);
    }
 
}
