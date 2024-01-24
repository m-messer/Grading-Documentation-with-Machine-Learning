import java.util.*;

/**
 * A class representing the weather.
 *
 * @version 2021.03.03
 */
public class Weather {
        Map<String, Integer> weatherTypes;

    /**
     * Instantiate an object of class 'Weather'.
     */
    public Weather() {
        weatherTypes = new HashMap<>();
    }

    /**
     * Add a new type of weather
     *
     * @param weatherName name of the new weather type
     * @param weight      weight of it happening
     */
    public void addWeather(String weatherName, int weight) {
        weatherTypes.put(weatherName, weight);
    }

    /**
     * Return a randomised weather type.
     *
     * @return String random weather
     */
    public String randomiseWeather() {
        Object[] options = weatherTypes.keySet().toArray();
        int totalWeight = 0;
        for (Object weather : options) {
            totalWeight += weatherTypes.get(weather);
        }

        // Now choose a random weather.
        int idx = 0;
        for (double r = Math.random() * totalWeight; idx < options.length - 1; ++idx) {
            r -= weatherTypes.get(options[idx]);
            if (r <= 0.0) break;
        }
        return (String) options[idx];
    }
}
