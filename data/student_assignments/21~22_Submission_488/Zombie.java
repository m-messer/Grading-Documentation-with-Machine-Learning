import java.awt.*;

/**
 * A simple model of a Zombie.
 * Zombies age, move, eat cows, pigman and chickens, reproduce and die.
 *
 */
public class Zombie extends Animal{

    /**
     * Create an Zombie with a specific point.
     * 
     * @param point The point on the habitat.
     * @param adult Whether this Zombie should be instantiated as an adult or a new born.
     */
    protected Zombie(Point point, boolean adult) {
        super(point, adult);
    }

    /**
     * Gets an offspring from this Zombie.
     * 
     * @param point The location of the offspring.
     * 
     * @return A Zombie offspring.
     */
    @Override
    protected Animal makeOffspring(Point point) {
        return new Zombie(point, false);
    }

    /**
     * Gets the maximum number of offsprings this Zombie could have at once. 
     * 
     * @return 2.
     */
    @Override
    int maxOffspring() {
        return 2;
    }
    
    /**
     * Gets the preys of this Zombie.
     * 
     * @return Cow.class inside an array. 
     */
    @Override
    Class[] foodSource() {
        return new Class[] {Pigman.class, FriedChicken.class, Cow.class};
    }

    /**
     * Gets whether this Zombie is active at night or day
     * 
     * @return true as it is only active at night.
     */
    @Override
    boolean nocturnal() {
        return true;
    }

    /**
     * Gets the probability that this Zombie could reproduce.
     * 
     * @return 0.4.
     */
    @Override
    double reproductionProbability() {
        return 0.4;
    }

    /**
     * Gets the probability that this Zombie would catch a STD on instantiation.
     * 
     * @return 0.2.
     */
    @Override
    double stdProbability() {
        return 0.2;
    }

    /**
     * Gets the color of this Zombie.
     * 
     * @return Blue.
     */
    @Override
    public Color color() {
        return Color.decode("#ADD8E6");
    }

    /**
     * Gets the maximum age of this Zombie.
     * 
     * @return 150. 
     */
    @Override
    protected int maxAge() {
        return 150;
    }

    /**
     * The age that this Zombie must reach before it could reproduce.
     * 
     * @return 20.
     */
    @Override
    int pubertyAge() {
        return 20;
    }

    /**
     * The food value of each prey. 
     * 
     * @return 4.
     */
    @Override
    int foodValue() {
        return 4;
    }
}
