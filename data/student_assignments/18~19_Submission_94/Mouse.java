import java.util.List;
import java.util.Random;

/**
 * A simple model of a mouse.
 * Mice age, eat, move, breed, and die.
 *
 * @version 2019.02.21
 */
public class Mouse extends Animal {
    // Characteristics shared by all mice (class variables).
    
    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a mouse can live.
    private static final int MAX_AGE = 30;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The likelihood of a mouse breeding when the weather is changed.
    private static double NEW_BREEDING_PROBABILITY;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new mouse. A mouse may be created not only with age
     * zero (a new born) or with a random age but also with disease.
     * 
     * @param randomAge If true, the mouse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param gender The gender of a mouse. (Male, Female)
     * @param isSick If true, the mouse is sick.
     */
    public Mouse(boolean randomAge, Field field, Location location, String gender, boolean isSick)
    {
        super(randomAge, field, location, gender, isSick);
    }

    /**
     * Check wheather the parameter is food or not.
     * 
     * @return wheather the parameter is food or not.
     * @param object the object the mouse is going to eat.
     */
    public boolean isFood(Object object) 
    {
        if (object instanceof Corn) {
            return true;
        }

        return false;
    }
    
    /**
     * This is what the mouse does most of the time - it runs 
     * around and eats the corns. Sometimes it will breed, die of hunder or old age or get disease.
     * 
     * @param newMice A list to return newly born mice.
     * @param isDay Indicate wheather it is a day or night.
     * @param weather The weather the mouse is in.
     */
    public void act(List<Animal> newMice, boolean isDay, String weather)
    {
        SpreadDisease();
        
        weatherEffect(weather);
        
        incrementAge(MAX_AGE);
        incrementHunger();
        incrementThirsty();
        diseaseCauseDeath();
        if(isAlive()) {
            if(findMate()) {
                giveBirth(newMice);
            }
            Location newLocation = findFood();
            if(newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                setDead();
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * 
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        int births = 0;
        if (canBreed(BREEDING_AGE) && rand.nextDouble() <= NEW_BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * Check whether or not this mouse is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newMice A list to return newly born mice.
     */
    public void giveBirth(List<Animal> newMice)
    {
        // New mice are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            String gender = Randomizer.getRandomGender();
           
            boolean isSick = false;
            if (isSick())
            {
                isSick = Randomizer.getRandomIsSick();
            }
            Mouse young = new Mouse(false, field, loc, gender, isSick);
            newMice.add(young);
        }
    }
    
    /**
     * Check wheather the adjacent mouse can be a partner(mate).
     * 
     * @param object The adjacent object.
     * @return wheather or not the adjacent mouse can be a partner.
     */
    public boolean isMate(Object object) 
    {
        boolean result = false;

        if (object instanceof Mouse) {
            Mouse mouse = (Mouse) object;
            
            if (mouse.isAlive() && mouse.canBreed(BREEDING_AGE)&& !(mouse.getGender().equals(this.getGender()))) {
                result = true;
            }
        }

        return  result;
    }

    /**
     * According to the weather, the mouse get effected on the breeding probability.
     * 
     * @param weather The string of weather.
     */
    public void weatherEffect (String weather)
    {
        
        if (weather.equals("Sunny")){
            NEW_BREEDING_PROBABILITY = BREEDING_PROBABILITY;
        }
        else if (weather.equals("Cloudy")){
            NEW_BREEDING_PROBABILITY = BREEDING_PROBABILITY*0.9;
        }
        else if (weather.equals("Rainy")){
            NEW_BREEDING_PROBABILITY = BREEDING_PROBABILITY*0.8;
        }     
        else if (weather.equals("Windy")){
            NEW_BREEDING_PROBABILITY = BREEDING_PROBABILITY*0.85;
        }
    }
}
