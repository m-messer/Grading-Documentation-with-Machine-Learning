import java.awt.Color;
import java.util.*;

/**
 * Tarmac Class - represents tiles of type Tarmac (to be used for roads).
 * @version 2020.02
 */
public class Tarmac extends NonPlantTile
{
    // the different color values that Road can have 
    private static final Color COLOR1 = new Color(30, 30, 30);
    private static final Color COLOR2 = new Color(20, 20, 20);
    private static final Color COLOR3 = new Color(10, 10, 10);
    
    private static List<Color> colorArray = Arrays.asList(COLOR1, COLOR2, COLOR3);
    
    /**
     * Constructor for class Grass
     */
    public Tarmac()
    {
        
    }
    
    /**
     * Determine the color of the Road Tile
     */
    protected Color getColor()
    {
        Collections.shuffle(colorArray);
        return colorArray.get(0);
    }
    
}
