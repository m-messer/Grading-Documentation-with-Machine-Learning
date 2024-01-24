import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a moose.
 * Moose age, move, eat plants, breed, and die.
 *
 * @version 2019.02.22 (2)
 */
public class Moose extends Prey
{
    // Characteristics shared by all moose (class variables).

    // The age at which a moose can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a moose can live.
    private static final int MAX_AGE = 25;
    // The likelihood of a moose breeding.
    private static final double BREEDING_PROBABILITY = 0.20;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).

    // The moose's age.
    private int age;
    // The moose's food level, which is increased by eating plants.
    private int foodLevel;

    /**
     * Create a new moose. A moose may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the moose will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param female If true, the animal is female.
     */
    public Moose(boolean randomAge, Field field, Location location, boolean female, boolean isSick)
    {
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
     * This could result in the moose's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this moose more hungry. This could result in the moose's death.
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
     * Check whether or not this moose is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMoose A list to return newly born moose.
     */
    protected void giveBirth(List<Animal> newMoose) {
        if (age > BREEDING_AGE) {
            //Look for adjacent animals of the same breed
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object object = field.getObjectAt(where);
                if (object instanceof Moose) {
                    Moose adMoose = (Moose) object;
                    // Check whether the animals are compatible to give birth.
                    if (adMoose.isAlive() && (adMoose.isFemale() && !this.isFemale()) || (!adMoose.isFemale() && this.isFemale())) {
                        if (adMoose.isSick()) {
                            changeSickness();
                            if (MAX_AGE - age < 10) {
                                age = MAX_AGE - age;
                            }
                        }
                        //Give birth in a random adjacent location.
                        List<Location> free = field.getFreeAdjacentLocations(getLocation());
                        int births = breed();
                        for (int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Moose young = new Moose(false, field, loc, rand.nextBoolean(), this.isSick());
                            newMoose.add(young);
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
     * A moose can breed if it has reached the breeding age and it is female.
     * @return true if the moose can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE && isFemale();
    }
}
