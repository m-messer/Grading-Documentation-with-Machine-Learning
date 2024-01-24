import java.awt.Color;
/**
 * Represents common features of all plant tiles.
 *
 * @version 2020.02.09
 */
public abstract class PlantTile extends Tile
{
    // store the amount of plant at the current Grass Tile
    private Plant plant;
    // 
    private static final int minCount = 200;
    private static final int minMedCount = 400;
    private static final int medCount = 600;
    private static final int medMaxCount = 800;
    private static final int maxCount = 1000;
   
    /**
     * Constructor for objects of class Grass
     */
    public PlantTile()
    {
        // super method can be omitted (i.e. Super())
        plant = new Plant();
    }
    
    /**
     * Get the amount of plant at this tile (between 0 and 1) as determined by the Plant class
     */
    public int getPlantCount()
    {
        return plant.getCount();
        
    }
    
    /**
     * @return the min value (plant tile with least amount of plant)
     */
    protected int getMinCount()
    {
        return minCount;
    }
    
    /**
     * @return the min-med value
     */
    protected int getMinMedCount()
    {
        return minMedCount;
    }
    
    /**
     * @return the medium value (plant tile with middle amount of plant)
     */
    protected int getMedCount()
    {
        return medCount;
    }
    
    /**
     * @return the medium value (plant tile with middle amount of plant)
     */
    protected int getMedMaxCount()
    {
        return medMaxCount;
    }
    
    /**
     * @return the max value (plant tile with maximum amount of plant)
     */
    protected int getMaxCount()
    {
        return maxCount;
    }
 
    /**
     * Checks for an animal located at tile and returns it, otherwise checks for a plant located at tile and returns it.
     * @return Organism at tile, prioritising animal, or null.
     */
    public Actor getOrganism()
    {
        if(getAnimal() != null) {
            return getAnimal();
        }
        else {
            return plant;
        }
    }
    
    /**
     * @return the plant at this PlantTile
       */
    public Plant getPlant()
    {
        return plant;
    }
    
    /**
     * Determine the color of the PlantTile
     */
    protected Color determineColor()
    {
        int count = getPlantCount();
        if(count < getMinCount()) return getMinColor();
        else if(count < getMinMedCount()) return getMinMedColor();
        else if(count < getMedCount()) return getMedColor();
        else if(count < getMedMaxCount()) return getMedMaxColor();
        else return getMaxColor();
    }
    
    /**
     * @return the min color
     */
    public abstract Color getMinColor();
    
    /**
     * @return the min color
     */
    public abstract Color getMinMedColor();
    
    /**
     * @return the min color
     */
    public abstract Color getMedColor();
    
    /**
     * @return the min color
     */
    public abstract Color getMedMaxColor();
    
    /**
     * @return the min color
     */
    public abstract Color getMaxColor();
}
