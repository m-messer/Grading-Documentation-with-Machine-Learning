package Utils;

import java.util.Random;

/**
 * A 2D noise map which allows
 *
 * @version 2022.02.08
 */
public class Noise2D
{
    Noise3D noise;
    
    /**
     * Constructor for objects of class MultiSimplexNoise
     * @param octaves The number of octaves for this noise map
     * @param xFreq The frequency in the x direction
     * @param yFreq The frequency in the y direction
     * @param persistence The persistence of this noise
     * @param lacunarity The lacunarity of this noise
     */
    public Noise2D(Random randomiser, int octaves, double xFreq, double yFreq, double persistence, double lacunarity)
    {
        noise = new Noise3D(randomiser, octaves, xFreq, yFreq, 1, persistence, lacunarity);
    }
    
    /**
     * Evaluate the noise at a given x and y coordinate
     * @param x The x coordinate to evaluate at
     * @param y The y coordinate to evaluate at
     */
    public double eval(double x, double y) {
        return noise.eval(x, y, 0);
    }
}
