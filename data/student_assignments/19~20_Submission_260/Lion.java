import java.util.*;
import java.awt.Color;
/**
 * A simple model of a lion.
 * Lions age, move, eat, breed, and die.
 *
 * @version 2020.02
 */
public class Lion extends Predator
{
    // Characteristics shared by all lions (class variables).
    
    // The age at which a lion can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a lion can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a lion breeding.
    private static final double BREEDING_PROBABILITY = 0.35;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single lion. In effect, this is the
    // number of steps a predator of the lion can go before it has to eat again.
    private static final int FOOD_VALUE = 27;
    // The set of animals that this one preys apon.
    private static final Set<Class> PREY_SET = new HashSet<>(Arrays.asList(Zebra.class));
    // The lion's maximum fullness. In effect the
    // maximum number of steps the lion can go before it has to eat again.
    private static final int MAX_FULLNESS = 20;
    // The percentage of max fullness the lion needs to breed
    private static final double BREEDING_FULLNESS = 0.70;
    // The set of all possible types of tiles a lion can walk on
    private static final Set<Class> WALKABLE_TILES = new HashSet<>(Arrays.asList(Grass.class, Sand.class, SeaBed.class));
    // The set of possible colors of this animal, determined by its disease state
    private static final Color[] colors = new Color[] {Color.BLUE, new Color(0, 0, 235), new Color(0, 0, 215)};

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     */
    public Lion(boolean randomAge, Field field, Location location, int diseaseState)
    {
        super(randomAge, field, location, diseaseState);
    }
    
    /**
     * @return The lion's max age.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return The lion's breeding age.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return The lion's breeding probability
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return The lion's max litter size (max amount of babies able to be created at once)
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return The set of the lion's prey animals.
     */
    public Set<Class> getPrey()
    {
        return PREY_SET;
    }
    
    /**
     * @return The lion's food value to its predators.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return The lion's max fullness.
     */
    public int getMaxFullness()
    {
        return MAX_FULLNESS;
    }
    
    /**
     * @return The animal's breeding fullness value
     */
    protected double getBreedingFullness()
    {
        return BREEDING_FULLNESS;
    }
    
    /**
     * @return the set of the lion's possible colors.
     */
    public Color[] getColors()
    {
        return colors;
    }
    
    /**
     * @return The lion's walkable tiles as a set
     */
    public Set<Class> getWalkableTiles()
    {
        return WALKABLE_TILES;
    }
}
