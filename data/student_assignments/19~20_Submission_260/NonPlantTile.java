import java.awt.Color;
import java.util.*;
/**
 * Represents common features of all non-plant tiles.
 *
 * @version 2020.02
 */
public abstract class NonPlantTile extends Tile
{
    private Color color;
    
    public NonPlantTile() 
    {
        color = getColor();
    }
    
    
    /**
     * determine the color of tile buy randomly picking a color from a provided list
     * @return the 0th index of the shuffled colors list (effectively a random color)
     */
    protected Color determineColor()
    {
        return color;
    }
    
    /**
     * @return the list of color's a tile can have
     */
    protected abstract Color getColor();
}
