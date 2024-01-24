import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a owl.
 * Owles age, move, eat worms, and die.
 *
 * @version 2019.02.22
 */
public class Owl extends Organism
{
    // Characteristics shared by all owles (class variables).
    
    // The age at which a owl can start to breed.
    private static final int BREEDING_AGE = 8;
    // The age to which a owl can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a owl breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single worm. In effect, this is the
    // number of steps a owl can go before it has to eat again.
    private static final int WORM_FOOD_VALUE = 12;
    
    // Individual characteristics (instance fields).

    // The owl's food level, which is increased by eating worms.
    private int foodLevel;

    /**
     * Create an owl. An owl can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the owl will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Owl(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = getRandomAge();
            foodLevel = rand.nextInt(WORM_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = WORM_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the owl does most of the time: it hunts for
     * worms. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newOwles A list to return newly born owles.
     */
    public void act(List<Organism> newOwles)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newOwles);            
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
     * Increase the age. This could result in the owl's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Make this owl more hungry. This could result in the owl's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for worms adjacent to the current location.
     * Only the first live worm is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if(organism instanceof Worm) {
                Worm worm = (Worm) organism;
                if(worm.isAlive()) { 
                    worm.setDead();
                    foodLevel = WORM_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this owl is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newOwles A list to return newly born owles.
     */
    private void giveBirth(List<Organism> newOwles)
    {
        // New owles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Owl young = new Owl(false, field, loc);
            newOwles.add(young);
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
     * An owl can breed if it has reached the breeding age.
     * @return true if the owl can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * @return the maximum age of owl in which it can live
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
}
