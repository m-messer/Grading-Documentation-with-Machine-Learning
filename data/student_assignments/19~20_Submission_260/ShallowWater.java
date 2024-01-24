import java.awt.Color;
/**
 * ShallowWater Class - represents tiles of type Shallow Water.
 * @version 2020.02
 */
public class ShallowWater extends PlantTile
{
    // the different color values that ShallowWater can have
    private static final Color MIN_COLOR = new Color(139, 196, 253);
    private static final Color MIN_MED_COLOR = new Color(130, 183, 236);
    private static final Color MED_COLOR = new Color(116, 165, 213);
    private static final Color MED_MAX_COLOR = new Color(113, 153, 199);
    private static final Color MAX_COLOR = new Color(102, 145, 188);
    /**
     * Constructor for class ShallowWater
     */
    public ShallowWater()
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
