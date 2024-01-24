import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A model of a lion.
 * Lions age, move, reproduce, die and eat hyenas, zebras & buffalos. 
 *
 * @version 2022.02.21
 */
public class Lion extends Animal
{
    // Characteristics shared by all lion (class variables).

    // The age at which a lion can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a lion can live.
    private static final int MAX_AGE = 250;
    // The likelihood of a lion breeding.
    private static final double BREEDING_PROBABILITY = 0.999;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single buffalo. 
    private static final int BUFFALO_FOOD_VALUE = 20;
    // The food value of a single zebra. 
    private static final int ZEBRA_FOOD_VALUE = 20;
    // The food value of a single hyena. 
    private static final int HYENA_FOOD_VALUE = 50;
    // The food level at which the lion needs to have to breed. 
    private static final int BREEDING_FOOD_LEVEL = 12;
    // The maximum food level the lion can reach.
    private static final int MAX_FOOD_LEVEL = 150;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();  

    // Individual characteristics (instance fields).
    // The lion's age.
    private int age;
    // The lion's food level, which is increased by eating buffalos, zebras and hyena.
    private int foodLevel;

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
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
     * This is what the lion does most of the time: it hunts for
     * buffalo, zebra or hyena in the night time. In the process, it might breed, die of hunger,
     * or die of old age. Also, it is possible for a lion to catch an infection. 
     * @param newLions A list to return newly born lions.
     */
    public void act(List<Organism> newLions)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (isInfected()) {
                infectOthers();
                heal();
            }
            else {
                reproduce(newLions);            
            }         

            if (nightTime()) {
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
     * Return the age for this lion.
     * @return The lion's age.
     */
    protected int getAge() 
    {
        return age;
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
     * Return the breeding age for a lion.
     * @return The lion's breeding age 
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
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
     * Look for buffalo/zebra/hyena adjacent to the current location.
     * Only the first live prey is eaten.
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
            if(object instanceof Zebra){
                Zebra zebra = (Zebra) object;
                if(zebra.isAlive()) { 
                    zebra.setDead();
                    foodLevel += ZEBRA_FOOD_VALUE;
                    reachMaxFoodLevel();
                    return where;
                }
            }
            
            if(object instanceof Buffalo) {
                Buffalo buffalo = (Buffalo) object;
                if(buffalo.isAlive()) { 
                    buffalo.setDead();
                    foodLevel += BUFFALO_FOOD_VALUE;
                    reachMaxFoodLevel();
                    return where;
                }
            }

            if (object instanceof Hyena) {
                Hyena hyena = (Hyena) object;
                if(hyena.isAlive()) { 
                    hyena.setDead();
                    foodLevel += HYENA_FOOD_VALUE;
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
     * Check whether or not this lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLions A list to return newly born lions.
     */
    protected void reproduce(List<Organism> newLions)
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
    protected int breed()
    {
        int births = 0;
        if(canBreed() && healthy() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Check whether the lion is healthy enough to breed. 
     * @return true if food level is above breeding food value, false if not.
     */
    private boolean healthy() {
        return foodLevel >= BREEDING_FOOD_LEVEL;
    }
}
