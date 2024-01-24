package Environment;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javafx.util.Pair;

import Entities.Entity;
import Entities.Creature;
import Entities.Fish;
import Entities.CreatureActivity;
import Entities.EntityType;

/**
 * A Habitat represents an area where animals and plantlife can interact. A habitat should
 * always be generated using the HabitatGenerator class
 *
 * @version 2022.02.08
 */
public class Habitat
{
    // The width and height of the habitat in tiles
    private int width, height;
    private int secondsPerStep;
    private int metersPerTile;
    
    // Randomisation variables
    long seed;
    private Random randomiser;
    
    // Storage for the tiles.
    private Tile[][] tileGrid;
    // The maximum and mininum elevations (in meters)
    private double maxElevation;
    private double minElevation;
    // The maximum and minimum water levels (in meters)
    private double maxWaterLevel;
    private double minWaterLevel;
    // The maximum and minimum saturations
    private float maxSaturation;
    private float minSaturation;
    
    // Map of clouds in the area
    private CloudMap cloudMap;
    // The number of seconds since the world was created
    private long secondsSinceStart;
    
    
    /**
     * Create a habiat based on the given parameters.
     * @param randomiser The randomiser that determines behaviour within the habitat
     * @param width The horizondal length of the habiat.
     * @param height The vertical length of the habiat.
     * @param secondsPerStep The number of seconds that each step represents
     * @param metersPerTile The number of meters that each tile represents
     */
    public Habitat(long seed, int width, int height, int secondsPerStep, int metersPerTile)
    {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.secondsPerStep = secondsPerStep;
        this.metersPerTile = metersPerTile;
    }
    
    /**
     * Reset the habitat to its initial conditions
     */
    public void reset()
    {
        // Reset the randomiser
        randomiser = new Random(seed);
        
        // Initialise the tile array
        tileGrid = new Tile[height][width];
        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                Tile tile = new Tile(this,row,col);
                tileGrid[row][col] = tile;
            }
        }
        
        cloudMap = null;
        
        // Initialise the elevations
        maxElevation = 0;
        minElevation = 0;
    }
    
    /**
     * Perform one step of the simulation. This means incrementing the time, and calling
     * the update method for all entities in the simulation.
     */
    public void step() {
        secondsSinceStart += secondsPerStep;
        for(Entity entity : getEntities()) {
            entity.update(secondsPerStep);
        }
        // Useful for debugging
        //printActivities(); 
        
        cloudMap.update(secondsPerStep);
    }
    
    private List<Entity> getEntities() {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for(Tile tile : getTiles()) {
            entities.addAll(tile.getEntities());
        }
        return entities;
    }
        
    /**
     * Return the tile at the given location, if any.
     * @param col The column to check in
     * @param row The row to checkin
     * @return The tile at the given location, or null if there is none.
     */
    public Tile getTile(int col, int row)
    {
        if(row < 0 || col < 0 || col >= width || row >= height) {
            return null;
        } else return tileGrid[row][col];
    }
    
    /**
     * Get all of the tiles in the habitat. These are not provided in any particular order.
     * @return An array containing all tiles within the habitat.
     */
    public Tile[] getTiles() {
        // Flatten the tileGrid to a one-dimensional array, and return it
        return Stream.of(tileGrid).flatMap(Stream::of).toArray(Tile[]::new);
    }
    
    /**
     * @return The tile grid containing all tiles within the habitat
     */
    public Tile[][] getTileGrid() {
        return tileGrid;
    }
    
    /**
     * Gets all of the tiles within a square of a given side length whose center is a
     * given tile within this habitat. This effectively gets all the tiles within a given
     * distance of the origin tile. The ordering is a spiral-like pattern from the origin.
     * 
     * @param origin the tile at the center of the square
     * @param distance the side length of the square
     * @return An array containing all tiles within that square
     */
    public Tile[] getTilesWithinDistance(Tile origin, int distance) {
        // Create a list of tiles to add to
        ArrayList<Tile> spiral = new ArrayList<Tile>();
        
        // Iterate until reaching the desired radius
        for(int currentDistance = 0; currentDistance <= distance; currentDistance++) {
            Tile[] tiles = getTilesAtDistance(origin, currentDistance);
            spiral.addAll(Arrays.asList(tiles));
        }
        
        // Return the tiles in the spiral as an array
        return spiral.toArray(new Tile[spiral.size()]);
    }
    
    /**
     * Gets all of the tiles on the perimeter of a square of a given side length whose
     * center is a given tile within this habitat. This effectively gets all the tiles at
     * a given distance from the origin. The ordering is a spiral-like pattern from the
     * origin.
     * 
     * @param origin the tile at the center of the square
     * @param distance the side length of the square
     * @return An array containing all tiles that are on the perimeter of that square
     */
    public Tile[] getTilesAtDistance(Tile origin, int distance) {
        // The ArrayList of tiles to add to
        ArrayList<Tile> tiles = new ArrayList<Tile>();
        
        if(distance == 0) tiles.add(origin);
        else {
            // Start at the top-left of the spiral
            int col = origin.getCol() - distance;
            int row = origin.getRow() - distance;
        
            // A description of the clockwise motion that the spiral will take.
            int[][] spiralArms = new int[][] {{1,0},{0,1},{-1,0},{0,-1}};
            // Add the tiles on each arm of the spiral of this radius
            for(int[] spiralArm : spiralArms) {
                // Add each tile on the current spiral arm
                for(int i = 0; i < 2*distance; i++)
                {
                    Tile tile = getTile(col,row);
                    if(tile != null) tiles.add(tile);
                    // Move to the next tile
                    col += spiralArm[0];
                    row += spiralArm[1];
                }
            }
        }
        return tiles.toArray(new Tile[tiles.size()]);
    }
    
    /**
     * Recalculate the minimum and maximum elevations.
     * Use this whenever the elevation of a tile is changed
     */
    public void recalculateMinAndMaxElevations() {
        Tile[] tiles = getTiles();
        minElevation = maxElevation = tiles[0].getElevation();
        for(Tile tile : tiles) {
            minElevation = Math.min(minElevation, tile.getElevation());
            maxElevation = Math.max(maxElevation, tile.getElevation());
        }
    }
    
    /**
     * @return The max elevation of all tiles within this habitat
     */
    public double getMaxElevation() {
        return maxElevation;
    }
    
    /**
     * @return The min elevation of all tiles within this habitat
     */
    public double getMinElevation() {
        return minElevation;
    }
    
    /**
     * Recalculate the minimum and maximum water levels.
     * Use this whenever the elevation of a tile is changed
     */
    public void recalculateMinAndMaxWaterLevels() {
        Tile[] tiles = getTiles();
        minWaterLevel = maxWaterLevel = tiles[0].getWaterLevel();
        for(Tile tile : tiles) {
            minWaterLevel = Math.min(minWaterLevel, tile.getWaterLevel());
            maxWaterLevel = Math.max(maxWaterLevel, tile.getWaterLevel());
        }
    }
    
    /**
     * @return The max saturation of all tiles within this habitat
     */
    public double getMaxWaterLevel() {
        return maxWaterLevel;
    }
    
    /**
     * @return The min saturation of all tiles within this habitat
     */
    public double getMinWaterLevel() {
        return minWaterLevel;
    }
    
    /**
     * Recalculate the minimum and maximum saturations.
     * Use this whenever the elevation of a tile is changed
     */
    public void recalculateMinAndMaxSaturations() {
        Tile[] tiles = getTiles();
        minSaturation = maxSaturation = tiles[0].getSaturation();
        for(Tile tile : tiles) {
            minSaturation = Math.min(minSaturation, tile.getSaturation());
            maxSaturation = Math.max(maxSaturation, tile.getSaturation());
        }
    }
    
    /**
     * @return The max saturation of all tiles within this habitat
     */
    public float getMaxSaturation() {
        return maxSaturation;
    }
    
    /**
     * @return The min saturation of all tiles within this habitat
     */
    public float getMinSaturation() {
        return minSaturation;
    }
    
    /**
     * @return The width of the habiat.
     */
    public int getWidth()
    {
        return width;
    }
    
    /**
     * @return The height of the habiat.
     */
    public int getHeight()
    {
        return height;
    }
    
    /**
     * @return the value of metersPerTile.
     */
    public int getMetersPerTile() {
        return metersPerTile;
    }
    
    public int getSecondsPerStep() {
        return secondsPerStep;
    }
    
    /**
     * @return the CloudMap used by this habitat to generate weather
     */
    public CloudMap getCloudMap() {
        return cloudMap;
    }
    
    /**
     * Sets the cloudmap
     * @param cloudMap the cloudMap to set
     */
    public void setCloudMap(CloudMap cloudMap) {
        this.cloudMap = cloudMap;
    }
    
    /**
     * @return the number of seconds since the start of the current day that this
     * simulation is on
     */
    public int getTimeOfDay() {
        return (int) (secondsSinceStart%(60*60*24));
    }
    
    /**
     * @return the Random object used by this habitat to generate random values
     */
    public Random getRandomiser() {
        return randomiser;
    }
    
    /**
     * Print the number of creatures of each type performing each activity A very useful
     * procedure for debugging.
     */
    private void printActivities() {
        Map<Pair<EntityType,CreatureActivity>,Integer> activities = new HashMap<>();
        for(Entity entity : getEntities()) {
            if(entity instanceof Creature) {
                Creature creature = (Creature) entity;
                Pair<EntityType,CreatureActivity> pair = new Pair(creature.getType(), creature.getActivity());
                Integer value = activities.get(pair);
                if(value == null) activities.put(pair, 1);
                else activities.put(pair,value+1);
            }
        }
        
        System.out.println("");
        for(EntityType type : EntityType.values()) {
            System.out.print(type + "{");
            for(CreatureActivity activity : CreatureActivity.values()) {
                Integer value = activities.get(new Pair(type, activity));
                if(value != null) System.out.print(activity + ":" + value  + ", ");
            }
            System.out.println("}");
        }
    }
}
