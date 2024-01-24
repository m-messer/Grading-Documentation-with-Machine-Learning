import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.io.IOException;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single tile.
 *
 * @version 2020.02.09
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the tiles.
    private Tile[][] field;
    // Terrain generator
    private TerrainGenerator tr;
    
    //Weather object
    private Weather weather;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     * @param terrainSize, the relative size of the terrain stuctures.
     * @param seed, the seed for the terrain generation noise map.
     */
    public Field(int depth, int width, double terrainSize, long seed) throws IOException
    {
        this.depth = depth;
        this.width = width;
        field = new Tile[depth][width];
        tr = new TerrainGenerator(depth, width, terrainSize, seed);
        weather = new Weather();
    }
    
    /**
     * @return The array of tiles.
     */
    public Tile[][] getField()
    {
        return field;
    }
    
    /**
     * @return TerrainGenerator Object
     */
    public TerrainGenerator getTR()
    {
        return tr;
    }
    
    /**
     * @return Weather, the current weather of the field.
     */
    public Weather getWeather()
    {
        return weather;
    }

    /**
     * @return tile at given location.
     */
    public Tile getTile(Location location)
    {
        return field[location.getRow()][location.getCol()];
    }
    
    /**
     * @return tile at given location.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public Tile getTile(int row, int col)
    {
        return field[row][col];
    }
    
    /**
     * Empty the field of animals.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                getTile(row, col).clearAnimal();
            }
        }
    }
    
    /**
     * Clear the given location of its animal.
     * @param location The location to clear.
     */
    public void clear(Location location)
    {
        field[location.getRow()][location.getCol()].clearAnimal();
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Actor animal, int row, int col)
    {
        place(animal, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Actor animal, Location location)
    {
        field[location.getRow()][location.getCol()].setAnimal(animal);
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getAnimalAt(Location location)
    {
        return getAnimalAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getAnimalAt(int row, int col)
    {
        return field[row][col].getAnimal();
    }
    
    /**
     * Return the organism at the given location. 
     * An animal will be returned, if any, otherwise the plant object at location will be returned, if any.
     * @param location Where in the field.
     * @return The organism at the given location, or null if there is none.
     */
    public Object getOrganismAt(Location location)
    {
        return getOrganismAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the organism at the given location. 
     * An animal will be returned, if any, otherwise the plant object at location will be returned, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The organism at the given location, or null if there is none.
     */
    public Object getOrganismAt(int row, int col)
    {
        return field[row][col].getOrganism();
    }
    
    /**
     * Return the plant at the given location, if any.
     * @param location Where in the field.
     * @return The plant at the given location, or null if there is none.
     */
    public Object getPlantAt(Location location)
    {
        return getPlantAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the plant at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The plant at the given location, or null if there is none.
     */
    public Object getPlantAt(int row, int col)
    {
        return field[row][col].getPlant();
    }
    
    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getAnimalAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }
    
    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }
    
    /**
     * Return a shuffled list of locations adjacent to the given one, with a predefined offset of 1.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        return adjacentLocations(location, 1);
    }
    
    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param offset How far to 'look' / offset the adjacent locations returned
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location, int offset)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        
        if (offset == 0) return locations; // the animal currently cannot see, return an empty list
        
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -offset; roffset <= offset; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -offset; coffset <= offset; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }
    
    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
