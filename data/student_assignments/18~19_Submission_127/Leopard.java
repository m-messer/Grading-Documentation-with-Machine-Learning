import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * A simple model of a Leopard.
 * Leopards age, move, hunt pandas, and die.
 *
 * @version 2019.02.22 
 */
public class Leopard extends Animal
{
    // Characteristics shared by all Leopards (class variables).
    
    // The age at which a leopard can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a leopard can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a leopard breeding.
    private static final double BREEDING_PROBABILITY = 0.06141024095416006;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The food value of a single panda. In effect, this is the
    // number of steps a leopard can go before it has to eat again.
    private static final int PANDA_FOOD_VALUE = 30;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The leopard's age.
    private int age;
    // The leopard's food level, which is increased by eating pandas.
    private int foodLevel;

    /**
     * Create a leopard. A leopard can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the leopard will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Leopard(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PANDA_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = PANDA_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the leopard does most of the time during the day: it hunts for
     * pandas. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newLeopards A list to return newly born Leopards.
     */
    public void act1(List<Animal> newLeopards)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newLeopards);            
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
     * This is what the leopard does most of the time during the night: it sleeps 
     * In the process, it might die of hunger or old age
     * @param field The field currently occupied.
     * @param newLeopards A list to return newly born Leopards.
     */
    public void act2(List<Animal> newLeopards)
    {
        incrementAge();
        incrementHunger();
    }

    /**
     * Increase the age. This could result in the leopard's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this leopard more hungry. This could result in the leopard's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for pandas adjacent to the current location.
     * Only the first live panda is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Panda) {
                Panda panda = (Panda) animal;
                int food = panda.foodLevel;
                if(panda.isAlive()) { 
                    panda.setDead();
                    foodLevel += food;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this leopard is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLeopards A list to return newly born leopards.
     */
    private void giveBirth(List<Animal> newLeopards)
    {
        // New leopards are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Leopard young = new Leopard(false, field, loc);
            newLeopards.add(young);
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
     * A leopard can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
