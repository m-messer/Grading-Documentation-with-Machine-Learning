import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

/**
 * OpenSimplexNoise Creation class. This is the class responsible for generating the actual noise image and writing
 * it to a file (noise.png) to be used throughout the rest of the simulation. 
 *  
 * 
 * *NOTE - ALL CREDIT GOES TO THE OWNER.
 */

public class OpenSimplexNoiseCreate
{
    private static final int DEFAULT_WIDTH = 512;
    private static final int DEFAULT_HEIGHT = 512;
    private static final double DEFAULT_FEATURE_SIZE = 100;
    private static final int DEFAULT_SEED = 567;

    /**
     * Generate a new noise map with designated parameters
     * @param width, the width of the noise map
     * @param height, the height of the noise map
     * @param featureSize, the relative size of the features (smaller featureSize = more grainy image)
     */
    public static void generate(int height, int width, double featureSize, long seed)
        throws IOException {
        
        OpenSimplexNoise noise = new OpenSimplexNoise(seed);
        BufferedImage image = new BufferedImage(height, width, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                double value = noise.eval(x / featureSize, y / featureSize, 0.0);
                int rgb = 0x010101 * (int)((value + 1) * 127.5);
                image.setRGB(y, x, rgb);
            }
        }
        ImageIO.write(image, "png", new File("noise.png"));
    }
    
    /**
     * Generate a noise map with default dimensions and feature size
     */
    public static void generate()
        throws IOException {
        generate(DEFAULT_HEIGHT, DEFAULT_WIDTH, DEFAULT_FEATURE_SIZE, DEFAULT_SEED);
    }
}
