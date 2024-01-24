import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a snake.
 * Snakees age, move, eat chickens, and die.
 *
 * @version 2019.02.22
 */
public class Snake extends Organism
{
    // Characteristics shared by all snakees (class variables).
    
    // The age at which a snake can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a snake can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a snake breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The food value of a single chicken. In effect, this is the
    // number of steps a snake can go before it has to eat again.
    private static final int CHICKEN_FOOD_VALUE = 20;
    
    // Individual characteristics (instance fields).

    // The snake's food level, which is increased by eating chickens.
    private int foodLevel;
    //states whether the snake is sick
    private boolean isSick ; 
    
    /**
     * Create a snake. A snake can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = getRandomAge();
            foodLevel = rand.nextInt(CHICKEN_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = CHICKEN_FOOD_VALUE;
        }
        
    }
    
    /**
     * This is what the snake does most of the time: it hunts for
     * chickens. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSnakes A list to return newly born snakees.
     */
    public void act(List<Organism> newSnakes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSnakes);  
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
            
            if(isSick){ setDead(); }
        }
        
    }

    /**
     * Increase the age. This could result in the snake's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Make this snake more hungry. This could result in the snake's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for chickens adjacent to the current location.
     * Only the first live chicken is eaten.
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
            if(organism instanceof Chicken) {
                Chicken chicken = (Chicken) organism;
                if(chicken.isAlive()) { 
                    if(chicken.getIsSick()){isSick = true;}
                    chicken.setDead();
                    foodLevel = CHICKEN_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this snake is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSnakees A list to return newly born snakees.
     */
    private void giveBirth(List<Organism> newSnakes)
    {
        // New snakees are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Snake young = new Snake(false, field, loc);
            newSnakes.add(young);
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
     * A snake can breed if it has reached the breeding age.
     * @return true if the snake can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
   
    /**
     * @return the maximum age of snake in which it can live
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
}
