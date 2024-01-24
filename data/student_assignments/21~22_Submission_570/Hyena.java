import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A model of a hyena.
 * Hyenas age, move, reproduce, eat zebras and die..
 *
 * @version 2022.02.26
 */
public class Hyena extends Animal
{
    // Characteristics shared by all hyenas (class variables).

    // The age at which a hyena can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a hyena can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a hyena breeding.
    private static final double BREEDING_PROBABILITY = 0.99;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a single zebra. 
    private static final int ZEBRA_FOOD_VALUE = 20;
    // The food level at which the hyena needs to have to breed. 
    private static final int BREEDING_FOOD_LEVEL = 10;
    // The maximum food level the hyena can reach.
    private static final int MAX_FOOD_LEVEL = 100;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The hyena's age.
    private int age;
    // The hyena's food level, which is increased by eating zebras.
    private int foodLevel;

    /**
     * Create a hyena. A hyena can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the hyena will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hyena(boolean randomAge, Field field, Location location)
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
     * This is what the hyena does most of the time: it hunts for zebra 
     * in the day time. In the process, it might breed, die of hunger,
     * or die of old age. Also, it is possible for a hyena to catch an infection. 
     * @param newHyenas A list to return newly born hyenas.
     */
    public void act(List<Organism> newHyenas)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (isInfected()) {
                infectOthers();
                heal();
            }
            else {
                reproduce(newHyenas);            
            }            

            if(dayTime()) {
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
    }

    /**
     * Return the age for this hyena.
     * @return The grass's hyena.
     */
    protected int getAge() 
    {
        return age;
    }

    /**
     * Increase the age. This could result in the hyena's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Return the breeding age for a hyena
     * @return The hyena's breeding age 
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Look for zebra adjacent to the current location.
     * Only the first live zebra is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if (object instanceof Zebra) {
                Zebra zebra = (Zebra) object;
                if(zebra.isAlive()) { 
                    zebra.setDead();
                    foodLevel += ZEBRA_FOOD_VALUE;
                    reachMaxFoodLevel();
                    return where;
                }
            }

            if(object instanceof Plant) {
                Plant plant = (Plant) object;
                if(plant.isAlive()) { 
                    plant.setDead();
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
     * Check whether or not this hyena is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newHyenas A list to return newly born hyenas.
     */
    protected void reproduce(List<Organism> newHyenas)
    {
        // New hyenas are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Hyena young = new Hyena(false, field, loc);
            newHyenas.add(young);
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
     * Make this hyena more hungry. This could result in the hyena's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether the hyena is healthy enough to breed. 
     * @return true if food level is above breeding food value, false if not.
     */
    private boolean healthy() {
        return foodLevel >= BREEDING_FOOD_LEVEL;
    }
}