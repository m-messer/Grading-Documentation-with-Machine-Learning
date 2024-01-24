import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a lizard.
 * Lizardes age, move, eat worms, and die.
 *
 * @version 2019.02.22
 */
public class Lizard extends Organism
{
    // Characteristics shared by all lizardes (class variables).
    
    // The age at which a lizard can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a lizard can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a lizard breeding.
    private static final double BREEDING_PROBABILITY = 0.19;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single worm. In effect, this is the
    // number of steps a lizard can go before it has to eat again.
    private static final int WORM_FOOD_VALUE = 12;

    // Individual characteristics (instance fields).

    // The lizard's food level, which is increased by eating worms.
    private int foodLevel;

    /**
     * Create a lizard. A lizard can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lizard will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lizard(boolean randomAge, Field field, Location location)
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
     * This is what the lizard does most of the time: it hunts for
     * worms. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newLizardes A list to return newly born lizardes.
     */
    public void act(List<Organism> newLizardes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newLizardes);            
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
     * Increase the age. This could result in the lizard's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Make this lizard more hungry. This could result in the lizard's death.
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
     * Check whether or not this lizard is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLizardes A list to return newly born lizardes.
     */
    private void giveBirth(List<Organism> newLizardes)
    {
        // New lizardes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lizard young = new Lizard(false, field, loc);
            newLizardes.add(young);
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
     * A lizard can breed if it has reached the breeding age.
     * @return true if the lizard can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * @return the maximum age of lizard in which it can live
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
}
