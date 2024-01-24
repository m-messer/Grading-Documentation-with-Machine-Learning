import java.awt.Color;
import java.util.Collections;
/**
 * DeepWater Class- represents tiles of type Deep Water.
 * @version 2020.02
 */
public class DeepWater extends PlantTile
{
    // the different color values that DeepWater can have
    private static final Color MIN_COLOR = new Color(102, 178, 255);
    private static final Color MIN_MED_COLOR = new Color(51, 153, 255);
    private static final Color MED_COLOR = new Color(0, 128, 255);
    private static final Color MED_MAX_COLOR = new Color(0, 102, 204);
    private static final Color MAX_COLOR = new Color(0, 76, 153);
    
    /**
     * Constructor for class DeepWater
     */
    public DeepWater()
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
