package Environment.EntityGenerators;

import java.util.Random;

import Utils.Noise2D;
import Environment.Tile;
import Entities.Fish;

/**
 * A class that influences and controls the generation of Fish tiles.
 * Adjusting the constants in this class will affect factors such as the rate at which Fish spawn.
 *
 * @version 2022.02.08
 */
public class FishGenerator extends EntityGenerator
{
    // fish constants
    // The frequency for the noise of the fish
    private static final double FISH_FREQ = 1;
    // The number of octaves for the noise of the fish
    private static final int FISH_OCTAVES = 1;
    // The persistence of the noise for the fish
    private static final double FISH_PERSISTENCE = 0.9;
    // The lacunarity of the noise for the fish
    private static final double FISH_LACUNARITY = 0.5;
    
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
    
    private static final double FISH_THRESHOLD = 2.5;
    
    /**
     * Constructor for FishGenerator
     * @param randomiser The randomiser to generate the elevations from
     */
    public FishGenerator(Random randomiser) {
        // Create some NoiseMaps to generate elevation from
        entityNoise = new Noise2D(randomiser, FISH_OCTAVES, FISH_FREQ, FISH_FREQ, FISH_PERSISTENCE, FISH_LACUNARITY);
        // Create some NoiseMaps to offset
        xOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
        yOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
    }
    
    /**
     * Attempt to place a fish onto a tile, if the tile is a water tile, AND
     * the elevation of the tile is above a threshold value.
     * @param tile      the tile an fish will attempt to be placed on.
     */
    public void populateTile(Tile tile) {
        if(eval(tile) > FISH_THRESHOLD && tile.getWaterLevel() > 0) {
            new Fish(tile);
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
        double fishValue = (1+entityNoise.eval(xAfterOffset,yAfterOffset))/2;
        
        return fishValue;
    }
}