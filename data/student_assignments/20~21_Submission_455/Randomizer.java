import java.util.Random;

/**
 * Provide control over the randomization of the simulation. By using the shared, fixed-seed 
 * randomizer, repeated runs will perform exactly the same (which helps with testing). Set 
 * 'useShared' to false to get different random behaviour every time.
 *
 * @version 2021.03.03 (3)
 */
public class Randomizer
{
    // The default seed for control of randomization.
    private static final int SEED = 1111;
    // A shared Random object, if required.
    private static final Random rand = new Random(SEED);
    // Determine whether a shared random generator is to be provided.
    private static final boolean useShared = true; // Turn to true for a more stable example of a simulation

    /**
     * Constructor for objects of class Randomizer
     */
    public Randomizer()
    {
    }

    /**
     * Provide a random generator.
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
    
    //Used for debugging
    // public static void test(){
        // //System.out.println( rand.nextInt() + " " + rand.nextInt() + " " + rand.nextDouble() + " " + rand.nextDouble() + " ");
        // Random rand = new Random(SEED);
        // Random rand1 = new Random(SEED);
        // System.out.println( rand.nextInt() + " " + rand.nextInt() + " " + rand.nextDouble() + " " + rand.nextDouble() + " ");
        // System.out.println( rand1.nextInt() + " " + rand1.nextInt() + " " + rand1.nextDouble() + " " + rand1.nextDouble() + " ");
    // }
}
