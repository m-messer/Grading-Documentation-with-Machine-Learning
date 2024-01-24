import java.util.List;
import java.util.Iterator; 
/**
 * A simple model of a worm.
 * Worms age, move, breed, die, eat on chards and are preys for owls and lizards.
 *
 * @version 2019.02.22
 */
public class Worm extends Organism
{
    // Characteristics shared by all worms (class variables).
    //food value of chard
    private static final int CHARD_FOOD_VALUE= 20; 
    // The age at which a worm can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a worm can live. 
    private static final int MAX_AGE = 20;
    // The likelihood of a worm breeding.
    private static final double BREEDING_PROBABILITY = 0.20;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    
    // Individual characteristics (instance fields).

    //the worms hunger level
    private int foodLevel;

    /**
     * Create a new worm. A worm may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the worm will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Worm(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        
        if(randomAge) {
            age = getRandomAge();
            //sets the hunger level to a random value
            foodLevel = rand.nextInt(CHARD_FOOD_VALUE); 
        }
        else{
             age = 0;
             foodLevel = CHARD_FOOD_VALUE; //least hunger value for new borns 
        }
        
    }
    
    /**
     * This is what the worm does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newWorms A list to return newly born worms.
     */
    public void act(List<Organism> newWorms)
    {
        incrementAge(); //increments age 
        incrementHunger(); //increments hunger
        
        if(isAlive()) {
            giveBirth(newWorms);            
            // Try to move into a free location.
            Location newLocation = findFood();
            // Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            
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
     * This could result in the worm's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Make this worms more hungry. This could result in the worms's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this worm is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWorms A list to return newly born worms.
     */
    private void giveBirth(List<Organism> newWorms)
    {
        // New worms are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Worm young = new Worm(false, field, loc);
            newWorms.add(young);
        }
    }
    
    /**
     * Look for chards adjacent to the current location.
     * Only the first live chard is eaten.
     * @return where food was found, or null if it wasn't.
     */
    private Location findFood(){
        Field field = getField(); 
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if(organism instanceof Chard) {
                Chard chard = (Chard) organism;
                if(chard.isAlive()) { 
                    chard.setDead();
                    foodLevel = CHARD_FOOD_VALUE;
                    return where;
                }
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
     * A worm can breed if it has reached the breeding age.
     * @return true if the worm can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * @return the maximum age of worm in which it can live
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
}
