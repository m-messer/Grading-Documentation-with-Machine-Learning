import java.awt.Color;
import java.util.*;

/**
 * Snow Class - represents tiles of type Snow.
 * @version 2020.02
 */
public class Snow extends NonPlantTile
{
    // the different color values that Snow can have 
    private static final Color COLOR1 = new Color(221, 255, 255);
    private static final Color COLOR2 = new Color(232, 252, 252);
    private static final Color COLOR3 = new Color(240, 255, 255);
    
    private static List<Color> colorArray = Arrays.asList(COLOR1, COLOR2, COLOR3);
    
    /**
     * Constructor for class Grass
     */
    public Snow()
    {
        
    }
    
    /**
     * Determine the color of the Snow Tile
     */
    protected Color getColor()
    {
        Collections.shuffle(colorArray);
        return colorArray.get(0);
    }
    
}
