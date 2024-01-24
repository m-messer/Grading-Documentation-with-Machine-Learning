import java.util.List;
import java.util.Iterator;
/**
 * A simple model of a Giraffe.
 * Giraffes age, move,eat Grass and Trees, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Giraffe extends Animal
{
    // Characteristics shared by all Giraffes (class variables).

    // The age at which a Giraffe can start to breed.
    private static final int BREEDING_AGE = 100;
    // The age to which a Giraffe can live.
    private static final int MAX_AGE = 800;
    // The likelihood of a Giraffe breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The time before a Giraffe can mate again after giving birth
    private static final int BREEDING_COOLDOWN = 100;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single Giraffe. In effect, this is the
    // number of steps a Hyena can go before it has to eat again. 
    private static final int FOOD_VALUE = 900;

    /**
     * Create a new Giraffe. A Giraffe may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Giraffe will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Giraffe(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }
    
    /**
     * Giraffes perform bodily functions i.e
     * age, get hungry, give birth etc.
     * If it is not raining and is daytime it roams
     * and finds food etc
     * @param isRaining whether it is raining or not
     */
    public void actDay(boolean isRaining)
    {
        bodilyFunctions();
        roam();
    }
    
    /**
     * Giraffes only perform bodily functions during night
     * and sleep thus not roam and hunt
     */   
    public void actNight(boolean isRaining)
    {
        bodilyFunctions();
    }

    /**
     * @return Time Giraffe can go without eating
     */    
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return Max age until Giraffe lives
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return Breeding age of a Giraffe
     */    
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return Max litter size of a Giraffe
     */    
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return Probability Giraffe breeds succesfully
     */    
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Create a new Giraffe
     * @param field Field of parent Giraffe
     * @param location Location of parent Giraffe 
     * @return The newly born Giraffe
     */
    protected Actor generateOffspring(Field field, Location loc)
    {
        Giraffe young = new Giraffe(false, field, loc);
        return young;
    }
    
    /**
     * @return Time period before Giraffe can breed again after giving birth
     */
    public int getBreedingCooldown()
    {
        return BREEDING_COOLDOWN;
    }
}
