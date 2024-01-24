
/**
 * A class representing shared characteristics of all organisms.
 *
 * @version 2020.02
 */
public abstract class Organism implements Actor
{
    // Whether the organism is alive or not.
    private boolean alive;
    
    /**
     * Create a new organism at a location in the field
     */
    public Organism()
    {
        alive = true;
    }
    
    /**
     * Check whether the organism is active/alive or not.
     * @return true if the organism is active/alive.
     */
    public boolean isActive()
    {
        return alive;
    }
    
    /**
     * Set the organism's state to inactive
     */
    public void setInactive()
    {
        alive = false;
    }
    
    /**
     * Set the organism's state to inactive
     */
    protected abstract void getEaten();
    
    /**
     * @return the organism's food value to its predator
     */
    protected abstract int getFoodValue();
    
    /**
     * @return true, if the organism is available to be eaten
     */
    protected abstract boolean isAvailable();
}
