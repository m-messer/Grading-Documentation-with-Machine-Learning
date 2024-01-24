import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat Hares and Deer, and die.
 *
 * @version 1
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).

    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 180;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of cubs a wolf can give birth to
    private static final int MAX_LITTER_SIZE = 2;

    // The food value of a single Hare and Deer. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int Hare_FOOD_VALUE = 40;
    private static final int Deer_FOOD_VALUE = 50;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The number of steps a wolf is poisoned before metabolising it
    private static final int POISON_DURATION = 1;

    // Individual characteristics (instance fields).
    // The wolf's age.
    private int age;
    // The wolf's food level, which is increased by eating Hares.
    private int foodLevel;
    // A counter of the steps the wolf has been poisoned
    private int poisonLevel;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero)
     * or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        poisonLevel = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(Deer_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = Deer_FOOD_VALUE/ 3;
        }
    }

    /**
     * This is what the wolf does most of the time: it hunts for
     * Hares or Deer. In the process, it might breed, die of hunger, suffer poison
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWolves A list to return newly born wolves.
     * @param isDay A boolean for daytime
     */
    public void act(List<Animal> newWolves, boolean isDay)
    {
        incrementAge();
        incrementHunger(isDay);
        incrementPoison();

        if(isAlive() && isDay) {           
            if(this.canBreed()){
                findMate(newWolves);
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
     * This method checks adjacent locations for a potential mate. If the wolf finds a mature wolf of the opposite sex and is female, It gives birth
     * @param newWolves The list of newborn wolves
     */
    private void findMate(List<Animal> newWolfs)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Wolf) {
                Wolf Wolf = (Wolf) animal;
                if(Wolf.isAlive() && Wolf.canBreed() && !Wolf.isSameGender(this.isMale()) && !this.isMale()) { 
                    giveBirth(newWolfs);
                }
            }
        }

    }

    /**
     * Increase the age. This could result in the wolf's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this wolf more hungry. This could result in the wolf's death.
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
     * Increments the poison level and shortens the wolf's lifespan according to its Poison Duration
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
     * Look for Hares/Deer adjacent to the current location.
     * Only the first live Deer is eaten, otherwise a live rabbit is eaten or nothing at all.
     * Deer/Hares may poison the wolf
     * 
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
            if(animal instanceof Deer) {
                Deer deer= (Deer) animal;
                if(deer.isAlive()) { 
                    deer.setDead();
                    if(deer.isPoisoned()){
                        this.setPoisoned(true);
                    }
                    foodLevel = Deer_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Hare) {
                Hare hare = (Hare) animal;
                if(hare.isAlive()) { 
                    hare.setDead();
                    if(hare.isPoisoned()){
                        this.setPoisoned(true);
                    }
                    foodLevel = Hare_FOOD_VALUE;
                    return where;
                }
            }

        }
        return null;
    }

    /**
     * Check whether or not this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWolves A list to return newly born wolves.
     */
    private void giveBirth(List<Animal> newWolves)
    {
        // New wolves are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Wolf young = new Wolf(false, field, loc);
            newWolves.add(young);
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
     * A wolf can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
