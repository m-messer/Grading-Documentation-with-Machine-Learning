import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a coyote.
 * coyotees age, move, eat Hares and Berries, and die.
 *
 * @version 1
 */
public class Coyote extends Animal
{
    // Characteristics shared by all coyotees (class variables).

    // The age at which a coyote can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a coyote can live.
    private static final int MAX_AGE = 180;
    // The likelihood of a coyote breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single Hare/Berries. In effect, this is the
    // number of steps a coyote can go before it has to eat again.
    private static final int HARE_FOOD_VALUE = 40;
    private static final int BERRIES_FOOD_VALUE = 15;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // The number of steps a Coyote is poisoned before metabolising it
    private static final int POISON_DURATION = 1;

    // Individual characteristics (instance fields).
    // The coyote's age.
    private int age;
    // The coyote's food level, which is increased by eating Hares.
    private int foodLevel;
    // A counter of the steps the coyote has been poisoned
    private int poisonLevel;

    /**
     * Create a coyote. A coyote can be created as a new born (age zero)
     * or with a random age and food level.
     * 
     * @param randomAge If true, the coyote will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Coyote(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        poisonLevel = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(HARE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = HARE_FOOD_VALUE/2;
        }
    }

    /**
     * This is what the coyote does most of the time: it hunts for
     * Hares/Berries. In the process, it might breed, die of hunger,suffer poison
     * or die of old age.
     * 
     * @param newCoyotes A list to return newly born coyotes.
     * @param isDay A boolean for daytime
     */
    public void act(List<Animal> newCoyotes, boolean isDay)
    {
        incrementAge();
        incrementPoison();
        incrementHunger(isDay);

        if(isAlive() && isDay) {           

            if(this.canBreed()){
                findMate(newCoyotes);
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
     * This method checks adjacent locations for a potential mate. If the coyote finds a mature coyote of the opposite sex and is female, It gives birth
     * @param newCoyotes The list of newborn coyotes
     */
    private void findMate(List<Animal> newCoyotes)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Coyote) {
                Coyote Coyote = (Coyote) animal;
                if(Coyote.isAlive() && Coyote.canBreed() && !Coyote.isSameGender(this.isMale()) && !this.isMale()) { 
                    giveBirth(newCoyotes);
                }
            }
        }

    }

    /**
     * Increase the age. This could result in the coyote's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this coyote more hungry. This could result in the coyote's death.
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
     * Increments the poison level and shortens the coyote's lifespan according to its Poison Duration
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
     * Look for Hares/berries adjacent to the current location.
     * The first live Hare is eaten if not, a shrub of berries is eaten or nothing at all.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object food = field.getObjectAt(where);
            if(food instanceof Hare) {
                Hare hare = (Hare) food;
                if(hare.isAlive()) { 
                    hare.setDead();
                    if(hare.isPoisoned()){
                        this.setPoisoned(true);
                    }
                    foodLevel = HARE_FOOD_VALUE;
                    return where;
                }
            }
            else if(food instanceof Berries) {
                Berries berries = (Berries) food;
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
     * Check whether or not this coyote is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newcoyotes A list to return newly born coyotes.
     */
    private void giveBirth(List<Animal> newCoyotes)
    {
        // New coyotees are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Coyote young = new Coyote(false, field, loc);
            newCoyotes.add(young);
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
     * A coyote can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
