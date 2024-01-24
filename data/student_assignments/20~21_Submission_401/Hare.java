import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 *  A simple model of a hare.
 *  Hares age, move, eat berries, breed, and die.
 *
 * @version 1
 */
public class Hare extends Animal
{
    // Characteristics shared by all Hares (class variables).

    // The age at which a Hare can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Hare can live.
    private static final int MAX_AGE = 80;
    // The likelihood of a Hare breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value when a shrub of berries is eaten
    private static final int Berries_FOOD_VALUE = 35;
    // The number of turns this species can metabolise poison.
    private static final int POISON_DURATION = 3;

    // Individual characteristics (instance fields).
    private int foodLevel;
    private int poisonLevel;

    // The Hare's age.
    private int age;

    /**
     * Create a new Hare. A Hare may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Hare will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hare(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        poisonLevel = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(Berries_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel =  Berries_FOOD_VALUE;
        }
    }

    /**
     * This is what the Hare does most of the time - it runs 
     * around and eats berries. Sometimes it will breed or die of old age.
     * @param newHares A list to return newly born Hares.
     * @param isDay A boolean to check if its daytime.
     */
    public void act(List<Animal> newHares,boolean isDay)
    {
        incrementAge();
        incrementPoison();
        incrementHunger(isDay);

        if(isAlive() && isDay) {           
            // Try to move into a free location.
            if(this.canBreed()){
                findMate(newHares);

            }

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
     * Look for Berries adjacent to the current location.
     * Only the first live Berries is eaten.
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
            if(plant instanceof Berries) {
                Berries berries = (Berries) plant;
                if(berries.isAlive()) { 
                    berries.setDead();
                    if(berries.isPoisonous()){
                        this.setPoisoned(true);
                    }
                    foodLevel = Berries_FOOD_VALUE;
                    return where;

                }
            }
        }
        return null;
    }

    /**
     * This method checks adjacent locations for a potential mate. If the hare finds a mature hare of the opposite sex and is female, It gives birth
     * @param newHare The list of newborn hares
     */
    private void findMate(List<Animal> newHares)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Hare) {
                Hare Hare = (Hare) animal;
                if(Hare.isAlive() && Hare.canBreed() && !Hare.isSameGender(this.isMale()) && !this.isMale()) { 
                    giveBirth(newHares);
                }
            }
        }
    }

    /**
     * Increments the poison level and shortens the hare's lifespan according to its Poison Duration
     */
    private void incrementPoison()
    {

        if(isPoisoned() && isAlive()){
            incrementAge();
            poisonLevel++;
            if(poisonLevel % POISON_DURATION == 0){
                this.setPoisoned(false);
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the Hare's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this Hare is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newHares A list to return newly born Hares.
     */
    private void giveBirth(List<Animal> newHares)
    {
        // New Hares are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Hare young = new Hare(false, field, loc);
            newHares.add(young);
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
        if(rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Make this hare more hungry. This could result in the hare's death.
     */
    private void incrementHunger(boolean isActive)
    {
        if (isActive) {
            foodLevel--;
            if(foodLevel <= 0) {
                setDead();
            }
        }
    }

    /**
     * A Hare can breed if it has reached the breeding age.
     * @return true if the Hare can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
