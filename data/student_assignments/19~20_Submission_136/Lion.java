import java.util.*;

/**
 * A simple model of a lion.
 * lions age, move, hide from the rain, get sick, eat baboons, zebras, 
 * and gazelles, and die.
 *
 * @version 2020.02.23
 */
public class Lion extends Animal
{
    // Characteristics shared by all lions (class variables).

    // The age at which a lions can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a lions can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a lions breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * Look for baboons, zebras, and gazelles adjacent to the current location.
     * Only the first live baboon, zebra, or gazelle is eaten.
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
                Object animal = field.getObjectAt(where);
                //if zebra is found first, eat
                if(animal instanceof Zebra) {
                    Zebra zebra = (Zebra) animal;
                    if(zebra.isAlive()) {
                        zebra.setDead();
                        setFoodLevel(getZebraFoodValue());
                        return where;
                    }
                }
                //if gazelle is found first, eat
                else if(animal instanceof Gazelle)
                {
                    Gazelle gazelle = (Gazelle) animal;
                    if(gazelle.isAlive()) {
                        gazelle.setDead();
                        setFoodLevel(getGazelleFoodValue());
                        return where;
                    }
                }
                //if baboon is found first, eat
                else if(animal instanceof Baboon)
                {
                    Baboon baboon = (Baboon) animal;
                    if(baboon.isAlive()) {
                        baboon.setDead();
                        setFoodLevel(getBaboonFoodValue());
                        return where;
                    }
                }
            }
        }
        //if food is not found, continue looking
        return null;
    }

    /**
     * Get the set breeding age of the lions
     * @return breeding age of lions
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Get the maximum age of the of the lions
     * @return maximum age of lions
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Get the maximum amount of babies of the lions
     * @return maximum amount of babies of the lions
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Get the probability of breeding of the lions
     * @return probability of breeding of the lions
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
}
