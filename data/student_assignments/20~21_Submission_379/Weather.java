import java.util.ArrayList; 
import java.util.Random;

/**
 * The weather in the simulation.
 * Different weathers are simulated which affects the
 * behaviour of participating objects in the simulator.
 * The weather can change randomly during different periods.
 *
 * @version 2021.03.03
 */
public class Weather
{
    // Stores the types of weather in the simulation.
    private ArrayList<String> weatherTypes;
    // Stores the sequence of weathers that happened.
    private ArrayList<String> weatherHistory;
    // The previous weather in the simulation.
    private String previous;
    // The current weather in the simulation.
    private String current;
    // The count of a recurring weather type.
    private int count;

    /**
     * Construct a weather simulation.
     */
    public Weather()
    {
        weatherTypes = new ArrayList<>();
        addWeatherTypes();
        weatherHistory = new ArrayList<>();
        previous = "";
        current = "";
        changeWeather();
        count = 1;
    }

    /**
     * Returns the current weather.
     * @return The String of the current weather.
     */
    public String getCurrent()
    {
        return current;
    }

    /**
     * Add different weather types to be simulated.
     */
    private void addWeatherTypes()
    {
        weatherTypes.add("Rainy");
        weatherTypes.add("Sunny");
        weatherTypes.add("Cloudy");
    }

    /**
     * Changes the current weather by random.
     */
    public void changeWeather()
    {
        Random rand = Randomizer.getRandom();
        int weather = rand.nextInt(weatherTypes.size());

        previous = current;
        if (current.equals("Rainy") && count == 3) {
            current = "Foggy";
        } else {
            current = weatherTypes.get(weather);
        }
        addWeatherHistory(current);

        if (previous.equals(current)) {
            count++;
        } else {
            count = 1;
        }
    }

    /**
     * Add current weather into the history of weathers simulated.
     * @weather The current weather.
     */
    private void addWeatherHistory(String weather)
    {
        weatherHistory.add(weather);
    }

    /**
     * Returns true if there was no rain and no sun for 
     * 3 consecutive periods.
     * @return true if there was no rain and no sun for  
     * 3 consecutive periods. Otherwise, false.
     */
    public boolean noRainAndSun()
    {
        int size = weatherHistory.size();
        if (size > 3) {
            boolean noRain = true;
            boolean noSun = true;
  
            for(int i = size - 4; i < size-1; i++) {
                if(weatherHistory.get(i).equals("Rainy")) {
                    noRain = false;
                }

                if(weatherHistory.get(i).equals("Sunny")) {
                    noSun = false;
                }
            }
            
            return noRain && noSun;
        }
        
        return false;
    }

}
