import java.util.*;
import java.awt.Color;
/**
 * A simple model of a stickleback.
 * Sticklebacks age, move, eat, breed, and die.
 *
 * @version 2020.02
 */
public class Stickleback extends Prey
{
    // Characteristics shared by all sticklebacks (class variables).
    
    // The age at which a stickleback can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a stickleback can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a stickleback breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single stickleback. In effect, this is the
    // number of steps a predator of the stickleback can go before it has to eat again.
    private static final int FOOD_VALUE = 9;
    // The set of animals that this one preys apon.
    private static final Set<Class> PREY_SET = new HashSet<>(Arrays.asList(Plant.class));
    // The stickleback's maximum fullness. In effect the
    // maximum number of steps the stickleback can go before it has to eat again.
    private static final int MAX_FULLNESS = 13;
    // The percentage of max fullness the stickleback needs to breed
    private static final double BREEDING_FULLNESS = 0.65;
    // The set of all possible types of tiles a stickleback can walk on
    private static final Set<Class> WALKABLE_TILES = new HashSet<>(Arrays.asList(DeepWater.class, ShallowWater.class));
    // The set of possible colors of this animal, determined by its disease state
    private static final Color[] colors = new Color[] {Color.WHITE, new Color(235, 235, 235), new Color(215, 215, 215)};
    

    /**
     * Create a stickleback. A stickleback can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the stickleback will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     */
    public Stickleback(boolean randomAge, Field field, Location location, int diseaseState)
    {
        super(randomAge, field, location, diseaseState);
    }
    
    /**
     * @return The stickleback's max age.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return The stickleback's breeding age.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return The stickleback's breeding probability.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return The stickleback's max litter size (max amount of babies able to be created at once).
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return The set of the stickleback's prey animals.
     */
    public Set<Class> getPrey()
    {
        return PREY_SET;
    }
    
    /**
     * @return The stickleback's food value to it's predators.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return The stickleback's max fullness.
     */
    public int getMaxFullness()
    {
        return MAX_FULLNESS;
    }
    
    /**
     * @return The animal's breeding fullness value.
     */
    protected double getBreedingFullness()
    {
        return BREEDING_FULLNESS;
    }
    
    /**
     * @return The set of the stickleback's walkable tiles.
     */
    public Set<Class> getWalkableTiles()
    {
        return WALKABLE_TILES;
    }
    
    /**
     * @return the set of the stickleback's possible colors.
     */
    public Color[] getColors()
    {
        return colors;
    }
}
