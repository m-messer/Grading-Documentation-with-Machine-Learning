package Environment;
import java.util.Random;

import Utils.Noise2D;

/**
 * A map for obtaining elevation values from coordinates.
 * Adjusting the constants in this class will create different terrain types.
 *
 * @version 2022.02.08
 */
public class ElevationMap
{
    // Hill constants
    // The frequency for the noise of the hills
    private static final double HILL_FREQ = 0.015;
    // The number of octaves for the noise of the hills
    private static final int HILL_OCTAVES = 7;
    // The persistence of the noise for the hills
    private static final double HILL_PERSISTENCE = 0.9;
    // The lacunarity of the noise for the hills
    private static final double HILL_LACUNARITY = 0.5;
    
    // Valley constants
    // The frequency of the noise for the valleys
    private static final double VALLEY_FREQ = 0.01;
    // The number of octaves for the noise of the valleys
    private static final int VALLEY_OCTAVES = 2;
    // The persistence of the noise for the valleys
    private static final double VALLEY_PERSISTENCE = 0.5;
    // The lacunarity of the noise for the valleys
    private static final double VALLEY_LACUNARITY = 0.5;
    // How wide the valleys are
    private static final double VALLEY_WIDTH = 0.1;
    
    // The ratio of the intensity of the hill noise map to the valley noise map
    private static final double HILL_VALLEY_RATIO = 0.8;
    
    // Offset constants (these affect how jagged the terrain is)
    // The frequency of the noise for the offsets
    private static final double OFFSET_FREQ = 0.25;
    // The persistence of the noise for the valleys
    private static final double OFFSET_PERSISTENCE = 0.5;
    // The lacunarity of the noise for the valleys
    private static final double OFFSET_LACUNARITY = 0.5;
    // The amplitude of the noise for the offsets
    private static final double OFFSET_AMPLITUDE = 5;
    // The number of octaves for the noise of the offsets
    private static final int OFFSET_OCTAVES = 4;
    
    //Noise variables
    public Noise2D hillNoise;
    public Noise2D valleyNoise;
    public Noise2D xOffsetNoise;
    public Noise2D yOffsetNoise;
    
    /**
     * Constructor for ElevationMap
     * @param randomiser The randomiser to generate the elevations from
     */
    public ElevationMap(Random randomiser) {
        // Create some NoiseMaps to generate elevation from
        hillNoise = new Noise2D(randomiser, HILL_OCTAVES, HILL_FREQ, HILL_FREQ, HILL_PERSISTENCE, HILL_LACUNARITY);
        valleyNoise = new Noise2D(randomiser, VALLEY_OCTAVES, VALLEY_FREQ, VALLEY_FREQ, VALLEY_PERSISTENCE, VALLEY_LACUNARITY);
        // Create some NoiseMaps to offset
        xOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
        yOffsetNoise = new Noise2D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
    }
    
    /**
     * Evaluate the elevation of the terrain at a given coordinate (after offsetting)
     * @param x The x coordinate to get the elevation at
     * @param y The y coordinate to get the elevation at
     * @return The elevation of the terrain at the given coordinate (after offsetting)
     */
    public double eval(double x, double y) {
        // Calculate the offset
        double xAfterOffset = x + OFFSET_AMPLITUDE * xOffsetNoise.eval(x,y);
        double yAfterOffset = y + OFFSET_AMPLITUDE * yOffsetNoise.eval(x,y);
        
        // Calculate the elevation at the offset position
        return calculateElevation(xAfterOffset, yAfterOffset);
    }
    
    /**
     * Evaluate the elevation of the terrain at a given coordinate (before offsetting)
     * @param x The x coordinate to get the elevation at
     * @param y The y coordinate to get the elevation at
     * @return The elevation of the terrain at the given coordinate (before offsetting)
     */
    private double calculateElevation(double x, double y) {
        // The hill value corresponds simply to the hill noise
        double hillVal = hillNoise.eval(x, y);
        
        // The valley value is mapped by a series of steps which produce valley-like structures
        double valleyVal = valleyNoise.eval(x,y);
        valleyVal = Math.abs(valleyVal);
        valleyVal = Math.pow(valleyVal, VALLEY_WIDTH);
        valleyVal = 2*valleyVal-1;
        
        // Return the hills and valleys, distributed according to the hill-valley ratio
        return ((1-HILL_VALLEY_RATIO)*hillVal + HILL_VALLEY_RATIO*valleyVal);
    }
}
