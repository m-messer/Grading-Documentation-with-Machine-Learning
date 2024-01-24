import java.util.Random;
/**
 * A Class representing weather in the simulation.
 *
 * @version 2020.02
 */
public class Weather 
{
    boolean isFog = false;
    boolean isRaining = false;

    boolean isFlooding = false;
    boolean isDroughting = false;

    // determines how many more steps of the current weather condition are left
    int fogTime = 0;
    int weatherSteps = 0;

    // thresholds for how long a weather condition needs to last before a flood/drought occurs.
    double floodingThreshold = 0.7;
    double droughtThreshold = 0.3;

    // value between 0 and 1, where >0.5 is raining, else sunny.
    double currentRainOrSun = 0.5;

    // set the maximum weather length before a new one occurs
    int maxWeatherLength = 24;

    // set the minimum weather length before a new one occurs
    int minWeatherLength = 12;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for a weather object
     */
    public Weather()
    {

    }

    /**
     * @return true if it is currently flooding.
     */
    public boolean isFlooding()
    {
        return isFlooding;
    }

    /**
     * @return true if it is currently droughting. 
     */
    public boolean isDroughting()
    {
        return isDroughting;
    }

    /**
     * Determine if fog is currently occuring.
     */
    public void determineFog()
    {
        isFog = fogTime > 0;
        if (!isFog && rand.nextDouble()<0.01) {fogTime = rand.nextInt(25) + 12; } // value between 12 and 24 inclusive
    }

    /**
     * Determine if a drought or flood is currently occuring, and if it is raining or sunny.
     */
    public void determineStates()
    {
        if (currentRainOrSun > 0.5) isRaining = true;
        else isRaining = false;

        if (currentRainOrSun > floodingThreshold) {
            isFlooding = true; 
            isDroughting = false;
        }
        else if (currentRainOrSun < droughtThreshold) {
            isDroughting = true; 
            isFlooding = false;
        }
        else {
            isDroughting = false;
            isFlooding = false;
        }
    }

    /**
     * Decrement the amount of time that rain and/or fog is currently running for. If they're not running, do nothing.
     */
    public void step()
    {
        // decrement the amount of time
        fogTime--;
        weatherSteps--;

        // determine if a a new weather state may occur
        determineFog();

        if (weatherSteps <= 0) {
            currentRainOrSun = rand.nextDouble();
            weatherSteps = rand.nextInt(maxWeatherLength) + minWeatherLength;
            determineStates();
        }
    }

    /**
     * @return true if it's foggy
     */
    public boolean isFog()
    {
        return isFog;
    }

    /**
     * @return true if it's raining
     */
    public boolean isRaining()
    {
        return isRaining;
    }
}
