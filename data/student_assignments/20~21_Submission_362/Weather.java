
/**
 * Tracks the weather in the field. 
 * Rainy weather affects the tides causing plants to grow less 
 * during the rainy season. 
 *
 * @version 25.02.2021
 */
public class Weather
{
    private static String weather = "sunny";

    /**
     * Constructor for objects of class Weather
     */
    public Weather()
    {
        //nothing
    }

    /**
     * Changes the weather when called. 
     * If rainy, change to sunny.
     * If dry, change to sunny. 
     */
    public void changeWeather()
    {
        if(weather.equalsIgnoreCase("rainy")){
            weather = "sunny";
        }
        else{
            weather = "rainy";
        }
    }

    /**
     * Set weather to string in parameter. 
     *
     * @param weather Type of weather (rainy or sunny) 
     */
    public void setWeather(String weather)
    {
        this.weather = weather; 
    }    

    /**
     * Returns the current weather 
     *
     * @return the current weather
     */
    public String getWeather()
    {
        return weather; 
    }
}
