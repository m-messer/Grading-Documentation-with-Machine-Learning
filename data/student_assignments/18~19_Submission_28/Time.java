
/**
 * A class that keeps track of time. All variables and methods are static because time 
 * is unique throughout and public as to be accessed from anywhere
 *
 * @version 22/02/2019
 */
public class Time
{
    // A 24-hr day is represented by 400 steps. 
    // This variable receives all values in the range of 0..399
    private static int currentTime = 0;

    /**
     * Constructor for objects of class TimeOfDay
     */
    public Time()
    {
    }
    
    // Based on the current step value, the currentTime is calculated.
    public static void setTime(int step) {
        currentTime = step % 400;
    }
    
    // Returns the currentTime.
    public static int getTime()
    {
        return currentTime;
    }
    
    // Sunlight there is NOT at the current time. 0..100
    // Returning 0 means complete sunlight/daytime.
    // Returning 100 means night, darkness.
    // In between day and night, it calculates a value for each step, rising or falling by 1, appropriately, until the next night or day comes along. 
    public static int sunlight() {
        if(currentTime < 100) {
            return 0;
        } else if (currentTime < 200) {
            return (currentTime - 100);
        } else if (currentTime < 300) {
            return 100;
        } else {
            return (400 - currentTime);
        }
    }
}
