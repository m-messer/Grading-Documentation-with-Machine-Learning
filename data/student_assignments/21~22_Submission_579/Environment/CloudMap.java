package Environment;

import java.util.Random;

import Utils.Noise3D;
import Utils.Noise1D;

/**
 * CloudMap class. Used to generate weather using noise.
 * 
 * Currently, the CloudMap works by accepting two 2D noise maps and passing them over
 * each other in opposite directions, taking the average of the two values it produces.
 *
 * @version 2022.02.08
 */
public class CloudMap
{
    // Cloud constants
    // The frequency for the noise of the clouds
    private static final double CLOUD_FREQ = 0.05;
    // The number of octaves for the noise of the clouds
    private static final int CLOUD_OCTAVES = 4;
    // The persistence of the noise for the clouds
    private static final double CLOUD_PERSISTENCE = 0.5;
    // The lacunarity of the noise for the clouds
    private static final double CLOUD_LACUNARITY = 0.5;
    // How much the clouds change with time
    private static final double CLOUD_CHANGEABILITY = 0.00001;
    
    // The level of condensation which clouds start to properly form
    public static final double CLOUD_THRESHOLD = 0.6;
    public static final double SUB_CLOUD_AMPLITUDE = 0.5;
    public static final double SUPER_CLOUD_EXPONENT = 3;
    
    
    // Offset constants (these affect how jagged the terrain is)
    // The frequency of the noise for the offsets
    private static final double OFFSET_FREQ = 0.1;
    // The amplitude of the noise for the offsets
    private static final double OFFSET_AMPLITUDE = 10;
    // The number of octaves for the noise of the offsets
    private static final int OFFSET_OCTAVES = 3;
    // How much the clouds change with time
    public static final double OFFSET_CHANGEABILITY = 0.00017;
    // The persistence of the noise for the valleys
    private static final double OFFSET_PERSISTENCE = 0.5;
    // The lacunarity of the noise for the valleys
    private static final double OFFSET_LACUNARITY = 0.5;
    
    
    // Wind speed constants (these affect how jagged the terrain is)
    // The frequency of the noise for the offsets
    private static final double HORIZONTAL_WIND_FREQ = 0.000001;
    // The amplitude of the noise for the offsets
    private static final double HORIZONTAL_WIND_AMPLITUDE = 0.3;
    // The number of octaves for the noise of the offsets
    private static final int HORIZONTAL_WIND_OCTAVES = 3;
    // The persistence of the noise for the valleys
    private static final double HORIZONTAL_WIND_PERSISTENCE = 0.5;
    // The lacunarity of the noise for the valleys
    private static final double HORIZONTAL_WIND_LACUNARITY = 0.5;
    
    // Wind speed constants (these affect how jagged the terrain is)
    // The frequency of the noise for the offsets
    private static final double VERTICAL_WIND_FREQ = 0.000001;
    // The amplitude of the noise for the offsets
    private static final double VERTICAL_WIND_AMPLITUDE = 0.3;
    // The number of octaves for the noise of the offsets
    private static final int VERTICAL_WIND_OCTAVES = 3;
    // The persistence of the noise for the valleys
    private static final double VERTICAL_WIND_PERSISTENCE = 0.5;
    // The lacunarity of the noise for the valleys
    private static final double VERTICAL_WIND_LACUNARITY = 0.5;
    
    
    
    // Noise maps
    private Noise3D cloudNoise;
    private Noise3D xOffsetNoise;
    private Noise3D yOffsetNoise;
    private Noise1D horizontalWindNoise;
    private Noise1D verticalWindNoise;
    
    // How much the clouds are currently displaced
    private double xDisplacement;
    private double yDisplacement;
    // How much time has passed since the clouds were at their initial point
    private double time;

    /**
     * Constructor for objects of class CloudMap
     * @param randomiser A random object to generate the clouds from
     */
    public CloudMap(Random randomiser)
    {
        cloudNoise = new Noise3D(randomiser, CLOUD_OCTAVES, CLOUD_FREQ, CLOUD_FREQ, CLOUD_CHANGEABILITY, CLOUD_PERSISTENCE, CLOUD_LACUNARITY);
        
        xOffsetNoise = new Noise3D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_CHANGEABILITY, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
        yOffsetNoise = new Noise3D(randomiser, OFFSET_OCTAVES, OFFSET_FREQ, OFFSET_FREQ, OFFSET_CHANGEABILITY, OFFSET_PERSISTENCE, OFFSET_LACUNARITY);
        
        horizontalWindNoise = new Noise1D(randomiser, HORIZONTAL_WIND_OCTAVES, HORIZONTAL_WIND_FREQ, HORIZONTAL_WIND_PERSISTENCE, HORIZONTAL_WIND_LACUNARITY);
        verticalWindNoise = new Noise1D(randomiser, VERTICAL_WIND_OCTAVES, VERTICAL_WIND_FREQ, VERTICAL_WIND_PERSISTENCE, VERTICAL_WIND_LACUNARITY);
        
        xDisplacement = yDisplacement = 0;
        time = 0;
    }
    
    /**
     * Updates the displacement values of the clouds as time increases.
     * The more time passed, the further the clouds should be from start location.
     * @param timePassed        The amount of time passed since simulation stage start.
     */
    public void update(double timePassed) {
        time += timePassed;
        xDisplacement += timePassed * HORIZONTAL_WIND_AMPLITUDE * horizontalWindNoise.eval(time);
        yDisplacement += timePassed * VERTICAL_WIND_AMPLITUDE * verticalWindNoise.eval(time);
    }
    
    /**
     * Evaluate the clouds at a given coordinate and step
     * @param x The x coordinate to evaluate at
     * @param y The y coordinate to evaluate at
     */
    public double eval(int x, int y) {
        double xAfterDisplacement = x - xDisplacement;
        double yAfterDisplacement = y - yDisplacement;
        
        double xAfterOffset = xAfterDisplacement + (OFFSET_AMPLITUDE * xOffsetNoise.eval(xAfterDisplacement,yAfterDisplacement,time));
        double yAfterOffset = yAfterDisplacement + (OFFSET_AMPLITUDE * yOffsetNoise.eval(xAfterDisplacement,yAfterDisplacement,time));
        
        double noiseVal = cloudNoise.eval(xAfterOffset,yAfterOffset,time);
        return noiseToCloudCover(noiseVal);
    }
    
    /**
     * Converts a noise value into a value used for cloud cover patterns.
     * @param noiseVal      double value of the noise value.
     */
    private double noiseToCloudCover(double noiseVal) {
        double cloudVal = (noiseVal+1)/2;
        return (cloudVal > CLOUD_THRESHOLD ? Math.pow(cloudVal,1/SUPER_CLOUD_EXPONENT) : cloudVal*SUB_CLOUD_AMPLITUDE);
    }
}
