
/**
 * Write a description of class Clock here.
 *
 * @version (a version number or a date)
 */
public class Clock
{
    // Counter for steps locally
    private int tick = 0;
    // Boolean for if night
    private boolean isNight = false;
    
    /**
     * Constructor for clock
     */
    public Clock()
    {
        
    }
    
    /**
     * Function which increments the tick based on main programs tick
     * @param int step
     */
    public void tick(int step) {
        tick = step;
        isNight = !isNight;
    }
    
    /**
     * Accessor func for isNight
     * @return boolean isnight
     */
    public boolean isNight() {
        return isNight;
    }
}
