import java.util.Random;
/**
 * A class defining and returning the environmental  
 * factors of the field i.e time of day, number of 
 * day, day or night and if it is raining.
 *
 * @version (a version number or a date)
 */
public class EnviromentalFactors
{
    // The current time of day in hours
    int currentTime; 
    // If it is night or not (day)
    boolean isNight;
    // Number of day in simulation
    int day;
    // Whether it is raining or not 
    boolean isRaining;
    // A random boolean generator to provide randomly if it is raining.
    private Random rand = Randomizer.getRandom();
    
    /**
     * Define values for enviromental factors
     */
    public EnviromentalFactors()
    {
        //start the simulation at midnight
        isNight = true;
        currentTime = 0;
        day = 0;
        isRaining = false;        
    }
    
    /**
     * Increment time of day and call methods to check if it is new day 
     * and if it is night 
     */
    public void nextHour()
    {
        currentTime++;
        checkNewDay();
        checkNight();
    }
    
    /**
     * If hour of day is equal to 24 reset 
     * it to 0(midnight) and increment number of day 
     * and set randomly if it is raning or not
     */
    public void checkNewDay()
    {
        if(currentTime >= 24)
        {
            currentTime = 0;
            day ++;
            isRaining = rand.nextBoolean();
        }
    }
    
    /**
     * set boolean value for isNight according to 
     * what time period (hours) of day it is 
     * between 6am and 7pm it is daytime 
     */
    private void checkNight()
    {
        if((currentTime >= 6) && (currentTime <= 19))
        {
            isNight = false;
        }
        else
        {
            isNight = true;
        }
    }
            
    /**
     * @return Current time (hours) of the day 
     */
    public int getTime()
    {
        return currentTime;
    }
    
    /**
     * @return true If it is night time 
     */
    public boolean getIsNight()
    {
        return isNight;
    }
    
    /**
     * @return Number of day in simulation 
     */
    public int getDay()
    {
        return day;    
    }
    
    /**
     * @returb true If it is raining
     */
    public boolean getIsRaining()
    {
        return isRaining;
    }
}
