import java.awt.*;

/**
 * A simple model of a Cow.
 * Cows age, move, eat grasses, reproduce and die.
 *
 */
public class Cow extends Animal{

    /**
     * Create an cow with a specific point.
     * 
     * @param point The point on the habitat.
     * @param adult Whether this Cow should be instantiated as an adult or a new born.
     */
    protected Cow(Point point, boolean adult) {
        super(point, adult);
    }

    /**
     * Gets an offspring from this Cow.
     * 
     * @param point The location of the offspring.
     * 
     * @return A Cow offspring.
     */
    @Override
    protected Animal makeOffspring(Point point) {
        return new Cow(point, false);
    }

    /**
     * Gets the maximum number of offsprings this Cow could have at once. 
     * 
     * @return 7.
     */
    @Override
    int maxOffspring() {
        return 7;
    }
    
    /**
     * Gets the preys of this Cow.
     * 
     * @return Pigman.class inside an array. 
     */
    @Override
    Class[] foodSource() {
        return new Class[] {Grass.class};
    }

    /**
     * Gets whether this Cow is active at night or day
     * 
     * @return false as it is not active at night.
     */
    @Override
    boolean nocturnal() {
        return false;
    }

    /**
     * Gets the probability that this Cow could reproduce.
     * 
     * @return 1.
     */
    @Override
    double reproductionProbability() {
        return 1;
    }

    /**
     * Gets the probability that this Cow would catch a STD on instantiation.
     * 
     * @return 0.3.
     */
    @Override
    double stdProbability() {
        return 0.3;
    }

    /**
     * Gets the color of this Cow.
     * 
     * @return Orange.
     */
    @Override
    public Color color() {
        return Color.decode("#FFD580");
    }

    /**
     * Gets the maximum age of this Cow.
     * 
     * @return 40. 
     */
    @Override
    protected int maxAge() {
        return 40;
    }

    /**
     * The age that this Cow must reach before it could reproduce.
     * 
     * @return 6.
     */
    @Override
    int pubertyAge() {
        return 6;
    }

    /**
     * The food value of each prey. 
     * 
     * @return 2.
     */
    @Override
    int foodValue() {
        return 2;
    }
}
