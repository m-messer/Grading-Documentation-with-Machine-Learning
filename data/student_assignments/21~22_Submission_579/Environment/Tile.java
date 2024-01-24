package Environment;

import java.util.ArrayList;
import java.util.Iterator;

import Entities.Entity;
import Entities.EntityType;

/**
 * An object of the Tile class represents a location within a particular habitat. A tile
 * has a number of properties, such as saturation and elevation, and can store any number
 * of entities
 *
 * @version 2022.02.08
 */
public class Tile
{
    // The parent habitat
    private Habitat habitat;
    // Row and column positions.
    private int row;
    private int col;
    
    // The height of the tile (in meters)
    private double elevation;
    // The water level of the 
    private double waterLevel;
    private float saturation;
    // Storage for entities
    private ArrayList<Entity> entities;

    /**
     * Represent a row and column.
     * @param row The row.
     * @param col The column.
     */
    public Tile(Habitat habitat, int row, int col)
    {
        this.habitat = habitat;
        this.row = row;
        this.col = col;
        entities = new ArrayList<Entity>();
    }
    
    /**
     * Gets all of the tiles of a habitat within a square of a given side length whose
     * center is this tile. This effectively gets all the tiles within a given
     * distance of this tile. The ordering is a spiral-like pattern from the origin.
    * @param distance the side length of the square
     * @return An array containing all tiles within that square.
     */
    public Tile[] getTilesWithinDistance(int radius) {
        return habitat.getTilesWithinDistance(this, radius);
    }
    
    /**
     * Gets all of the tiles on the perimeter of a square of a given side length whose
     * center is a given tile within this habitat. This effectively gets all the tiles at
     * a given distance from the origin. The ordering is a spiral-like pattern from the
     * origin.
     * 
     * @param distance the side length of the square
     * @return An array containing all tiles that are on the perimeter of that square
     */
    public Tile[] getTilesAtDistance(int radius) {
        return habitat.getTilesAtDistance(this, radius);
    }
    
    /**
     * Search for, and return, an entity of a certain type on a tile. 
     * If the entity does not exist, return null.
     * @param type      the type of the entity.
     */
    public Entity searchForEntity(EntityType type) {
        for(Entity entity : entities) {
            if(entity.getType() == type) {
                return entity;
            }
        }
        return null;
    }
    
    /**
     * Add a creature to the tile
     * @param creature The creature to be added
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }
    
    /**
     * Attempt to remove a creature from the tile
     * @param creature the creature to be removed
     * @return true if the operation was successful, otherwise false
     */
    public boolean removeEntity(Entity entity) {
        return entities.remove(entity);
    }
    
    /**
     * @return the creatures within this tile
     */
    public ArrayList<Entity> getEntities()
    {
        return entities;
    }
    
    /**
     * Attempt to "spread" water on one tile into its 4 surrounding tiles in each cardinal direction.
     * If a surrounding tile has lower elevation than the original, water can spread into it.
     */
    public void spreadWater() {
        // Find the tile with the lowest overall level
        Tile lowestTile = this;
        double lowestLevel = this.getElevationPlusWater();
        for(Tile tile : habitat.getTilesAtDistance(this, 1)) {
            double level = tile.getElevationPlusWater();
            if(level < lowestLevel) {
                lowestTile = tile;
                lowestLevel = level;
            }
        }
        
        double average = (this.getElevationPlusWater() + lowestLevel)/2;
        double desiredAmountToGive = average - lowestLevel;
        double amountToGive = Math.min(waterLevel,desiredAmountToGive);
        
        addWaterLevel(-amountToGive);
        lowestTile.addWaterLevel(amountToGive);
    }
    
    /**
     * @return the elevation value of the tile
     */
    public double getElevation() {
        return elevation;
    }
    
    /**
     * Assign an elevation value to the tile
     * @param elevation     elevation of tile
     */
    public void setElevation(double elevation) {
        this.elevation = elevation;
    }
    
    /**
     * @return the waterLevel value of tile 
     */
    public double getWaterLevel() {
        return waterLevel;
    }
    
    /**
     * Add to the waterLevel value, then set waterLevel to be either 0 or the total water level of tile (whichever is larger)
     * @param waterLevel        the value to be added (double)
     */
    public void addWaterLevel(double waterLevel) {
        this.waterLevel = Math.max(0,this.waterLevel + waterLevel);
    }
    
    /**
     * @return the saturation value of the tile
     */ 
    public float getSaturation() {
        return saturation;
    }
    
    /**
     * Assign a saturation floating point value to the tile.
     * @param saturation        saturation value of tile
     */
    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }
    
    /**
     * @return elevation of tile + water level of tile
     */
    public double getElevationPlusWater() {
        return elevation + waterLevel;
    }
    
    /**
     * @return The habitat containing this tile
     */
    public Habitat getHabitat()
    {
        return habitat;
    }
    
    /**
     * @return The row that this tile is in.
     */
    public int getRow()
    {
        return row;
    }
    
    /**
     * @return The column that this tile is in.
     */
    public int getCol()
    {
        return col;
    }
    
    /**
     * Implement content equality.
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Tile) {
            Tile other = (Tile) obj;
            return row == other.getRow() && col == other.getCol();
        }
        else {
            return false;
        }
    }
    
    /**
     * Use the top 16 bits for the row value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (row, col) pair.
     * @return A hashcode for the location.
     */
    public int hashCode()
    {
        return (row << 16) + col;
    }
    
    /**
     * Return a string of the form row,column
     * @return A string representation of the location.
     */
    public String toString()
    {
        return row + "," + col;
    }
}
