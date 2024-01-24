
/**
 * Monitors and returns the time in the simulator.
 *
 */
public class Time
{
    // instance variables - replace the example below with your own
   
    private int hour;
    //The current time
    private int hourMode;
    // Time whether in 12hr format of 24hr (Set in simulator viewer class)

    /**
     * Constructor for objects of class Time
     */
    public Time(int hourMode)
    {
        this.hourMode = hourMode;
    }

    /**
     * For very 24 steps, we return the modulus of the total step
     * giving us the current hour in the simulator.  
     */
    public void setTime(int steps)
    {
        hour = steps%hourMode;
    }
    
    /**
     * Returns the current time
     */
    public int getTime(){
        return hour;
    }
    
    /**
     * Sets the field is night to true between the hours off 20:00 and 06:00
     */
    public boolean isNight(){
        return (hour < 6 || hour > 20 || hour == 0);
    }
}
