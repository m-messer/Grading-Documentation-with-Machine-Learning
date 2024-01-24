import java.util.List;
import java.util.Iterator;
/**
 * A simple model of a Lion.
 * Lions age, move, eat Zebras and Giraffes, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Lion extends Animal
{
    // Characteristics shared by all Lions (class variables).
    
    // The age at which a Lion can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which a Lion can live.
    private static final int MAX_AGE = 900;
    // The likelihood of a Lion breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The time before a Lion can mate again after giving birth
    private static final int BREEDING_COOLDOWN = 150;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single Lion.    
    private static final int LION_FOOD_VALUE = 300;

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * @return Max age until Lion lives
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Lions perform bodily functions i.e
     * age, get hungry, give birth etc.
     * If it is not raining and is daytime it roams
     * and finds food etc
     * @param isRaining whether it is raining or not
     */
    public void actDay(boolean isRaining)
    {
        bodilyFunctions();
        if (!isRaining)
            roam();
    }
    
    /**
     * Lions only perform bodily functions during night
     * and sleep thus not roam and hunt
     */    
    public void actNight(boolean isRaining)
    {
        bodilyFunctions();
    }
    
    /**
     * @return Time Lion can go without eating
     */    
    public int getFoodValue()
    {
        return LION_FOOD_VALUE;
    }

    /**
     * @return Breeding age of a Lion
     */    
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return Max litter size of a Lion
     */    
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return Probability Lion breeds succesfully
     */    
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Create a new Lion
     * @param field Field of parent Lion
     * @param location Location of parent Lion 
     * @return The newly born Lion
     */
    protected Actor generateOffspring(Field field, Location loc)
    {
        Lion young = new Lion(false, field, loc);
        return young;
    }
    
    /**
     * @return Time period before Lion can breed again after giving birth
     */
    public int getBreedingCooldown()
    {
        return BREEDING_COOLDOWN;
    }
}
