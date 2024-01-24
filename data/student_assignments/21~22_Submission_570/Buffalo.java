import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A model of a buffalo.
 * Buffalos age, move, reproduce, eat grass and die.
 *
 * @version 2022.02.21
 */
public class Buffalo extends Animal
{
    // Characteristics shared by all buffalos (class variables).

    // The age at which a buffalo can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a buffalo can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a buffalo breeding.
    private static final double BREEDING_PROBABILITY = 0.70;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single grass.
    private static final int GRASS_FOOD_VALUE = 4;
    // The food level at which the buffalo needs to have to breed. 
    private static final int BREEDING_FOOD_LEVEL = 5;
    // The maximum food level the buffalo can reach.
    private static final int MAX_FOOD_LEVEL = 7;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The buffalo's age.
    private int age;
    // The buffalo's food level, which is increased by eating grass.
    private int foodLevel;

    /**
     * Create a new buffalo. A buffalo may be created with age
     * zero (a new born) or with a random age.
     * @param randomAge If true, the buffalo will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Buffalo(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        foodLevel = rand.nextInt(MAX_FOOD_LEVEL); 
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
    }

    /**
     * This is what the buffalo does most of the time - it runs and eat grass 
     * around in day time. Sometimes it will breed or die of old age or catch an infection. 
     * @param newBuffalos A list to return newly born buffalos.
     */
    public void act(List<Organism> newBuffalos) 
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (isInfected()) {
                infectOthers();
                heal();
            }
            else {
                reproduce(newBuffalos);            
            }

            if(dayTime()){
                // Try to move into a free location.
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
    }

    /**
     * Return the age for this buffalo.
     * @return The buffalo's age.
     */
    protected int getAge() 
    {
        return age;
    }

    /**
     * Increase the age.
     * This could result in the buffalo's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Return the breeding age for a buffalo.
     * @return The buffalo's breeding age 
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Look for grass adjacent to the current location.
     * Only the first live grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()  
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    grass.setDead();
                    foodLevel += GRASS_FOOD_VALUE;
                    reachMaxFoodLevel();
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether food level is beyond maximum food level. If yes, cap it. 
     */
    private void reachMaxFoodLevel()
    {
        if(foodLevel > MAX_FOOD_LEVEL) {
            foodLevel = MAX_FOOD_LEVEL; 
        }
    }

    /**
     * Make this buffalo more hungry. This could result in the buffalo's death.
     */
    private void incrementHunger() 
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this buffalo is to reproduce at this step.
     * New births will be made into free adjacent locations.
     * @param newBuffalos A list to return newly born buffalos.
     */
    protected void reproduce(List<Organism> newBuffalos)
    {
        // New buffalos are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Buffalo young = new Buffalo (false, field, loc);
            newBuffalos.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && healthy() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Check whether the buffalo is healthy enough to breed. 
     * @return true if food level is above breeding food value, false if not.
     */
    private boolean healthy() {
        return foodLevel >= BREEDING_FOOD_LEVEL;
    }
}