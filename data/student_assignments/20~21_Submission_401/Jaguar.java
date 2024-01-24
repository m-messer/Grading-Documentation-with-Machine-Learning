import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a jaguar.
 * Jaguars age, move, eat Hares, and die.
 *
 * @version 1
 */
public class Jaguar extends Animal
{
    // Characteristics shared by all Jaguars (class variables).

    // The age at which a jaguar can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a jaguar can live.
    private static final int MAX_AGE = 180;
    // The likelihood of a jaguar breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single deer. In effect, this is the
    // number of steps a jaguar can go before it has to eat again.
    private static final int DEER_FOOD_VALUE = 60;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The number of steps a jaguar is poisoned before metabolising it
    private static final int POISON_DURATION = 1;
    
    // Individual characteristics (instance fields).
    // The jaguar's age.
    private int age;
    // The jaguar's food level, which is increased by eating Hares.
    private int foodLevel;
     // A counter of the steps the jaguar has been poisoned
    int poisonLevel;

    /**
     * Create a jaguar. A jaguar can be created as a new born (age zero) 
     * or with a random age and food level.
     * 
     * @param randomAge If true, the jaguar will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Jaguar(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        poisonLevel = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEER_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = DEER_FOOD_VALUE/4;
        }
    }

    /**
     * This is what the jaguar does most of the time: it hunts for
     * Deer. In the process, it might breed, die of hunger, get poisoned
     * or die of old age.
     * 
     * @param field The field currently occupied.
     * @param newJaguars A list to return newly born Jaguars.
     * @param isDay A boolean for daytime
     */
    public void act(List<Animal> newJaguars, boolean isDay)
    {
        incrementAge();
        incrementPoison();
        incrementHunger(!isDay);

        if(isAlive() && !isDay) {          

            if(this.canBreed()){
                findMate(newJaguars);

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
     * This method checks adjacent locations for a potential mate. If the jaguar finds a mature jaguar of the opposite sex and is female, It gives birth
     * @param newJaguars The list of newborn Jaguars
     */
    private void findMate(List<Animal> newJaguars)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Jaguar) {
                Jaguar Jaguar = (Jaguar) animal;
                if(Jaguar.isAlive() && Jaguar.canBreed() && !Jaguar.isSameGender(this.isMale()) && !this.isMale()) { 
                    giveBirth(newJaguars);
                }
            }
        }

    }

    /**
     * Increase the age. This could result in the jaguar's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this jaguar more hungry. This could result in the jaguar's death.
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
     * Look for Deer adjacent to the current location.
     * Only the first live Deer is eaten.
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
                    foodLevel = DEER_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Increments the poison level and shortens the jaguar's lifespan according to its Poison Duration
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
     * Check whether or not this jaguar is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newJaguars A list to return newly born Jaguars.
     */
    private void giveBirth(List<Animal> newJaguars)
    {
        // New Jaguars are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Jaguar young = new Jaguar(false, field, loc);
            newJaguars.add(young);
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
     * A jaguar can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
