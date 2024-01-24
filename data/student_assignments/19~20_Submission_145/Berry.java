import java.util.List;
import java.util.Random;

/**
 * A simple model of berries.
 * Berries grow, spread and die.
 * 
 * @version 2020.02.22
 */
public class Berry extends Plant
{
    // Characteristics shared by all berries (class variables).

    // The age at which berries can start to reproduce.
    private static final int SPREADING_AGE = 5;
    // The age to which berries can live.
    private static final int MAX_AGE = 50;
    // The likelihood of berries spreading.
    private static final double SPREADING_PROBABILITY = 0.09;
    // The maximum number of reproductions via seeds.
    private static final int MAX_SEED_NUM = 4;
    // The likelihood of berry getting infected.
    private static final double INFECTED_PROBABILITY = 0.2;
    // The suitable weather required for the berries to grow.
    private static final String SUITABLE_WEATHER = "windy";
    // A shared random number generator to control spreading.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The berry's age.
    private int age;

    /**
     * Create  new berry. Berry may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the berry will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Berry(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }

        // set isInfected if within the INFECTED_PROBABILITY  
        if(Math.random() <= INFECTED_PROBABILITY){
            setIsInfected(true);
        }
        else{
            setIsInfected(false);
        }
    }

    /**
     * This is what the berries do most of the time - it spreads 
     * around. Sometimes it will spread or die of old age.
     * Berries spread around during day time.
     * 
     * @param newBerries  A list to return newly born berries.     
     * @param isDayTime A boolean which tells whether the current time is day or night.
     * @param currentWeather A String to denote the current weather.
     */
    public void act(List<Plant> newBerries, boolean isDayTime, String currentWeather)
    {
        if(isDayTime){  // berries are only moving at day time
            incrementAge();
            if(isAlive()) {
                produceSeeds(newBerries, currentWeather);   
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the berry's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this berry is to produce seeds at this step.
     * New productions will be made into free adjacent locations.
     * 
     * @param newBerries    A list to return newly born berries.
     * @param currentWeather A String to denote the current weather.
     */
    private void produceSeeds(List<Plant> newBerries, String currentWeather)
    {
        // New berries are produced into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if(Math.random() < 0.5){    // the berry have 50% of chance to produce seeds
            int seeds = spread();
            if(super.checkSuitableWeather(currentWeather)) {    // if is suitableWeather, double the seed num
                seeds *= 2;
            }

            for(int b = 0; b < seeds && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Berry young = new Berry(false, field, loc);
                newBerries.add(young);
            }
        }
    }

    /**
     * Generate a number representing the number of seeds,
     * if it can spread.
     * 
     * @return int  The number of rhizomes(may be zero).
     */
    private int spread()
    {
        int seeds = 0;
        if(canSpread() && rand.nextDouble() <= SPREADING_PROBABILITY) {
            seeds = rand.nextInt(MAX_SEED_NUM) + 1;
        }
        return seeds;
    }

    /**
     * A berry can spread if it has reached its spreading age.
     * 
     * @return boolean  True if the berry can spread, false otherwise.
     */
    private boolean canSpread()
    {
        return age >= SPREADING_AGE;
    }

    /**
     * Returns the suitable weather for the growth.
     * 
     * @return String   The suitable weather for growth.
     */
    public String getSuitableWeather()
    {
        return SUITABLE_WEATHER;
    }
}
