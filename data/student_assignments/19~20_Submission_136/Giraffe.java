import java.util.*;

/**
 * A simple model of a Giraffe.
 * giraffes age, move, hide from the rain, get sick, eat 
 * acacia trees and die.
 *
 * @version 2020.02.23
 */
public class Giraffe extends Animal
{
    // Characteristics shared by all giraffes (class variables).

    // The age at which a giraffes can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a giraffes can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a giraffes breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;

    /**
     * Create a giraffe. A giraffe can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the giraffe will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Giraffe(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * Look for acacia trees adjacent to the current location.
     * Only the first tree is eaten, but not completely, 
     * reduces its length.
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            if(field.getObjectAt(where) != null) {
                Object plant = field.getObjectAt(where);
                //if tree is found, eat from tree and reduce size
                if(plant instanceof Acacia) {
                    Acacia acacia = (Acacia) plant;
                    if(acacia.isAlive()) {
                        acacia.decreaseSize(10);
                        setFoodLevel(getAcaciaFoodValue());
                        if (acacia.getSize() < 0) {
                            acacia.setDead();
                            return where;
                        }
                    }
                }
            }
        }
        //if food is not found, continue looking
        return null;
    }

    /**
     * Get the set breeding age of the giraffes
     * @return breeding age of giraffes
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Get the maximum age of the of the giraffes
     * @return maximum age of giraffes
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Get the maximum amount of babies of the giraffes
     * @return maximum amount of babies of the giraffes
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Get the probability of breeding of the giraffes
     * @return probability of breeding of the giraffes
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
}