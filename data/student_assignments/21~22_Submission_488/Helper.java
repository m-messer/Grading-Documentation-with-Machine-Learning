import java.util.*;
import java.awt.*;
/**
 * A helper with useful functions. 
 * 
 * It incldues a randomizer that provides control over the randomization of the simulation. By using the shared, fixed-seed 
 * randomizer, repeated runs will perform exactly the same (which helps with testing). Set 
 * 'useShared' to false to get different random behaviour every time.
 *
 */
public class Helper
{
    // The default seed for control of randomization.
    private static final int SEED = 1111;
    // A shared Random object, if required.
    private static final Random rand = new Random(SEED);
    // Determine whether a shared random generator is to be provided.
    private static final boolean useShared = true;

    /**
     * Provide a random generator.
     * 
     * @return A random object.
     */
    public static Random getRandom()
    {
        if(useShared) {
            return rand;
        }
        else {
            return new Random();
        }
    }
    
    /**
     * Reset the randomization.
     * This will have no effect if randomization is not through
     * a shared Random generator.
     */
    public static void reset()
    {
        if(useShared) {
            rand.setSeed(SEED);
        }
    }
    
    /**
     * Gets the hex value of a color as a string.
     * 
     * @param color The color to convert.
     * 
     * @return The hexcode as a string.
     */
    public static String hexFrom(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Gets a random object from a collection
     * 
     * @param color The collecton to choose from.
     * 
     * @return A random object.
     */
    public static <E> Object randomObjectFrom(Collection<E> collection) {
        int index = getRandom().nextInt(collection.size());
        return new ArrayList<E>(collection).get(index); 
    }
}
