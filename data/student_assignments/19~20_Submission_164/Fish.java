import java.util.List;
import java.util.Random; 
import java.util.Iterator;
/**
 * A simple model of a fish.
 * Fish age, move, breed, eat algae, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Fish extends Animal 
{
    // Characteristics shared by all fish (class variables).

    // The age at which a fish can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a fish can live.
    private static final int MAX_AGE = 80;
    // The likelihood of a fish breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    private static final int ALGAE_FOOD_VALUE = 15;
    // Individual characteristics (instance fields).
    
    // The fish's age.
    private int age;
    
    private int foodLevel;
    
    /**
      * Create a new fish. A fish may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the fish will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param step The step that the simulator is on
     */
    public Fish(boolean randomAge, Field field, Location location, Step step)
    {
        super(field, location, step);
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
     * This is what the fish does most of the time at Daytime - it wonders
     * around. Sometimes it will breed or die of hunger or old age.
     * @param newFish A list to return newly born fish. 
     */
    public void actDay(List<Animal> newFish)
    {
        incrementAge();
        incrementHunger();  
        if(isAlive()) {
            giveBirth(newFish);            
            //Try to move into a free location.
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
                //Overcrowding.
                setDead();
            }
        }
    }
    
    /**
     * This is what the fish does most of the time at Night time - it
     * gets hungry. Sometimes it will breed.
     * @param newFish A list to return newly born fish.
     */
    public void actNight(List<Animal> newFish)
    {            
        incrementHunger();      
        if(isAlive()) {
            giveBirth(newFish);            
        }
    }
    
    /**
     * Increase the age.
     * This could result in the fish's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this fish is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFish A list to return newly born fish.
     */
    private void giveBirth(List<Animal> newFish)
    {
        // New fish are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        Step step = getStep();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fish young = new Fish(false, field, loc, step);
            newFish.add(young);
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

    /**
     * A fish can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
       
    /**
     * Increase the hunger level. This could result in the fish's death.
     * At night, it makes the fish hungry more than Day time, 
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
 }
