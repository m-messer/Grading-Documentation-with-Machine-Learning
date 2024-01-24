import java.util.*;

/**
 * A simple model of a Gazelle.
 * gazelles age, move, hide from the rain, get sick, eat bushes and die.
 *
 * @version 2020.02.23
 */
public class Gazelle extends Animal
{
    // Characteristics shared by all gazelles (class variables).

    // The age at which a gazelles can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a gazelles can live.
    private static final int MAX_AGE = 80;
    // The likelihood of a gazelles breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;

    /**
     * Create a gazelle. A gazelle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the gazelle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Look for bushes adjacent to the current location.
     * Only the first bush is eaten.
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
                //if bush is found, eat
                if(plant instanceof Bush) {
                    Bush bush = (Bush) plant;
                    if(bush.isAlive()) {
                        bush.setDead();
                        setFoodLevel(getBushFoodValue());
                        return where;
                    }
                }
            }
        }
        //if food is not found, continue looking
        return null;
    }

    /**
     * Get the set breeding age of the gazelles
     * @return breeding age of gazelles
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Get the maximum age of the of the gazelles
     * @return maximum age of gazelles
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Get the maximum amount of babies of the gazelles
     * @return maximum amount of babies of the gazelles
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Get the probability of breeding of the gazelles
     * @return probability of breeding of the gazelles
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
}
