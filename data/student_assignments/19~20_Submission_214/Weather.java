import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * Defining weather activities of simulation.
 *
 * @version 2020.2.22
 */
public class Weather
{
    //the state of raining, if it'raining return true.
    private boolean rain;
    
    /**
     * Constract initial weather conditions.
     */
    public Weather()
    {
        rain = false;
    }

    /**
     * The behavior of raining.Plants will be active on rainy days.
     * @param plants  List of plants in the field.
     * @param view  A graphical view of the simulation
     */
    public void Rain(List<Plant> plants,SimulatorView view)
    {
        if(rain){
        view.changeToRain();
        // Provide space for newborn plants.
        List<Plant> newPlants = new ArrayList<>();
            for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            // Let all plants act.
            plant.act(newPlants);
            if(! plant.isAlive()) {
                it.remove();
            }
         }
        // Add the newly born animals to the main lists.
        plants.addAll(newPlants);
        } else{
          view.changeToSunny();
        }
    }
    
    /**
     * Get next random weather.
     */
    public void nextWeather()
    {
        Random rand = Randomizer.getRandom();
        rain = rand.nextBoolean();
    }
}
