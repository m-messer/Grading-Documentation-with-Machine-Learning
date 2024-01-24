import java.util.*;
import java.awt.Color;
/**
 * A simple model of a zebra.
 * Zebras age, move, eat, breed, and die.
 *
 * @version 2020.02
 */
public class Zebra extends Prey
{
    // The age at which a zebra can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a zebra can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.74;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single zebra. In effect, this is the
    // number of steps a predator of the zebra can go before it has to eat again.
    private static final int FOOD_VALUE = 9;
    // The set of animals that this one preys apon.
    private static final Set<Class> PREY_SET = new HashSet<>(Arrays.asList(Plant.class));
    // The zebra's maximum fullness. In effect the
    // maximum number of steps the zebra can go before it has to eat again.
    private static final int MAX_FULLNESS = 13;
    // The percentage of max fullness the zebra needs to breed
    private static final double BREEDING_FULLNESS = 0.688;
    // The set of all possible types of tiles a zebra can walk on
    private static final Set<Class> WALKABLE_TILES = new HashSet<>(Arrays.asList(Grass.class, ShallowWater.class, Sand.class, Rock.class, SeaBed.class));
    // The set of possible colors of this animal, determined by its disease state
    private static final Color[] colors = new Color[] {Color.YELLOW, new Color(235, 235, 0), new Color(215, 215, 0)};
    // Individual characteristics (instance fields).


    /**
     * Create a new zebra. A zebra may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the zebra will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     */
    public Zebra(boolean randomAge, Field field, Location location, int diseaseState)
    {
        super(randomAge, field, location, diseaseState);
    }
    
    /**
     * @return The zebra's max age.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return The zebra's breeding age.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return The zebra's breeding probability.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return The zebra's maximum litter size.
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return The set of the zebra's prey animals.
     */
    public Set<Class> getPrey()
    {
        return PREY_SET;
    }
    
    /**
     * @return The zebra's food value to it's predators.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return The zebra's max fullness.
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
     * @return the set of the zebra's possible colors.
     */
    public Color[] getColors()
    {
        return colors;
    }
    
    /**
     * @return The zebra's walkable tiles
     */
    public Set<Class> getWalkableTiles()
    {
        return WALKABLE_TILES;
    }
}