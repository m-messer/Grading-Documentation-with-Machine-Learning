package Environment.EntityGenerators;

import java.util.Random;

import Utils.Noise2D;
import Environment.Tile;
import Entities.Deer;

/**
 * A class that influences and controls the generation of Deer tiles.
 * Adjusting the constants in this class will affect factors such as the rate at which Deer spawn.
 *
 * @version 2022.02.08
 */
public class DeerGenerator extends EntityGenerator
{
    // deer constants
    // The frequency for the noise of the deer
    private static final double DEER_FREQ = 1;
    // The number of octaves for the noise of the deer
    private static final int DEER_OCTAVES = 1;
    // The persistence of the noise for the deer
    private static final double DEER_PERSISTENCE = 0.9;
    // The lacunarity of the noise for the deer
    private static final double DEER_LACUNARITY = 0.5;
    
    // Offset constants (these affect how jagged generation is)
    // The frequency of the noise for the offsets
    private static final double OFFSET_FREQ = 1;
    // The persistence of the noise for the offsets
    private static final double OFFSET_PERSISTENCE = 0.5;
    // The lacunarity of the noise for the offsets
    private static final double OFFSET_LACUNARITY = 0.5;
    // The amplitude of the noise for the offsets
    private static final double OFFSET_AMPLITUDE = 25;
    // The number of octaves for the noise of the offsets
    private static final int OFFSET_OCTAVES = 4;
    
    private static final double DEER_THRESHOLD = 3.4;
    
    /**
     * Constructor for SeerGenerator
     * @param randomiser The randomiser to generate the elevations from
     */
    public DeerGenerator(Random randomiser) {
        // Create some NoiseMaps to generate elevation from
        entityNoise = new Noise2D(randomiser, DEER_OCTAVES, DEER_FREQ, DEER_FREQ, DEER_PERSISTENCE, DEER_LACUNARITY);
        // Create some NoiseMaps to offset
        xOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
        yOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
    }
    
    /**
     * Attempt to place a deer onto a tile, if the tile is a water tile, AND
     * the elevation of the tile is above a threshold value.
     * @param tile      the tile an deer will attempt to be placed on.
     */
    public void populateTile(Tile tile) {
        if(eval(tile) > DEER_THRESHOLD && tile.getWaterLevel() <= 0) {
            new Deer(tile);
        }
    }
    
    /**
     * Evaluate the elevation of the terrain at a given coordinate (after offsetting)
     * @param x The x coordinate to get the elevation at
     * @param y The y coordinate to get the elevation at
     * @return The elevation of the terrain at the given coordinate (after offsetting)
     */
    public double eval(Tile tile) {
        int x = tile.getCol();
        int y = tile.getRow();
        
        // Calculate the offset
        double xAfterOffset = x + OFFSET_AMPLITUDE * xOffsetNoise.eval(x,y);
        double yAfterOffset = y + OFFSET_AMPLITUDE * yOffsetNoise.eval(x,y);
        double deerValue = (1+entityNoise.eval(xAfterOffset,yAfterOffset))/2;
        
        return deerValue;
    }
}
