import java.util.Random;
import java.util.ArrayList;
/**
 * Represent the weather of the field.
 * It might effect on the breeding probability of either animals or plants.
 *
 * @version 2019.02.21
 */
public class Weather
{
    // The list of 4 kinds of weather.
    private static ArrayList<String> weathers;
    // The string of the weather.
    private static String weather;
    // A random number generator for providing random weather.
    private static Random rand;
    /**
     * Construct a weather object.
     */
    public Weather()
    {
        weathers = new ArrayList<>();
        weathers.add("Sunny");
        weathers.add("Windy");
        weathers.add("Rainy");
        weathers.add("Cloudy");
        
        rand = Randomizer.getRandom();
        
        weather = "Sunny";
    }

    /**
     * @return random string of weather.
     */
    public static String getRandomWeather()
    {
        weather = weathers.get(rand.nextInt(weathers.size() - 1));
        return weather;
    }
}