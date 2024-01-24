import java.util.*;
import java.awt.Color;
/**
 * A simple model of a pike.
 * Pikes age, move, eat, breed, and die.
 *
 * @version 2020.02
 */
public class Pike extends Predator
{
    // Characteristics shared by all pikes (class variables)
    
    // The age at which a pike can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a pike can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a pike breeding.
    private static final double BREEDING_PROBABILITY = 0.26;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single pike. In effect, this is the
    // number of steps a predator of the pike can go before it has to eat again.
    private static final int FOOD_VALUE = 27;
    // The set of animals that this one preys apon.
    private static final Set<Class> PREY_SET = new HashSet<>(Arrays.asList(Stickleback.class));
    // The pike's maximum fullness. In effect the
    // maximum number of steps the pike can go before it has to eat again.
    private static final int MAX_FULLNESS = 20;
    // The percentage of max fullness the pike needs to breed
    private static final double BREEDING_FULLNESS = 0.65;
    // The set of all possible types of tiles a pike can walk on
    private static final Set<Class> WALKABLE_TILES = new HashSet<>(Arrays.asList(DeepWater.class));
    // The set of possible colors of this animal, determined by its disease state
    private static final Color[] colors = new Color[] {new Color(204, 102, 0), new Color(178, 89, 0), new Color(143, 73, 0)};

    /**
     * Create a pike. A pike can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the pike will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     */
    public Pike(boolean randomAge, Field field, Location location, int diseaseState)
    {
        super(randomAge, field, location, diseaseState);
    }

    /**
     * @return The pike's max age.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return The pike's breeding age
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return The pike's breeding probability
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return The pike's max litter size (max amount of babies able to be created at once)
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return The set of the pike's prey animals.
     */
    public Set<Class> getPrey()
    {
        return PREY_SET;
    }
    
    /**
     * @return The pike's food value to it's predators.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return The pike's maximum fullness level.
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
     * @return the set of the animal's possible colors.
     */
    public Color[] getColors()
    {
        return colors;
    }
    
    /**
     * @return The pike's walkable tiles
     */
    public Set<Class> getWalkableTiles()
    {
        return WALKABLE_TILES;
    }
}
