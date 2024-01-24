import java.awt.Color;
import java.util.Collections;

/**
 * Grass Class - represents tiles of type Grass.
 * @version 2020.02
 */
public class Grass extends PlantTile
{
    // the different color values that Grass can have
    private static final Color MIN_COLOR = new Color(229, 255, 204);
    private static final Color MIN_MED_COLOR = new Color(210, 242, 181);
    private static final Color MED_COLOR = new Color(161, 216, 137);
    private static final Color MED_MAX_COLOR = new Color(141, 196, 117);
    private static final Color MAX_COLOR = new Color(123, 179, 99);
    
    /**
     * Constructor for Class Grass.
     */
    public Grass()
    {
        
    }
    
    /**
     * @return the min color
     */
    public Color getMinColor()
    {
        return MIN_COLOR;
    }
    
    /**
     * @return the min med color
     */
    public Color getMinMedColor()
    {
        return MIN_MED_COLOR;
    }
    
    /**
     * @return the med color
     */
    public Color getMedColor()
    {
        return MED_COLOR;
    }
    
    /**
     * @return the med max color
     */
    public Color getMedMaxColor()
    {
        return MED_MAX_COLOR;
    }
    
    /**
     * @return the max color
     */
    public Color getMaxColor()
    {
        return MAX_COLOR;
    }
}
