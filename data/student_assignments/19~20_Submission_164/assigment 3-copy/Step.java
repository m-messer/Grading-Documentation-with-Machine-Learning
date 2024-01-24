
/**
 * This class represents one step being taken in the simulator. 
 *
 * @version (1.0)
 */
public class Step
{
    // A counter representing the number of steps taken in the simulator
    private int step;
    // This shows whether the step represents a day or a night
    private boolean day;
    // A field to hold the weather occuring in each step
    private Weather weather;
    

    /**
     * Constructor for objects of class Step
     */
    public Step()
    {
        step = 0;
        day = true;
    }

    /**
     * Sets the day/night status of each step. Two steps represent each day
     */
    public void setDay()
    {
       if ( step % 4 <= 1) { 
           day = true; 
        }
       else {
          day = false;
        } 
    }
    
    /**
     * Sets the season status of each step. Ten steps represent each season
     */
    public void setSeason()
    {
        if ( step % 20 == 0 ){
            weather.setSummer();
        }
        else if (step % 20 == 10){
            weather.setSummer();
        }
    }
    
    /**
     * Returns whether it is day or not
     */
    public boolean isDay()
    {
       return day;
    }
    
    /**
     * Returns the number of steps taken
     */
    public int getStep()
    {
        return step;
    }
    
    /**
     * Increments the number of steps taken by one
     */
    public void incrementStep()
    {
        step ++;
    }
    
    /**
     * Resets the number of steps taken to zero
     */
    public void reset()
    {
        step = 0;
    }
    
}
