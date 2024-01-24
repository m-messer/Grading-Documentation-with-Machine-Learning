package Utils;
import java.util.Random;


/**
 * Noise3D class. Operates on an OpenSimplexNoise class in three dimensions
 *
 * @version 2022.02.08
 */
public class Noise3D
{
    private OpenSimplexNoise[] noises;
    
    private double xFreq;
    private double yFreq;
    private double zFreq;
    private double persistence;
    private double lacunarity;
    
    private double normalisationFactor;

    /**
     * Constructor for objects of class MultiSimplexNoise
     * @param octaves The number of octaves for this noise map
     * @param xFreq The frequency in the x direction
     * @param yFreq The frequency in the y direction
     * @param zFreq The frequency in the z direction
     * @param persistence The persistence of this noise
     * @param lacunarity The lacunarity of this noise
     */
    public Noise3D(Random randomiser, int octaves, double xFreq, double yFreq, double zFreq, double persistence, double lacunarity)
    {
        this.xFreq = xFreq;
        this.yFreq = yFreq;
        this.zFreq = zFreq;
        this.persistence = persistence;
        this.lacunarity = lacunarity;
        
        noises = new OpenSimplexNoise[octaves];
        for(int i = 0; i < octaves; i++) {
            noises[i] = new OpenSimplexNoise(randomiser.nextLong());
        }
        
        normalisationFactor = 1-Math.pow(persistence,octaves);
    }

    /**
     * Evaluate this noise3D at a given point
     * @param x The x coordinate to evaluate at
     * @param y The y coordinate to evaluate at
     * @param z The z coordinate to evaluate at
     */
    public double eval(double x, double y, double z) {
        double val = 0;
        for(int i = 0; i < noises.length; i++) {
            double layerPersist = Math.pow(persistence,i+1);
            double layerLacunar = Math.pow(lacunarity,i);
            val += noises[i].eval(x*xFreq*layerLacunar, y*yFreq*layerLacunar, z*zFreq*layerLacunar)*layerPersist;
        }
        return val/normalisationFactor;
    }
}
