import java.awt.Color;
import java.util.*;

/**
 * Rock Class - represents tiles of type Rock.
 * @version 2020.02
 */
public class Rock extends NonPlantTile
{
    // the different color values that Rock can have 
    private static final Color COLOR1 = new Color(159, 159, 159);
    private static final Color COLOR2 = new Color(183, 183, 183);
    private static final Color COLOR3 = new Color(164, 164, 164);
    
    private static List<Color> colorArray = Arrays.asList(COLOR1, COLOR2, COLOR3);
    
    /**
     * Constructor for class Grass
     */
    public Rock()
    {
        
    }
    
    /**
     * Determine the color of the Rock Tile
     */
    protected Color getColor()
    {
        Collections.shuffle(colorArray);
        return colorArray.get(0);
    }
    
}
