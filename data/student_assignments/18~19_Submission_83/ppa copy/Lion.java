import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a lion.
 * Lions age, move, eat zebras, and die.
 *
 * @version 2019.02.22 (2)
 */
public class Lion extends Predator {
    // Characteristics shared by all lions (class variables).

    // The age at which a lion can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a lion can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a lion breeding.
    private static final double BREEDING_PROBABILITY = 0.85;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single zebra. In effect, this is the
    // number of steps a lion can go before it has to eat again.
    private static final int ZEBRA_FOOD_VALUE =13;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();



    // Individual characteristics (instance fields).

    // The lion's age.
    private int age;
    // The lion's food level, which is increased by eating zebras.
    private int foodLevel;

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location, boolean female, boolean isSick)
    {
        super(field, location, female, isSick);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(ZEBRA_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel =ZEBRA_FOOD_VALUE;
        }
    }



    /**
     * Increase the age. This could result in the lion's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this lion more hungry. This could result in the lion's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for zebras adjacent to the current location.
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
            Object animal = field.getObjectAt(where);
            if(animal instanceof Zebra) {
                Zebra zebra = (Zebra) animal;
                if(zebra.isAlive()) {
                    zebra.setDead();
                    foodLevel = ZEBRA_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLions A list to return newly born liones.
     */
    protected void giveBirth(List<Animal> newLions) {
        // New zebras are born into adjacent locations.
        // Get a list of adjacent free locations.

        if (age > BREEDING_AGE) {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object object = field.getObjectAt(where);
                if (object instanceof Lion) {
                    Lion adLion = (Lion) object;
                    // Check whether the animals are compatible to give birth.
                    if (adLion.isAlive() && (adLion.isFemale() && !this.isFemale()) || (!adLion.isFemale() && this.isFemale())) {
                        if (adLion.isSick()) {
                            changeSickness();
                            if (MAX_AGE - age < 10) {
                                age = MAX_AGE - age;
                            }
                        }
                        List<Location> free = field.getFreeAdjacentLocations(getLocation());
                        int births = breed();
                        for (int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Lion young = new Lion(false, field, loc, rand.nextBoolean(), this.isSick());
                            newLions.add(young);
                        }

                    }
                }
            }
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
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A lion can breed if it has reached the breeding age and it is a female.
     * @return true if it can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE && isFemale();
    }


}
