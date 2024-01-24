package Environment.EntityGenerators;

import java.util.Random;

import Environment.Tile;
import Entities.Woods;
import Utils.Noise2D;

/**
 * A class that influences and controls the generation of Woods tiles.
 * Adjusting the constants in this class will affect factors such as the rate at which Woods spawn.
 *
 * @version 2022.02.08
 */
public class WoodsGenerator extends EntityGenerator
{
    // wood constants
    // The frequency for the noise of the woods
    private static final double WOODS_FREQ = 0.15;
    // The number of octaves for the noise of the woods
    private static final int WOODS_OCTAVES = 3;
    // The persistence of the noise for the woods
    private static final double WOODS_PERSISTENCE = 0.9;
    // The lacunarity of the noise for the woods
    private static final double WOODS_LACUNARITY = 0.5;
    
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
    
    private static final double WOODS_SATURATION_DEPENDENCE = 0.8;
    private static final double WOODS_THRESHOLD = 0.05;
    
    //Noise variables
    public Noise2D woodsNoise;
    public Noise2D xOffsetNoise;
    public Noise2D yOffsetNoise;
    
    /**
     * Constructor for WoodsGenerator
     * @param randomiser The randomiser to generate the elevations from
     */
    public WoodsGenerator(Random randomiser) {
        // Create some NoiseMaps to generate elevation from
        entityNoise = new Noise2D(randomiser, WOODS_OCTAVES, WOODS_FREQ, WOODS_FREQ, WOODS_PERSISTENCE, WOODS_LACUNARITY);
        // Create some NoiseMaps to offset
        xOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
        yOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
    }
    
    /**
     * Attempt to place a woods feature onto a tile, if the tile is not a water tile, AND
     * the elevation of the tile is above a threshold value.
     * @param tile      the tile a woods will attempt to be placed on.
     */
    protected void populateTile(Tile tile) {
        if(eval(tile) > WOODS_THRESHOLD && tile.getWaterLevel() <= 0) {
            new Woods(tile);
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
        double woodsValue = (1 + entityNoise.eval(xAfterOffset,yAfterOffset))/2;
        
        double saturation = Math.pow(tile.getSaturation(), WOODS_SATURATION_DEPENDENCE);
        
        return woodsValue*saturation;
    }
}
