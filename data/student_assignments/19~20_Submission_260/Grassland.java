import java.awt.Color;
import java.util.Random;
/**
 * Represents the Biome in the simulation (Grassland). 
 * Stores the constants that determine what tile is created where for what luminance supplied by the noise map.
 *
 * @version 2020.02
 */
public class Grassland
{
    static float DEEP_WATER = 0.44f;
    
    static float SHALLOW_WATER = 0.5f;
    
    static float SEA_BED = 0.48f;
    
    static float SAND = 0.54f;
    
    static float GRASS = 0.76f;
    
    static float ROCK = 0.80f;
    
    // the final capped value (1)
    static float SNOW = 1; 
    
    // the grassland's current humidity (can change)
    static float HUMIDITY = 0.5f;
    
    // rain specific parameters for a Grassland
    static float rainProbability = 0.01f;
    static int maxRainDuration = 24;
    
    // fog specific parameters for a Grassland
    static float fogProbability = 0.01f;
    static int maxFogDuration = 24;

    // the constant increment for flooding and droughting
    static float increment = 0.0005f;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Give back the exact colors for the ranges of luminances
     * 
     * @param luminance, the luminance value (0<x<1) of the 'pixel'
     * @param col, effectively the x axis
     * @param row, effectively the y axis
     */
    static Tile determineTile(float luminance)
    {
        if (luminance < DEEP_WATER + rand.nextInt(5)/100) return new DeepWater();
        else if (luminance < SHALLOW_WATER) return new ShallowWater();
        else if (luminance < SEA_BED) return new SeaBed();
        else if (luminance < SAND) return new Sand();
        else if (luminance < GRASS) return new Grass();
        else if (luminance < ROCK) return new Rock();
        else return new Snow();
    }
    
    /**
     * Introduce rising sea levels/flooding (i.e. more water)
     * *Shallow water and Grass may change
     */
    static void flood()
    {
        //float val = rand.nextInt(200)/10000; // generate a value between 0.0001-0.02
        float val = increment; // generate a value between 0.0001-0.02
        incDeepWater(val);
        incShallowWater(val);
    }
    
    /**
     * Decrease the sea levels (i.e. more land)
     * *Shallow water and Deep water may change
     */
    static void drought()
    {
        //float val = rand.nextInt(200)/10000; // generate a value between 0.0001-0.02
        float val = increment;
        decDeepWater(val);
        decShallowWater(val);
    }
    
    /**
     * @return get the increment value
     */
    static float getIncrement()
    {
        return increment;
    }
    
    /**
     * @return get the increment value
     */
    static float getDeepWaterThreshold()
    {
        return DEEP_WATER;
    }
    
    /**
     * @return get the increment value
     */
    static float getShallowWaterThreshold()
    {
        return SHALLOW_WATER;
    }
    
    /**
     * @return get the increment value
     */
    static float getSandThreshold()
    {
        return SAND;
    }
    
    /**
     * @return get the increment value
     */
    static float getGrassThreshold()
    {
        return GRASS;
    }
    
    /**
     * Set DEEP_WATER value
     */
    static void incDeepWater(float val)
    {
        DEEP_WATER+=val;
    }
    
    /**
     * Set DEEP_WATER value
     */
    static void decDeepWater(float val)
    {
        DEEP_WATER-=val;
    }
    
    /**
     * Increment SHALLOW_WATER value
     */
    static void incShallowWater(float val)
    {
        SHALLOW_WATER+=val;
    }
    
    /**
     * Decrement SHALLOW_WATER value
     */
    static void decShallowWater(float val)
    {
        SHALLOW_WATER-=val;
    }
    
    /**
     * Increment GRASS value
     */
    static void incGrass(float val)
    {
        GRASS+=val;
    }
    
    /**
     * Decrement GRASS value
     */
    static void decGrass(float val)
    {
        GRASS-=val;
    }
}
