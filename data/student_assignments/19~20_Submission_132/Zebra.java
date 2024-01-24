import java.util.List;
import java.util.Iterator;
/**
 * A simple model of a Zebra.
 * Zebras age, move,eat Grass, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Zebra extends Animal
{
    // Characteristics shared by all Zebras (class variables).

    // The age at which a Zebra can start to breed.
    private static final int BREEDING_AGE = 50;
    // The age to which a Zebra can live.
    private static final int MAX_AGE = 700;
    // The likelihood of a Zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.15;  
    // The time before a Zebra can mate again after giving birth
    private static final int BREEDING_COOLDOWN = 50;    
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single Zebra. In effect, this is the
    // number of steps a Hyena can go before it has to eat again.
    private static final int FOOD_VALUE = 800;

    /**
     * Create a new Zebra. A Zebra may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Zebra will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
    }

    /**
     * Zebras perform bodily functions i.e
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
     * Zebras only perform bodily functions during night
     * and sleep thus not roam and hunt
     */   
    public void actNight(boolean isRaining)
    {
        bodilyFunctions();
    }

    /**
     * @return Time Zebra can go without eating
     */    
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return Max age until Zebra lives
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return Breeding age of a Zebra
     */    
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * @return Max litter size of a Zebra
     */    
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return Probability Zebra breeds succesfully
     */    
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Create a new Zebra
     * @param field Field of parent Zebra
     * @param location Location of parent Zebra 
     * @return The newly born Zebra
     */
    protected Actor generateOffspring(Field field, Location loc)
    {
        Zebra young = new Zebra(false, field, loc);
        return young;
    }

    /**
     * @return Time period before Zebra can breed again after giving birth
     */
    public int getBreedingCooldown()
    {
        return BREEDING_COOLDOWN;
    }
}
