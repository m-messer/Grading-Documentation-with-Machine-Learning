import java.awt.*;

/**
 * A simple model of a grass.
 * A grass grows and dies. 
 * It has a color and maximum age. 
 *
 */
public class Grass extends Organism {

     /**
     * Create a new grass at a specific point. 
     * 
     * @param point The point on the habitat.
     */
    public Grass(Point point) {
        super(point);
    }

     /**
     * Gets the color of this grass.
     * 
     * @return Dark green.
     */
    @Override
    public Color color() {
        return Color.decode("#006400");
    }

    /**
     * Let the grass grow. It would die if it reaches its maximum age. 
     * 
     */
    public void grow() {
        if(naturalDeath())
            setDead();
    }

    /**
     * Gets the maximum age of this grass.
     * 
     * @return 20. 
     */
    @Override
    protected int maxAge() {
        return 20;
    }

    /**
     * Calculates whether this grass would die due to aging. 
     * 
     * @return true if the grass dies, false it not.
     */
    @Override
    protected boolean naturalDeath() {
        incrementAge();
        return age() >= maxAge();
    }
}
