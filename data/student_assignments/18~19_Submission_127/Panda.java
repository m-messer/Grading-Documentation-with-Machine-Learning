import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a panda.
 * Pandas age, move, breed, eat bamboos and die.
 *
 * @version 2019.02.22 
 */
public class Panda extends Animal
{
    // Characteristics shared by all pandas (class variables).

    // The age at which a panda can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a panda can live.
    private static final int MAX_AGE = 80;
    // The likelihood of a panda breeding.
    private static final double BREEDING_PROBABILITY = 0.30;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The food value of a single bamboo. In effect, this is the
    // number of steps a panda can go before it has to eat again.
    private static final int BAMBOO_FOOD_VALUE = 40;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The panda's age.
    private int age;
    // The lion's food level, which is increased by eating gazelles.
    public int foodLevel;
    
    /**
     * Create a new panda. A panda may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the panda will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Panda(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(BAMBOO_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = BAMBOO_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the panda does most of the time during the day- it runs 
     * around and eats grass. Sometimes it will breed or die of old age or hunger.
     * @param newPandas A list to return newly born pandas.
     */
    public void act1(List<Animal> newPandas)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newPandas);            
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
     * This is what the panda does during the night - it sleeps.
     * In the process, it might die of hunger, or old age.
     * @param newPandas A list to return newly born Pandas.
     */
    public void act2(List<Animal> newPandas)
    {
        incrementAge();
        incrementHunger();
    }

    /**
     * Increase the age.
     * This could result in the pandas's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this panda more hungry. This could result in the panda's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for bamboos adjacent to the current location.
     * Only the first grown bamboo is eaten.
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
            if(plant instanceof Bamboo) {
              Bamboo bamboo = (Bamboo) plant;
              int bambooAge = bamboo.bambooAge;
                if(bamboo.isAlive()) { 
                    if(bambooAge >= 3){
                        foodLevel += 2;
                        bambooAge -= 2;
                }
                else {
                    foodLevel += bambooAge;
                    bambooAge = 0;
                }
              }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this panda is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPandas A list to return newly born pandas.
     */
    private void giveBirth(List<Animal> newPandas)
    {
        // New pandas are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Panda young = new Panda(false, field, loc);
            newPandas.add(young);
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
     * A panda can breed if it has reached the breeding age and 
     * if the mate is of the opposite gender.
     * @return true if the panda can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE && findMate() && !isMale();
    }
    
    /**
     * Look for male pandas adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    private boolean findMate()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Panda) {
                Panda panda = (Panda) animal;
                if(panda.isMale()) { 
                    return true;
                }
            }
        }
        return false;
    }
}
