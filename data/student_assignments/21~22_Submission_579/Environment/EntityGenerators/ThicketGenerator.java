package Environment.EntityGenerators;

import java.util.Random;

import Environment.Tile;
import Entities.Thicket;
import Utils.Noise2D;

/**
* A class that influences and controls the generation of Thicket tiles (i.e. berry bushes)
 * Adjusting the constants in this class will affect factors such as the rate at which Thickets spawn.
 *
 * @version 2022.02.08
 */
public class ThicketGenerator extends EntityGenerator
{
    // Thicket constants
    // The frequency for the noise of the thickets
    private static final double THICKET_FREQ = 0.5;
    // The number of octaves for the noise of the thickets
    private static final int THICKET_OCTAVES = 3;
    // The persistence of the noise for the thickets
    private static final double THICKET_PERSISTENCE = 0.9;
    // The lacunarity of the noise for the thickets
    private static final double THICKET_LACUNARITY = 0.5;
    
    // Offset constants (these affect how jagged generation is)
    // The frequency of the noise for the offsets
    private static final double OFFSET_FREQ = 0.25;
    // The persistence of the noise for the offsets
    private static final double OFFSET_PERSISTENCE = 0.5;
    // The lacunarity of the noise for the offsets
    private static final double OFFSET_LACUNARITY = 0.5;
    // The amplitude of the noise for the offsets
    private static final double OFFSET_AMPLITUDE = 10;
    // The number of octaves for the noise of the offsets
    private static final int OFFSET_OCTAVES = 4;
    
    private static final double THICKET_SATURATION_DEPENDENCE = 0.1;
    private static final double THICKET_THRESHOLD = 0.8;
    
    /**
     * Constructor for ThicketGenerator
     * @param randomiser The randomiser to generate the elevations from
     */
    public ThicketGenerator(Random randomiser) {
        // Create some NoiseMaps to generate elevation from
        entityNoise = new Noise2D(randomiser, THICKET_OCTAVES, THICKET_FREQ, THICKET_FREQ, THICKET_PERSISTENCE, THICKET_LACUNARITY);
        // Create some NoiseMaps to offset
        xOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
        yOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
    }
    
    /**
     * Attempt to place a thicket feature onto a tile, if the tile is not a water tile, AND
     * the elevation of the tile is above a threshold value.
     * @param tile      the tile a thicket will attempt to be placed on.
     */    
    protected void populateTile(Tile tile) {
        if(eval(tile) > THICKET_THRESHOLD && tile.getWaterLevel() <= 0) {
            new Thicket(tile);
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
        double thicketValue = (1+entityNoise.eval(xAfterOffset, yAfterOffset))/2;
        
        double saturation = Math.pow(tile.getSaturation(), THICKET_SATURATION_DEPENDENCE);
        return thicketValue*saturation;
    }
}
