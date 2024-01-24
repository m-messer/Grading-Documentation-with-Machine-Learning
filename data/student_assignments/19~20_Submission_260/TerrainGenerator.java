import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.*;
import java.io.File;
import java.net.URL;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Class reponsible for generating terrain (2d topographical space) for the simulation.
 * @version 2020.02.09
 */
public class TerrainGenerator
{
    // the noise map
    private OpenSimplexNoiseCreate noiseMap = new OpenSimplexNoiseCreate();
    // the readable, processable image for Java
    private static BufferedImage bi;
    
    /**
     * Constructor for objects of class Terrain
     * @param width, the width of the terrain map
     * @param height, the height of the terrain map
     * @param terrainSize, the relative size of the terrain stuctures
     */
    public TerrainGenerator(int height, int width, double terrainSize, long seed) throws IOException
    {
        try {
            // initialize and create the noise map with given parameters
            noiseMap = new OpenSimplexNoiseCreate();
            noiseMap.generate(height, width, terrainSize, seed);
            
            bi = ImageIO.read(new File("noise.png"));
        }
        catch (IOException e) {
            System.out.println("Error loading the noise map.");
        }
    }
    
    /**
     * Acidify the noise map. This creates a very nice optical illusion.
     */
    public static void acidTrip() throws IOException
    {
        for (int x=0; x<bi.getWidth(); x++) {
            for (int y=0; y<bi.getHeight(); y++) {
                int color = bi.getRGB(x, y);
        
                // use unsigned right shifts to obtain the red, green, and blue color values (RGB), respectively
                int R = (color>>>16) & 0xFF;
                int G = (color>>>8) & 0xFF;
                int B = (color>>>0) & 0xFF;
                
                R+=10;
                G+=10;
                B+=10;
                
                String stringRGB = "" + R + G + B;
                int newRGB = Integer.parseInt(stringRGB);
    
                bi.setRGB(x, y, newRGB);
            }
        }
        
        ImageIO.write(bi, "png", new File("noise.png"));
    }
    
    /**
     * Obtain the luminance (a value between 0 and 1) of a specific tile (row,col)
     * @param col, the column (x-coordinate, width) of the tile
     * @param row, the row (y-coordinate, height) of the tile
     * @return the luminance (as a float). The higher the value, the brighter the pixel.
     */
    public static float getLuminance(int row, int col)
    {
        // searches the image as a 
        
        int color = bi.getRGB(row, col);
        
        // use unsigned right shifts to obtain the red, green, and blue color values (RGB), respectively
        int R = (color>>>16) & 0xFF;
        int G = (color>>>8) & 0xFF;
        int B = (color>>>0) & 0xFF;
        
        // formula used to calculate luminance, better than using (R+G+B)/3
        // https://stackoverflow.com/questions/687261/converting-rgb-to-grayscale-intensity
        float luminance = (R*0.216f + G*0.7152f + B*0.0722f)/255;
        //float luminance = ((R+G+B)/3)/255;
        return luminance;
    }
}
