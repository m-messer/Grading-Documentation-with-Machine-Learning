import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a deer.
 * deers age, move, eat berries, breed, and die.
 *
 * @version 1
 */
public class Deer extends Animal
{
    // Characteristics shared by all deers (class variables).

    // The age at which a deer can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a deer can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a deer breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The number of steps a deer is poisoned before metabolising it
    private static final int POISON_DURATION = 2;
    // Food value of berries, number of steps it can go after eating before dying
    private static final int BERRIES_FOOD_VALUE = 40;
    
    
    // Individual characteristics (instance fields).
    // Food level, increases when berries are eaten. 
    private int foodLevel;
    // The deer's age.
    private int age;
    // A counter of the steps a Deer has been poisoned
    private int poisonLevel;

    /**
     * Create a new deer. A deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        poisonLevel = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(BERRIES_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel =  BERRIES_FOOD_VALUE;
        }
    }

    /**
     * This is what the Deer does most of the time - it runs 
     * around and feeds on berries. Sometimes it will breed or die of old age.
     * @param newDeer A list to return newly born Deer.
     * @param isDay A boolean of whether or not its Daytime.
     */
    public void act(List<Animal> newDeer,boolean isDay)
    {
        incrementAge();
        incrementPoison();
        incrementHunger(isDay);

        if(isAlive() && isDay) {           
            // Try to move into a free location.
            if(this.canBreed()){
                findMate(newDeer);

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
                    foodLevel = BERRIES_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * This method checks adjacent locations for a potential mate. If the deer finds a mature deer of the opposite sex and is female, It gives birth
     * @param newDeer The list of newborn Deer
     */
    private void findMate(List<Animal> newDeer)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Deer) {
                Deer deer = (Deer) animal;
                if(deer.isAlive() && deer.canBreed() && !deer.isSameGender(this.isMale()) && !this.isMale()) { 
                    giveBirth(newDeer);
                }
            }
        }

    }

    /**
     * Increase the age.
     * This could result in the deer's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Increments the poison level and shortens the deer's lifespan according to its Poison Duration
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
     * Make this deer more hungry. This could result in the deer's death.
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
     * Check whether or not this deer is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newdeers A list to return newly born deers.
     */
    private void giveBirth(List<Animal> newdeers)
    {
        // New deers are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Deer young = new Deer(false, field, loc);
            newdeers.add(young);
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
     * A deer can breed if it has reached the breeding age.
     * @return true if the deer can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
