import java.awt.*;

/**
 * A simple model of a Fried Chicken.
 * Chickens age, move, eat grasses, reproduce and die.
 *
 */
public class FriedChicken extends Animal {

    /**
     * Create an FriedChicken with a specific point.
     * 
     * @param point The point on the habitat.
     * @param adult Whether this Fried Chicken should be instantiated as an adult or a new born.
     */
    protected FriedChicken(Point point, boolean adult) {
        super(point, adult);
    }

    /**
     * Gets an offspring from this FriedChicken.
     * 
     * @param point The location of the offspring.
     * 
     * @return A Fried Chicken offspring.
     */
    @Override
    protected Animal makeOffspring(Point point) {
        return new FriedChicken(point, false);
    }

    /**
     * Gets the maximum number of offsprings this FriedChicken could have at once.
     * 
     * @return 5.
     */
    @Override
    int maxOffspring() {
        return 5;
    }

    /**
     * Gets the preys of this FriedChicken.
     * 
     * @return Pigman.class inside an array.
     */
    @Override
    Class[] foodSource() {
        return new Class[] { Grass.class };
    }

    /**
     * Gets whether this FriedChicken is active at night or day
     * 
     * @return true as it is only active at night.
     */
    @Override
    boolean nocturnal() {
        return true;
    }

    /**
     * Gets the probability that this FriedChicken could reproduce.
     * 
     * @return 0.75.
     */
    @Override
    double reproductionProbability() {
        return 0.75;
    }

    /**
     * Gets the probability that this FriedChicken would catch a STD on instantiation.
     * s
     * @return 0.05.
     */
    @Override
    double stdProbability() {
        return 0.05;
    }

    /**
     * Gets the color of this FriedChicken.
     * 
     * @return Brown.
     */
    @Override
    public Color color() {
        return Color.decode("#C4A484");
    }

    /**
     * Gets the maximum age of this FriedChicken.
     * 
     * @return 50.
     */
    @Override
    protected int maxAge() {
        return 50;
    }

    /**
     * The age that this Fried Chicken must reach before it could reproduce.
     * 
     * @return 3.
     */
    @Override
    int pubertyAge() {
        return 3;
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
