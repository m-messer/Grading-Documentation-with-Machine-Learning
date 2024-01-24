import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * Write a description of class Wether here. TESTING
 *
 * @version 2022.02.27 (1)
 */
public class Weather
{
    // A string which holds the current weather of the simulation
    private String currentWeather;
    
    //A double value which holds the current strength of the weather
    private double weatherStrength;
    
    // Random class variable
    private static final Random rand = Randomizer.getRandom();
    
    // A check if a new weather instance is present
    private boolean weatherInstantiate = false;
    // Int representing length of current weather
    private int weatherLength = 0;
    // Int representing length of post weather effects
    private int postWeatherEffectsLength = 0;
    // Int representing length of weather change cooldown
    private int weatherCooldown = 0;
    
    // Probablity it is sunny
    private static final double SUNNY_PROBABILITY = 0.1;
    // Probablity it is raining
    private static final double RAIN_PROBABILITY = 0.1;
    // Probablity it is stormy
    private static final double STORM_PROBABILITY = 0.1;
    
    /**
     * Constructor for objects of class Weather
     */
    public Weather()
    {
        currentWeather = "Clear";
    }

    /**
     * Accessor function for current weather
     * @return The current wether of the simulation
     */
    public String getCurrentWeather()
    {
        return(currentWeather);
    }
    
    /**
     * Function to run effects of current weather
     * @param species - List type of all species that are alive
     */
    public void act(List<Species> species) {
        switch(currentWeather) {
            case "Clear":
                actClear(species);
                return;
            case "Sunny":
                actSunny(species);
                return;
            case "Rain":
                actRain(species);
                return;
            case "Storm":
                actStorm(species);
                return;
            default:
                return;
        }
    }
    
    /**
     * Function to run effects of clear weather
     * Generates random int and chooses if new weather
     * @param species - List type of all species that are alive
     */
    public void actClear(List<Species> species) {
        if (weatherCooldown > 0) {
            return;
        } else {
            if (rand.nextDouble() <= SUNNY_PROBABILITY) {
                currentWeather = "Sunny";
            } else if (rand.nextDouble() <= RAIN_PROBABILITY) {
                currentWeather = "Rain";
            } else if (rand.nextDouble() <= STORM_PROBABILITY) {
                currentWeather = "Storm";
            } else {
                return;
            }
            weatherCooldown = rand.nextInt(20);
            weatherLength = rand.nextInt(200);
            weatherInstantiate = true;
            postWeatherEffectsLength = rand.nextInt(30);
        }
    }
    
    /**
     * Function to run effects of sunny weather
     * doubles breed prob of plants
     * @param species - List type of all species that are alive
     */
    public void actSunny(List<Species> species) {
        if (weatherInstantiate) {
            postWeatherEffectsLength = 1;
            weatherCooldown = 0;
        } else if (weatherLength > 0) {
            weatherLength--;
            for(Iterator<Species> it = species.iterator(); it.hasNext(); ) {
                Species _species = it.next();
                if (_species instanceof Plant) {
                    Plant plant = (Plant) _species;
                    plant.setBreedingProbMult(2);
                }
            }
            return;
        } else if (postWeatherEffectsLength > 0) {
            postWeatherEffectsLength--;
            for(Iterator<Species> it = species.iterator(); it.hasNext(); ) {
                Species _species = it.next();
                if (_species instanceof Plant) {
                    Plant plant = (Plant) _species;
                    plant.setBreedingProbMult(1);
                }
            }
            return;
        } else {
            currentWeather = "Clear";
        }
    }
    
    /**
     * Function to run effects of raining weather
     * doubles breed prob of plants
     * @param species - List type of all species that are alive
     */
    public void actRain(List<Species> species) {
        if (weatherInstantiate) {
            postWeatherEffectsLength = 1;
            weatherCooldown = 0;
        } else if (weatherLength > 0) {
            weatherLength--;
            for(Iterator<Species> it = species.iterator(); it.hasNext(); ) {
                Species _species = it.next();
                if (_species instanceof Plant) {
                    Plant plant = (Plant) _species;
                    plant.setBreedingProbMult(2);
                }
            }
            return;
        } else if (postWeatherEffectsLength > 0) {
            postWeatherEffectsLength--;
            for(Iterator<Species> it = species.iterator(); it.hasNext(); ) {
                Species _species = it.next();
                if (_species instanceof Plant) {
                    Plant plant = (Plant) _species;
                    plant.setBreedingProbMult(1);
                }
            }
            return;
        } else {
            currentWeather = "Clear";
        }
    }
    
    /**
     * Function to run effects of stormy weather
     * halves breed prob of plants and has random chance of death
     * @param species - List type of all species that are alive
     */
    public void actStorm(List<Species> species) {
        if (weatherInstantiate) {
            postWeatherEffectsLength = 1;
            weatherCooldown = 0;
        } else if (weatherLength > 0) {
            weatherLength--;
            for(Iterator<Species> it = species.iterator(); it.hasNext(); ) {
                Species _species = it.next();
                if (_species instanceof Plant) {
                    Plant plant = (Plant) _species;
                    plant.setBreedingProbMult(0.5);
                }
                if (rand.nextInt(10000) <= 1) {
                    _species.setDead();
                }
            }
            return;
        } else if (postWeatherEffectsLength > 0) {
            postWeatherEffectsLength--;
            for(Iterator<Species> it = species.iterator(); it.hasNext(); ) {
                Species _species = it.next();
                if (_species instanceof Plant) {
                    Plant plant = (Plant) _species;
                    plant.setBreedingProbMult(1);
                }
            }
            return;
        } else {
            currentWeather = "Clear";
        }
    }
}
