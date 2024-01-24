import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a plankton.
 * Planktons age, move, breed, and die.
 *
 * @version 21.02.2020 (2)
 */
public class Plankton extends Animal
{
    // Characteristics shared by all planktons (class variables).

    // The age at which a plankton can start to breed.
    private static final int BREEDING_AGE = 2;
    
    // The age to which a plankton can live.
    private static final int MAX_AGE = 8;
    
    // The likelihood of a plankton breeding.
    private static final double BREEDING_PROBABILITY = 0.47;
    
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // The food value of a single seaweed.
    private static final int SEAWEED_FOOD_VALUE = 2;
    
    // Individual characteristics (instance fields).
    
    // The plankton's age.
    private int age;
    
    //distinguishes between male and female
    private boolean isMale;
    
    // The fish's food level, which is increased by eating food.
    private int foodLevel;
    
    /**
     * Create a new plankton. A plankton may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the plankton will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param rd if true, the plankton will be male
     */
    public Plankton(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SEAWEED_FOOD_VALUE);
        }
        Random rd = new Random(); // creating Random object
        isMale = rd.nextBoolean(); // distingushing between male and female
    }
    
    /**
     * This is what the plankton does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newPlanktons A list to return newly born planktons.
     */
    public void act(List<Animal> newPlanktons)
    {
        incrementAge();
        //incrementHunger();
        if(isAlive()) {
            giveBirth(newPlanktons);            
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
     * Increase the age.
     * This could result in the plankton's death.
     */
    private void incrementAge()
    {
        age++;  //increment the age 
        //if the current age was grater than max age
        if(age > MAX_AGE) {
            setDead();//the plankton will be dead
        }
    }
    
    /**
     *  Make this plankton more hungry. This could result in the plankton's death.
     */
    private void incrementHunger()
    {
        foodLevel--; //decreasing food level of plankton by one
        //if the food level of plankton is less than or equal to 0
        if(foodLevel <= 0) {
            setDead();//the plankton will be dead
        }
    }
    
    /**
     * Check whether or not this plankton is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPlanktons A list to return newly born planktons.
     */
    private void giveBirth(List<Animal> newPlanktons)
    {
        // New planktons are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plankton young = new Plankton(false, field, loc);
            newPlanktons.add(young);
        }
    }
        
    /**
     * Look for fish adjacent to the current location.
     * Only the first live fish is eaten.
     * plankton can only eat seaweed
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);            
            SeaWeed seaWeed = (SeaWeed) plant;
            //if the adjacent seaweed was alive let the fish to eat it
            if(seaWeed.isAlive()) { 
                seaWeed.setDead();
                foodLevel = SEAWEED_FOOD_VALUE;
                return where;
            }
        }
        return null;
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
     * A plankton can breed if it has reached the breeding age.
     * @return true if the plankton can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
