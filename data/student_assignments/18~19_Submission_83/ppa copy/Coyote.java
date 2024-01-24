import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a coyote.
 * Coyotes age, move, eat moose, and die.
 *
 * @version 2016.02.22 (2)
 */
public class Coyote extends Predator
{
    // Characteristics shared by all coyotes (class variables).

    // The age at which a coyote can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a coyote can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a coyote breeding.
    private static final double BREEDING_PROBABILITY = 0.8;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single moose. In effect, this is the
    // number of steps a coyote can go before it has to eat again.
    private static final int MOOSE_FOOD_VALUE = 13;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The coyote's age.
    private int age;
    // The coyote's food level, which is increased by eating mooses.
    private int foodLevel;

    /**
     * Create a coyote. A coyote can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the coyote will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Coyote(boolean randomAge, Field field, Location location, boolean female, boolean isSick)
    {
        super(field, location, female, isSick);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MOOSE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = MOOSE_FOOD_VALUE;
        }
    }



    /**
     * Increase the age. This could result in the coyote's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this coyote more hungry. This could result in the coyote's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for moose adjacent to the current location.
     * Only the first live moose is eaten.
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
            if(animal instanceof Moose) {
                Moose moose = (Moose) animal;
                if(moose.isAlive()) {
                    moose.setDead();
                    foodLevel = MOOSE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }



    /**
     * Check whether or not this coyote is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCoyotes A list to return newly born coyotees.
     */
    protected void giveBirth(List<Animal> newCoyotes) {
        // New zebras are born into adjacent locations.
        // Get a list of adjacent free locations.

        if (age > BREEDING_AGE) {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object object = field.getObjectAt(where);
                if (object instanceof Coyote) {
                    Coyote adCoyote = (Coyote) object;
                    // Check whether the animals are compatible to give birth.
                    if (adCoyote.isAlive() && (adCoyote.isFemale() && !this.isFemale()) || (!adCoyote.isFemale() && this.isFemale())) {
                        if (adCoyote.isSick()) {
                            changeSickness();
                            if (MAX_AGE - age < 10) {
                                age = MAX_AGE - age;
                            }
                        }
                        List<Location> free = field.getFreeAdjacentLocations(getLocation());
                        int births = breed();
                        for (int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Coyote young = new Coyote(false, field, loc, rand.nextBoolean(), this.isSick());
                            newCoyotes.add(young);
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
     * A coyote can breed if it has reached the breeding age and it is a female.
     * @return true if it can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE && isFemale();
    }
}
