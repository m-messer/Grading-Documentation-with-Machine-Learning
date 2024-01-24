import java.util.ArrayList;
import java.util.Random;
/**
 * This class is responsible for simulating different weather conditions.
 * Using a String ArrayList, this class will randomly select from five
 * different weather conditions every time there is a change in the time
 * of day.
 * 
 * Every condition has different implications:
 * 
 * "clear" - clear weather, the simulation runs as normal
 * "fog" - foggy weather, the predators cannot see well and thus
 *         can't hunt their prey
 * "rain" - it is raining, grass can grow
 * "blizzard" - a blizzard hits the habitat, animals can't act because
 *              of the cold
 * "heat" - the temperature rises, animals can't propagate during
 *          this weather condition
 *
 *         
 * @version 2021.03.02
 */
public class Weather
{
    private ArrayList<String> weatherList;
    private Random random;

    /**
     * Constructor for objects of class Weather
     * Initialises the weatherList ArrayList and adds the weather conditions.
     *
     */
    public Weather()
    {
        weatherList = new ArrayList<>();
        random = new Random();

        weatherList.add("clear");
        weatherList.add("fog");
        weatherList.add("rain");
        weatherList.add("blizzard");
        weatherList.add("heat");
    }

    /**
     * Returns a random weather condition from the ArrayList.
     * 
     * @return weather Random weather.
     *
     */
    public String getRandomWeather()
    {
        int randomIndex = random.nextInt(weatherList.size());
        String weather = weatherList.get(randomIndex);

        return weather;
    }
}
