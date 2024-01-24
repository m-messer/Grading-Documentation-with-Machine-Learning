import java.util.*;

/**
 * A abstract class representing the characteristics of all
 * trees(acacia trees).
 *
 * @version 2020.02.23
 */
abstract public class Tree extends Plant
{
    //The treeÂ´s size
    private int size;
    // A shared random number generator to control age.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new tree at location in field.
     * If the trees age is random, its size is a
     * random value between 0 and twice its maximum age
     * if the age is not random, the size is set to 1
     * 
     * @param randomAge if True starts with random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tree(boolean randomAge, Field field, Location location) 
    {
        super(randomAge, field, location);
        size = 1;
        if(randomAge) {
            size = rand.nextInt(2*getMaxAge());
        }
    }

    /**
     * The growth method increments the size of the tree
     */
    public void grow() {
        size += getGrowingRatio();
    }

    /**
     * Decrease the size when it gets bites from animals
     * @param decreaseRatio Decrease Ratio size damage
     */
    public void decreaseSize(double decreaseRatio) {
        size -= decreaseRatio;
    }

    /**
     * Gets the current size of the tree
     * @return Current Tree size
     */
    public double getSize() {
        return size;
    }

    /**
     * Abstract method - retrieves maximum age of the tree
     */
    abstract public int getMaxAge();

    /**
     * Abstract method - retrieves Growth Ratio of the tree
     */
    abstract public double getGrowingRatio();
}
