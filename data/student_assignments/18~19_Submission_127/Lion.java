import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Lion.
 * Lions age, move, hunt gazelles, and die.
 *
 * @version 2019.02.22 
 */
public class Lion extends Animal
{
    // Characteristics shared by all Lions (class variables).
    
    // The age at which a lion can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a lion can live.
    private static final int MAX_AGE = 60;
    // The likelihood of a lion breeding.
    private static final double BREEDING_PROBABILITY = 0.09444649664537645;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The food value of a single lion. In effect, this is the
    // number of steps a lion can go before it has to eat again.
    private static final int GAZELLE_FOOD_VALUE = 26;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The lion's age.
    private int age;
    // The lion's food level, which is increased by eating gazelles.
    private int foodLevel;

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GAZELLE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = GAZELLE_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the lion does most of the time during the day: it sleeps.
     * Hence, it could die of old age or hunger.
     * @param newLions A list to return newly born Lions.
     */
    public void act1(List<Animal> newLions)
    {
        incrementAge();
        incrementHunger();
        
    }
    
    /**
     * This is what the lion does most of the time during the night: it hunts for
     * gazelles. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newLions A list to return newly born Lions.
     */
    public void act2(List<Animal> newLions)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newLions);            
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
     * Increase the age. This could result in the lion's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this lion more hungry. This could result in the lion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for gazelles adjacent to the current location.
     * Only the first live gazelle is eaten.
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
            if(animal instanceof Gazelle) {
                Gazelle gazelle = (Gazelle) animal;
                int food = gazelle.foodLevel;
                if(gazelle.isAlive()) { 
                    gazelle.setDead();
                    foodLevel += food;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLions A list to return newly born lions.
     */
    private void giveBirth(List<Animal> newLions)
    {
        // New lions are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lion young = new Lion(false, field, loc);
            newLions.add(young);
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
     * A lion can breed if it has reached the breeding age
     * and it's mate is of the opposite gender.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}