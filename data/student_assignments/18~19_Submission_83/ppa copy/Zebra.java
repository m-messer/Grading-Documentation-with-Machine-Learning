import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a zebra.
 * Wolves age, move, eat plants, and die.
 *
 * @version 2019.02.22 (2)
 */
public class Zebra extends Prey {

    // Characteristics shared by all zebras (class variables).

    // The age at which a zebra can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a zebra can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The zebra's age.
    private int age;

    private int foodLevel;


    public Zebra(boolean randomAge, Field field, Location location, boolean female, boolean isSick){
        super(field, location, female, isSick);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = PLANT_FOOD_VALUE;
        }
    }

    /**
     * Increase the age.
     * This could result in the zebra's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this zebra more hungry. This could result in the zebra's death.
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
            Object object = field.getObjectAt(where);
            if(object instanceof Plant) {
                Plant plant = (Plant) object;
                if(plant.isAlive()) {
                    plant.setDead();
                    foodLevel = PLANT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }





    /**
     * Check whether or not this zebra is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newZebras A list to return newly born zebras.
     */
    protected void giveBirth(List<Animal> newZebras) {
        // New zebras are born into adjacent locations.
        // Get a list of adjacent free locations.

        if (age > BREEDING_AGE) {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object object = field.getObjectAt(where);
                if (object instanceof Zebra) {
                    Zebra adZebra = (Zebra) object;
                    // Check whether the animals are compatible to give birth.
                    if (adZebra.isAlive() && (adZebra.isFemale() && !this.isFemale()) || (!adZebra.isFemale() && this.isFemale())) {
                        if (adZebra.isSick()) {
                            changeSickness();
                            if (MAX_AGE - age < 10) {
                                age = MAX_AGE - age;
                            }
                        }
                        List<Location> free = field.getFreeAdjacentLocations(getLocation());
                        int births = breed();
                        for (int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Zebra young = new Zebra(false, field, loc, rand.nextBoolean(), this.isSick());
                            newZebras.add(young);
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
     * A zebra can breed if it has reached the breeding age and it is female.
     * @return true if the zebra can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE && isFemale();
    }
}
