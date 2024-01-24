import java.util.List;
import java.util.Random;

/**
 * A simple model of grass.
 * Grass grows, spreads and dies.
 * 
 * @version 2020.02.14
 */
public class Grass extends Plant
{
    // Characteristics shared by all grass (class variables).

    // The age at which grass can start to spread.
    private static final int SPREADING_AGE = 3;
    // The age to which grass can live.
    private static final int MAX_AGE = 30;
    // The likelihood of grass spreading.
    private static final double SPREADING_PROBABILITY = 0.06;
    // The likelihood of grass getting infected.
    private static final double INFECTED_PROBABILITY = 0.1;
    // The maximum number of reproductions via rhizomes.
    private static final int MAX_RHIZOME_NUM = 3;
    // The suitable weather required for the grass to grow.
    private static final String SUITABLE_WEATHER = "sunny";
    // A shared random number generator to control spreading.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The grass' age.
    private int age;

    /**
     * Create  new grass. Grass may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the tuft of grass will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
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
     * This is what the grass does most of the time - it spreads 
     * around. Sometimes it will spread or die of old age.
     * Grass is only going to spread during night time.
     * 
     * @param newGrass  A list to return newly born grass.     
     * @param isDayTime A boolean which tells whether the current time is day or night.
     * @param currentWeather A String to denote the current weather.
     */
    public void act(List<Plant> newGrass, boolean isDayTime, String currentWeather)
    {
        if(!isDayTime){     // only going to do things during night
            incrementAge();
            if(isAlive()) {
                produceRhizomes(newGrass, currentWeather);  
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the grass' death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this grass is to produce rhizomes at this step.
     * New productions will be made into free adjacent locations.
     * 
     * @param newGrass A list to return newly born grass.
     */
    private void produceRhizomes(List<Plant> newGrass, String currentWeather)
    {
        // New grass is produced into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if(Math.random() < 0.5){    // the grass has 50% of chance to produce Rhizomes
            int rhizomes = spread();
            if(super.checkSuitableWeather(currentWeather)) {    // if is suitableWeather, double the rhizomes num
                rhizomes *= 2;
            }
            for(int b = 0; b < rhizomes && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Grass young = new Grass(false, field, loc);
                newGrass.add(young);
            }
        }
    }

    /**
     * Generate a number representing the number of rhizomes,
     * if the grass can spread.
     * 
     * @return int  The number of rhizomes(may be zero).
     */
    private int spread()
    {
        int births = 0;
        if(canSpread() && rand.nextDouble() <= SPREADING_PROBABILITY) {
            births = rand.nextInt(MAX_RHIZOME_NUM) + 1;
        }
        return births;
    }

    /**
     * Grass can spread if it has reached its spreading age.
     * 
     * @return boolean  true if the grass can spread, false otherwise.
     */
    private boolean canSpread()
    {
        return age >= SPREADING_AGE;
    }

    /**
     * Returns the suitable weather for growth.
     * 
     * @return String   the suitable weather for growth.
     */
    public String getSuitableWeather()
    {
        return SUITABLE_WEATHER;
    }
}
