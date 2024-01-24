import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat moose, and die.
 *
 * @version 2019.02.22 (2)
 */
public class Wolf extends Predator
{
    // Characteristics shared by all wolves (class variables).
    
    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 1.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single moose. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int MOOSE_FOOD_VALUE =12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The wolf's age.
    private int age;
    // The wolf's food level, which is increased by eating mooses.
    private int foodLevel;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location, boolean female, boolean isSick)
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
     * Increase the age. This could result in the wolf's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this wolf more hungry. This could result in the wolf's death.
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
     * Check whether or not this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWolves A list to return newly born wolves.
     */
    protected void giveBirth(List<Animal> newWolves) {
        // New zebras are born into adjacent locations.
        // Get a list of adjacent free locations.

        if (age > BREEDING_AGE) {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object object = field.getObjectAt(where);
                if (object instanceof Wolf) {
                    Wolf adWolf = (Wolf) object;
                    // Check whether the animals are compatible to give birth.
                    if (adWolf.isAlive() && (adWolf.isFemale() && !this.isFemale()) || (!adWolf.isFemale() && this.isFemale())) {
                        if (adWolf.isSick()) {
                            changeSickness();
                            if (MAX_AGE - age < 10) {
                                age = MAX_AGE - age;
                            }
                        }
                        List<Location> free = field.getFreeAdjacentLocations(getLocation());
                        int births = breed();
                        for (int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Wolf young = new Wolf(false, field, loc, rand.nextBoolean(), this.isSick());
                            newWolves.add(young);
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
     * A wolf can breed if it has reached the breeding age and it is a female.
     * @return true if it can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE && isFemale();
    }
}
