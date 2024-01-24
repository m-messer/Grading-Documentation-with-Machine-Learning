package Utils;

import java.util.Random;

/**
 * A 1D noise map which allows
 *
 * @version 2022.02.08
 */
public class Noise1D
{
    Noise3D noise;
    
    /**
     * Constructor for objects of class MultiSimplexNoise
     * @param octaves The number of octaves for this noise map
     * @param xFreq The frequency in the x direction
     * @param persistence The persistence of this noise
     * @param lacunarity The lacunarity of this noise
     */
    public Noise1D(Random randomiser, int octaves, double xFreq, double persistence, double lacunarity)
    {
        noise = new Noise3D(randomiser, octaves, xFreq, 1, 1, persistence, lacunarity);
    }
    
    /**
     * Evaluate the noise at a given x and y coordinate
     * @param x The x coordinate to evaluate at
     */
    public double eval(double x) {
        return noise.eval(x, 0, 0);
    }
}
