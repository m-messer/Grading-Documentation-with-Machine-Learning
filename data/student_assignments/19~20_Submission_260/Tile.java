import java.awt.Color;
import java.util.Collections;
import java.util.List;

/**
 * An interface for all possible tiles, such as grass tiles, rock tiles,
 * snow tiles, water tiles, and many more.
 *
 * @version 2020.02
 */
public abstract class Tile
{
    private Actor animal;
    
    /**
     * @return the color of tile
     */
    protected abstract Color determineColor();
    
    /**
     * Set the tile's current animal to null.
     */
    protected void clearAnimal()
    {
        this.animal = null;
    }
    
    /**
     * Set the tile's current animal.
     */
    protected void setAnimal(Actor animal)
    {
        this.animal = animal;
    }
    
    /**
     * @return the tile's current animal
     */
    protected Actor getAnimal()
    {
        return animal;
    }

    /**
     * Checks for an animal located at tile and returns it, otherwise checks for a plant located at tile and returns it.
     * @return Organism at tile, prioritising animal, or null.
     */
    public Actor getOrganism()
    {
        return animal;
    }
    
    /**
     * @return the tile's current plant (null), which will be overwritten ONLY within PlantTile class
     */
    protected Plant getPlant()
    {
        return null;
    }
}

