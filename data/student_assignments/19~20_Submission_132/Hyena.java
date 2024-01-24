import java.util.List;
import java.util.Iterator;
/**
 * A simple model of a Hyena.
 * Hyenas age, move, eat Zebras and Giraffes, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Hyena extends Animal
{
    // Characteristics shared by all Hyenas (class variables).
    
    // The age at which a Hyena can start to breed.
    private static final int BREEDING_AGE = 60;
    // The age to which a Hyena can live.
    private static final int MAX_AGE = 800;
    // The likelihood of a Hyena breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The time before a Hyena can mate again after giving birth
    private static final int BREEDING_COOLDOWN = 100 ;    
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single Hyena. In effect, this is the
    // number of steps a Hyena can go before it has to eat again.
    private static final int FOOD_VALUE = 120;
    
    /**
     * Create a Hyena. A Hyena can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Hyena will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hyena(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        assert field != null;
    }
    
    /**
     * Hyenas perform bodily functions i.e
     * age, get hungry, give birth etc.
     * If it is not raining and is night it roams
     * and finds food etc
     * @param isRaining whether it is raining or not
     */
    public void actNight(boolean isRaining)
    {
        bodilyFunctions();
        if(!isRaining)
            roam();
    }
    
    /**
     * Hyenas only perform bodily functions during day
     * and sleep thus not roam and hunt
     */
    public void actDay(boolean isRaining)
    {
        bodilyFunctions();
    }
        
    /**
     * @return Time Hyena can go without eating
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return Max age until Hyena lives
     */
     public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return Breeding age of Hyena
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return Max litter size of Hyena
     */
      public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return Probability Hyena breeds succesfully 
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Create a new Hyena
     * @param field Field of parent Hyena
     * @param location Location of parent Hyena 
     * @return The newly born Hyena
     */
    protected Actor generateOffspring(Field field, Location loc)
    {
        Hyena young = new Hyena(false, field, loc);
        return young;
    }
    
    /**
     * @return Time before Hyena is fertile after giving birth
     */
    public int getBreedingCooldown()
    {
        return BREEDING_COOLDOWN;
    }
}
