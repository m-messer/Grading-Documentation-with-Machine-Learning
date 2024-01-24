
/**
 * Tracks whether it is day or night in the field. 
 * Some animals do not move during the night. 
 * Some animals hunt exclusively during the night. 
 *
 * @version 17.02.2021
 */
public class TimeOfDay
{
    private static String timeOfDay = "day"; 
    private static int days = 0; 
    /**
     * Constructor for objects of class TimeOfDay
     */
    public TimeOfDay()
    {
        //nothing
    }

    /**
     * Change the time of day (day or night).
     * If day, change to night. 
     * If night, change to day.
     */
    public void changeTime()
    {
        if(timeOfDay.equalsIgnoreCase("day")){
            timeOfDay = "night";
        }
        else{
            timeOfDay = "day";
            days++;
        }
    }
    
    /**
     * Change the time of day (day or night) to the time set in 
     * the paramater. 
     */
    public void setTime(String time)
    {
        timeOfDay = time; 
    }
    
    /**
     * Returns the current time (day or night)
     *
     * @return the current time
     */
    public String getTime()
    {
        return timeOfDay; 
    }
    
    /**
     * @return the number of days passed in the field
     */
    public int getDaysPassed()
    {
        return days; 
    }
    
    /**
     * Set days passed to 0
     *
     */
    public void resetDaysPassed()
    {
        days = 0; 
    }
}
