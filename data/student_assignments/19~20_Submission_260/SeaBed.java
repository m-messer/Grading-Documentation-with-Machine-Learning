import java.awt.Color;
import java.util.*;

/**
 * SeaBed Class - represents tiles of type Sea Bed.
 * @version 2020.02
 */
public class SeaBed extends NonPlantTile
{
    // the different color values that SeaBed can have 
    private static final Color COLOR1 = new Color(226, 224, 207);
    private static final Color COLOR2 = new Color(181, 196, 216);
    private static final Color COLOR3 = new Color(198, 210, 226);
    
    private static List<Color> colorArray = Arrays.asList(COLOR1, COLOR2, COLOR3);
    
    /**
     * Constructor for class Grass
     */
    public SeaBed()
    {
        
    }
    
    /**
     * Determine the color of the SeaBed Tile
     */
    protected Color getColor()
    {
        Collections.shuffle(colorArray);
        return colorArray.get(0);
    }
    
}
