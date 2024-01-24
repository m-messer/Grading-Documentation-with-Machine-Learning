/**
 * The time of day in the simulation.
 * "Time" keeps track of the time of day and changes
 * accordingly from day to night and night to day every 
 * period.
 *
 * @version 2021.03.03
 */
public class Time
{
    // True if the current time is daytime.
    private boolean isDay;

    /**
     * Construct the time of day for simulation.
     */
    public Time()
    {
        // The simulation starts during daytime.
        isDay = true;
    }

    /**
     * Returns true if it is daytime.
     * @returns true if daytime, otherwise, return false.
     */
    public boolean getIsDay()
    {
        return isDay;
    }
    
    /**
     * Returns a short description of the time of day.
     * @return The String of time of day.
     */
    public String getString()
    {
        if(isDay) {
            return "Day";
        } else {
            return "Night";
        }
    }
    
    /**
     * Changes the time of day.
     */
    public void changeTime()
    {
        isDay = !isDay;
    }
}