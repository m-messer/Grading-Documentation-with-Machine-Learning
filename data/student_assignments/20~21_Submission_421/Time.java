/**
 * This class sets and tracks the time of day.
 * The class simulates a day and night cycle, where predators
 * should only act during the night and their prey during the day.
 *
 *         
 * @version 2021.02.27
 */
public class Time
{
    private int time;
    private boolean isNight;
    private boolean isDay;
    private Weather weather;
    private String currentWeather;

    /**
     * Constructor for objects of class Time
     *
     */
    public Time()
    {
        time = 0;
        isDay = true; //sets the initial time of day
        weather = new Weather();
    }

    /**
     * Increments the time by one every step.
     *
     */
    public void incrementTime()
    {
        time ++;
    }

    /**
     * Changes the time of day and weather conditions every 20 steps.
     *
     */
    public void setTime()
    {
        if(time == 20){
            changeTime();
            currentWeather = weather.getRandomWeather();
            time = 0;
        }
    }
    
    /**
     * Returns the current weather condition.
     * 
     * @return currentWeather The current weather condition
     *
     */
    public String getCurrentWeather()
    {
        return currentWeather;
    }

    /**
     * Changes the time of day. This is called in the setTime method.
     *
     */
    private void changeTime()
    {
        if(isNight == true)
        {
            isDay = true;
            isNight = false;
        }
        else if(isNight == false)
        {
            isDay = false;
            isNight = true;
        }
    }

    /**
     * Checks whether it is night.
     * 
     * @return isNight Returns true during nighttime.
     *
     */
    public boolean getNight()
    {
        return isNight;
    }
}
