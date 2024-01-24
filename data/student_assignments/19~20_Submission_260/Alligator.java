import java.util.*;
import java.awt.Color;
/**
 * A simple model of a alligator.
 * Alligators age, move, eat, breed, and die.
 * @version 2020.02
 */
public class Alligator extends Predator
{
    // Characteristics shared by all alligators (class variables).
    
    // The age at which a alligator can start to breed.
    private static final int BREEDING_AGE = 45;
    // The age to which a alligator can live.
    private static final int MAX_AGE = 500;
    // The likelihood of a alligator breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // The food value of a single alligator. In effect, this is the
    // number of steps a predator of the alligator can go before it has to eat again.
    private static final int FOOD_VALUE = 27;
    // The set of animals that this one preys apon.
    private static final Set<Class> PREY_SET = new HashSet<>(Arrays.asList(Zebra.class, Stickleback.class));
    // The alligator's maximum fullness. In effect the
    // maximum number of steps the alligator can go before it has to eat again.
    private static final int MAX_FULLNESS = 9;
    // The percentage of max fullness the lion needs to breed
    private static final double BREEDING_FULLNESS = 0.14;
    // The set of all possible types of tiles an alligator can walk on
    private static final Set<Class> WALKABLE_TILES = new HashSet<>(Arrays.asList(Sand.class, ShallowWater.class));
    // The set of possible colors of this animal, determined by its disease state
    private static final Color[] colors = new Color[] {new Color(153, 153, 0), new Color(133, 133, 0), new Color(113, 113, 0)};

    /**
     * Create a alligator. A alligator can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the alligator will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     */
    public Alligator(boolean randomAge, Field field, Location location, int diseaseState)
    {
        super(randomAge, field, location, diseaseState);
    }
    
    /**
     * Check if the animal is to fall asleep, for Alligator which is Nocturnal - active during night, inactive during day.
     * @param isDay - true if it is day, false if it is night.
     * @param isDay - true if animal is awake, false if it is not.
     */
    @Override
    public boolean isAwake(boolean isDay)
    {
        boolean isAwake = !super.isAwake(isDay);
        return isAwake;
    }
    
    /**
     * @return The alligator's max age.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return The alligator's breeding age.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return The alligator's breeding probability
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return The alligator's max litter size (max amount of babies able to be created at once)
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return The set of the alligator's prey animals.
     */
    public Set<Class> getPrey()
    {
        return PREY_SET;
    }
    
    /**
     * @return The alligator's food value to its predators.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return The alligator's maximum fullness level.
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
     * @return the set of the alligator's's possible colors.
     */
    public Color[] getColors()
    {
        return colors;
    }
    
    /**
     * @return The alligator's walkable tiles
     */
    public Set<Class> getWalkableTiles()
    {
        return WALKABLE_TILES;
    }
}
