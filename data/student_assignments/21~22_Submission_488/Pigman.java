import java.awt.*;

/**
 * A simple model of a Pigman.
 * Pigmen age, move, eat grasses, reproduce and die.
 *
 */
public class Pigman extends Animal{

    /**
     * Create an Pigman with a specific point.
     * 
     * @param point The point on the habitat.
     * @param adult Whether this Pigman should be instantiated as an adult or a new born.
     */
    public Pigman(Point point, boolean adult) {
        super(point, adult);
    }

    /**
     * Gets the color of this Pigman.
     * 
     * @return Pink.
     */
    @Override
    public Color color() {
        return Color.pink;
    }

    /**
     * Gets the maximum age of this Pigman.
     * 
     * @return 40. 
     */
    @Override
    protected int maxAge() {
        return 40;
    }
    
    /**
     * Gets the maximum number of offsprings this Pigman could have at once. 
     * 
     * @return 8.
     */
    @Override
    int maxOffspring() {
        return 8;
    }

    /**
     * Gets the preys of this Pigman.
     * 
     * @return Grass.class inside an array. 
     */
    @Override
    Class[] foodSource() {
        return new Class[]{Grass.class};
    }

    /**
     * Gets an offspring from this Pigman.
     * 
     * @param point The location of the offspring.
     * 
     * @return A pigman offsring.
     */
    @Override
    protected Animal makeOffspring(Point point) {
        return new Pigman(point, false);
    }

    /**
     * Gets whether this Pigman is active at night or day
     * 
     * @return false as it is only active at day.
     */
    @Override
    boolean nocturnal() {
        return false;
    }

    /**
     * Gets the probability that this Pigman could reproduce.
     * 
     * @return 0.75.
     */
    @Override
    double reproductionProbability() {
        return 0.75;
    }

    /**
     * Gets the probability that this Pigman would catch a STD on instantiation.
     * 
     * @return 0.1.
     */
    @Override
    double stdProbability() {
        return 0.1;
    }

    /**
     * The age that this Pigman must reach before it could reproduce.
     * 
     * @return 5.
     */
    @Override
    int pubertyAge() {
        return 5;
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
