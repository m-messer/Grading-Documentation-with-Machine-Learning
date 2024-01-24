import java.util.List;
import java.util.Random;
/**
 * OAK TREE.
 * These's take a few days to grow but once they do the rabbits and squirrels fest upon them. 
 *
 * @version (12/02/2019)
 */
public class OakTree extends Plants 
{ 
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The Oak trees's age.
    private int age;

    /**
     * Create a new Oak Tree. A oak tree may be created with age
     * zero a saplin or with a random age.
     * 
     * @param randomAge If true, the oak tree will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public OakTree(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        
        Random random = Randomizer.getRandom();
        // Initial age of the tree
        age = 0;
        // Maximum age of the tree
        MAX_AGE = 100;
        // The probability that a tree will produce anthoer 
        BREEDING_PROBABILITY = 0.5;
        // The age at which the tree can start to grow new trees
        BREEDING_AGE = 2;
        // The maximum number of new trees that can be grown 
        MAX_LITTER_SIZE = 20;
        
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
        
    /**
     * Return the type of Oak tree
     */
    public Base returnMyType(boolean randomAge, Field field, Location location){
        return new OakTree(true, field, location);
    }
}

