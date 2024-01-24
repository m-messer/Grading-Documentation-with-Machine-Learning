import java.util.List;
import java.util.Random;

/**
 * A simple model of a cat.
 * Cats age, eat, move, breed, and die.
 *
 * @version 2019.02.21
 */
public class Cat extends Animal {
    // Characteristics shared by all mice (class variables).

    // The age at which a cat can start to breed.
    private static final int BREEDING_AGE = 9;
    // The age to which a cat can live.
    private static final int MAX_AGE = 60;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // The likelihood of a cat breeding.
    
    private static final double BREEDING_PROBABILITY = 0.6;
    // The likelihood of a cat breeding when the weather is changed.
    private static double NEW_BREEDING_PROBABILITY;
    // The likelihood of success that the cat hunts the mouse.
    private static final double MOUSE_HUNT_PROBABILITY = 0.7;
    // The likelihood of success that the cat hunts the mouse.
    // When the weather is changed.
    private static double NEW_MOUSE_HUNT_PROBABILITY;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    /**
     * Create a new cat. A cat may be created not only with age
     * zero (a new born) or with a random age but also with disease.
     * 
     * @param randomAge If true, the cat will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param gender The gender of a cat. (Male, Female)
     * @param isSick If true, the cat is sick.
     */
    public Cat(boolean randomAge, Field field, Location location, String gender, boolean isSick)
    {
        super(randomAge, field, location, gender, isSick);
    }

    /**
     * Check wheather the parameter is food or not.
     * 
     * @return wheather the parameter is food or not.
     * @param object the object the cat is going to eat.
     */
    public boolean isFood(Object object) 
    {
        if (object instanceof Mouse && rand.nextDouble()<= NEW_MOUSE_HUNT_PROBABILITY) {
            return true;
        }
        
        return false;
    }
    
    /**
     * This is what the cat does most of the time - it hunts 
     * for mouse. In the process, it might breed, die of hunger, disease or old age.
     * 
     * @param newCats A list to return newly born cats.
     * @param isDay Indicate wheather it is a day or night.
     * @param weather The weather the cat is in.
     */
    public void act(List<Animal> newCats, boolean isDay, String weather)
    {
        // New cats are born into adjacent locations.
        // Get a list of adjacent free locations.
        SpreadDisease();
        timeEffect(isDay);
        weatherEffect(weather);
        incrementAge(MAX_AGE);
        incrementHunger();
        incrementThirsty();
        diseaseCauseDeath();
        if(isAlive()) {
            if(findMate()) {
                giveBirth(newCats);
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
     * Check whether or not this cat is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newCats A list to return newly born cats.
     */
    public void giveBirth(List<Animal> newCats) 
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            String gender = Randomizer.getRandomGender();
           
            boolean isSick =false;
            if (isSick()) {
                isSick = Randomizer.getRandomIsSick();
            }
            Cat young = new Cat(false, field, loc, gender, isSick);
            newCats.add(young);
        }
    }
    
    /**
     * Check wheather the adjacent cat can be a partner(mate).
     * 
     * @param object The adjacent object.
     * @return wheather or not the adjacent cat can be a partner.
     */
    public boolean isMate(Object object) 
    {
        boolean result = false;

        if (object instanceof Cat) {
            Cat cat = (Cat) object;
            
            if (cat.isAlive() && cat.canBreed(BREEDING_AGE) && !(cat.getGender().equals(this.getGender()))) {
                result = true;
            }
        }
        
        return  result;
    }

    /**
     * According to the weather, the cat get effected on the breeding probability.
     * 
     * @param weather The string of weather.
     */
    public void weatherEffect (String weather)
    {
        if (weather.equals("Sunny")) {
            NEW_BREEDING_PROBABILITY = BREEDING_PROBABILITY;
        }
        else if (weather.equals("Cloudy")) {
            NEW_BREEDING_PROBABILITY = BREEDING_PROBABILITY*0.9;
        }
        else if (weather.equals("Rainy")) {
            NEW_BREEDING_PROBABILITY = BREEDING_PROBABILITY*0.8;
        }     
        else if (weather.equals("Windy")) {
            NEW_BREEDING_PROBABILITY = BREEDING_PROBABILITY*0.85;
        }
    }
    
    /**
     * If it is during the day, the probability of hunting increased.
     * or it is decreased.
     * 
     * @param isDay Indicate wheather it is during the day or not.
     */
    public void timeEffect (boolean isDay)
    {
        if(isDay) {
            NEW_MOUSE_HUNT_PROBABILITY = MOUSE_HUNT_PROBABILITY*1.1;
        }
        else if(!isDay) {
            NEW_MOUSE_HUNT_PROBABILITY = MOUSE_HUNT_PROBABILITY*0.9;
        }
        
    }
}
