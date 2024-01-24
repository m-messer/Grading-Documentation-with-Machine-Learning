import java.awt.*;

/**
 * A simple model of a Witch.
 * Witches  age, move, eat cows, pigman and chickens, reproduce and die.
 *
 */
public class Witch extends Animal{

    /**
     * Create an Witch with a specific point.
     * 
     * @param point The point on the habitat.
     * @param adult Whether this Witch should be instantiated as an adult or a new born.
     */
    protected Witch(Point point, boolean adult) {
        super(point, adult);
    }

    /**
     * Gets an offspring from this Witch.
     * 
     * @param point The location of the offspring.
     * 
     * @return A Witch offspring.
     */
    @Override
    protected Animal makeOffspring(Point point) {
        return new Witch(point, false);
    }

    /**
     * Gets the maximum number of offsprings this Witch could have at once. 
     * 
     * @return 2.
     */
    @Override
    int maxOffspring() {
        return 2;
    }
    
    /**
     * Gets the preys of this Witch.
     * 
     * @return Cow.class inside an array. 
     */
    @Override
    Class[] foodSource() {
        return new Class[] {Cow.class, Pigman.class, FriedChicken.class};
    }

    /**
     * Gets whether this Witch is active at night or day
     * 
     * @return false as it is not active at night.
     */
    @Override
    boolean nocturnal() {
        return false;
    }

    /**
     * Gets the probability that this Witch could reproduce.
     * 
     * @return 0.5.
     */
    @Override
    double reproductionProbability() {
        return 0.5;
    }

    /**
     * Gets the probability that this Witch would catch a STD on instantiation.
     * 
     * @return 0.3.
     */
    @Override
    double stdProbability() {
        return 0.3;
    }

    /**
     * Gets the color of this Witch.
     * 
     * @return Purple.
     */
    @Override
    public Color color() {
        return Color.decode("#330066");
    }

    /**
     * Gets the maximum age of this Witch.
     * 
     * @return 100. 
     */
    @Override
    protected int maxAge() {
        return 100;
    }

    /**
     * The age that this Witch must reach before it could reproduce.
     * 
     * @return 25.
     */
    @Override
    int pubertyAge() {
        return 25;
    }

    /**
     * The food value of each prey. 
     * 
     * @return 5.
     */
    @Override
    int foodValue() {
        return 5;
    }
}
