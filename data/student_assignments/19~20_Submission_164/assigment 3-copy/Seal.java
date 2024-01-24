import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a Seal.
 * Seals age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Seal extends Animal 
{
    // Characteristics shared by all seals (class variables).

    // The age at which a seal can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a seal can live.
    private static final int MAX_AGE = 25;
    // The likelihood of a seal breeding.
    private static final double BREEDING_PROBABILITY = 0.02;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    private static final int ALGAE_FOOD_VALUE = 9;
    // Individual characteristics (instance fields).

    // The seals's age.
    private int age;
    
    private int foodLevel;
    /**
     * Create a new seal. A seal may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the seal will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Seal(boolean randomAge, Field field, Location location, Step step)
    {
        super(field, location, step);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(ALGAE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = ALGAE_FOOD_VALUE;
        }
    }

    /**
     * This is what the seal does most of the time - it moves 
     * around. Sometimes it will breed or die of old age.
     * @param newSeals A list to return newly born seals.
     */
    public void actDay(List<Animal> newSeals)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSeals);            
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * This is what the seal does most of the time - it moves 
     * around. Sometimes it will breed or die of old age.
     * @param newSeals A list to return newly born seals.
     */
    public void actNight(List<Animal> newSeals)
    {
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSeals);            
        }
    }

    /**
     * Increase the age.
     * This could result in the seal's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Increase the hunger level. This could result in the polar bear's death.
     * At night,makes polar bear hungry more than Day time, 
     * as it cannot hunt for a prey at Night.
     */
    private void incrementHunger()
    {
        if(isDay()){
            foodLevel --;
        }
        else{
            foodLevel -= 2;
        }
        
        if(foodLevel <= 0) {
            setDead();
        }
    }

    
    /**
     * Check whether or not this seal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeals A list to return newly born seals.
     */
    private void giveBirth(List<Animal> newSeals)
    {
        // New seals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        Step step = getStep();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Seal young = new Seal(false, field, loc, step);
            newSeals.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A seal can breed if it has reached the breeding age.
     * @return true if the seal can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Look for fish adjacent to the current location.
     * Only the first live fish is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        Step step = getStep();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Algae) {
                Algae algae = (Algae) animal;
                if(algae.isAlive()) { 
                    algae.decreaseHealth();
                    foodLevel = ALGAE_FOOD_VALUE;
                    if (!algae.isAlive()) {
                       return where;
                    }
                    
                }
            }
        }
        return null;
    }
}
