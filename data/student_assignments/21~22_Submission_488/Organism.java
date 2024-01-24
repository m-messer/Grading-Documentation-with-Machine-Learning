import java.awt.*;

/**
 * The Organism class. It defines the basic fields and methods for all organisms. 
 *
 */
public abstract class Organism {
    // Whether the organism is alive.
    private boolean alive = true;
    // The location of the organism.
    private Point point;
    // The age of the organism.
    private int age = 0;
    
    /**
     * Create an Organism with a specific point.
     * 
     * @param point The point on the habitat.
     */
    protected Organism(Point point) {
        this.point = point;
    }
    
    /**
     * Sets the age of this organism.
     * 
     * @param age The age;
     */
    protected void setAge(int age) {
        this.age = age;
    }
    
    /**
     * Set the organism to be dead.
     * 
     */
    public void setDead() {
        this.alive = false;
    }
    
    /**
     * Gets the location of this organism.
     * 
     * @return The point. 
     */
    public Point point() {
        return point;
    }
    
    /**
     * Gets whether the organism is alive.
     * 
     * @return true if alive, false if dead. 
     */
    public boolean isAlive() {
        return alive;
    }
    
    /**
     * Gets the age of this organism.
     * 
     * @return The age.
     */
    protected int age() {
        return age;
    }
    
    /**
     * Increments the age of this organism.
     * 
     */
    protected void incrementAge() {
        this.age++;
    }
    
    /**
     * Gets the color of the organism.
     * 
     * @return The color.
     */
    abstract public Color color();    
    
    /**
     * Gets the maximum age of this organism.
     * 
     * @return The maximum age.
     */
    abstract protected int maxAge();
    
    /**
     * Gets whether the organism would die naturally. 
     * 
     * @return true if dead, false if alive. 
     */
    abstract protected boolean naturalDeath();
}    

