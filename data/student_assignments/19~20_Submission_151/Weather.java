import java.util.Random;
/**
 * This class holds shared characteristics of the weather. 
 * The weather changes automatically, and can also be changed manually. 
 *
 * @version 21.02.2020 (2)
 */
public class Weather
{
    // instance variables - replace the example below with your own

    //a variable which stores the number of steps
    private int steps;

    //a variable which stores the time of the day
    private boolean isDay;

    //a variable which stores the current button pressed
    private boolean button;

    //a temp variable which stores the calculation of steps
    private int a;

    //a variable which stores the weather condition
    private boolean isCloudy;

    // A shared random number generator to control breeding.
    private static final Random rd = Randomizer.getRandom();

    /**
     * crating the Weather, initialising the variables
     */
    public Weather()
    {
        isDay = true;

        isCloudy = false;

        //seting the variable to false, so
        //always the simulator will be on its automatic mode
        button = false;
    }

    /**
     * this method gets the steps and current time 
     * from SimulatorView Class and then computes the
     * current weather by having the count of steps
     * 
     * if the weather is on automatic mode
     * each 30 steps the changeWeather() method weil be called
     */
    public void computeStep(int step, boolean isDay)
    {
        this.isDay = isDay;
        steps = step;

        //if the steps are divisible by 30
        a = step % 30;
        if(!button)
        {
            if(a == 0)
            {
                changeWeather();
            }
        }
    }

    /**
     * 
     * if the weather is on automatic mode
     * evary 30 steps this method weil be called
     * 
     * this method changes the weather depending on current condition
     * if the current weather is cloudy, then it generates a new 
     * random boolean and then changes the weather
     * 
     */
    public void changeWeather()
    {
        Random rd = new Random(); // creating Random object

        //every 30 steps the weather randomly changes
        isCloudy = rd.nextBoolean();
        if (!isDay && isCloudy)
        {
            isCloudy = false;
        }
    }

    /**
     * returns a string of current weather
     */
    public String showWeather()
    {   

        if(isCloudy )
        {
            return "The weather is cloudy";

        }
        else
        {
            return "The sky is clear";
        }
    }

    /**
     * returns a boolean of the current weather
     *  true if its cloudy
     *  false if its clear
     */
    public boolean isCloudy()
    {
        return isCloudy;
    }

    /**
     * this method will change the current weather to cloudy
     * and also the current mode to manual
     */
    public void cloudy()
    {
        isCloudy = true; 
        button = true;
    }

    /**
     * this method will change the current weather to clear
     * and also the current mode to manual
     */
    public void clear()
    {
        button = true;
        isCloudy = false;
    }

    /**
     * this method will change the current weather to auto
     */
    public void auto()
    {
        button = false;
    }

    /**
     * this method returns string of current weather mode
     */
    public String showMode()
    {
        if (!button)
        {
            return "(Auto mode)";
        }
        else if (button)
        {
            return "(Manual mode)";
        }
        return "MODE";
    }
}
