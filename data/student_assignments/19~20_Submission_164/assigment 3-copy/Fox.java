import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, eat fish, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Fox extends Animal  
{
    // Characteristics shared by all foxes (class variables).
    
    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 9;
    // The age to which a fox can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single fish. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int FISH_FOOD_VALUE = 4;
    private static final int ALGAE_FOOD_VALUE = 4;
    
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The fox's age.
    private int age;
    // The fox's food level, which is increased by eating fish.
    private int foodLevel;
    
    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fox(boolean randomAge, Field field, Location location, Step step)
    {
        super(field, location, step);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FISH_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FISH_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the fox does most of the time at Day time: it hunts for
     * fish. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void actDay(List<Animal> newFoxes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newFoxes);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * This is what the fox does most of the time at Night time.
     * In the process, it might breed or die of hunger.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void actNight(List<Animal> newFoxes)
    {
        incrementHunger();
        if(isAlive()) {
            giveBirth(newFoxes);            
        }
    }
    
    /**
     * Increase the age. This could result in the fox's death.
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
            if(animal instanceof Fish) {
                Fish fish = (Fish) animal;
                if(fish.isAlive()) { 
                    fish.setDead();
                    foodLevel = FISH_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Algae) {
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
    
    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    private void giveBirth(List<Animal> newFoxes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        Step step = getStep();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fox young = new Fox(false, field, loc, step);
            newFoxes.add(young);
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
     * A fox can breed if it has reached the breeding age.
       */
      private boolean canBreed()
    {
          return age >= BREEDING_AGE;
    }
    
 }
