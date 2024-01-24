
/**
 * A simple model of a WildeBee.
 * Provide the data for the animal class.
 *
 * @version 2022.03.01 (15)
 */
public class WildeBeest extends Animal
{
    private static final int MAX_AGE = 3000;
    private static final int MAX_FOODLEVEL = 200;
    private static final int MAX_THRISTYLEVEL = 120;
    private static final double BREEDING_PROBABILITY = 0.8;
    private static final int MAX_LITTER_SIZE = 8;
    private static final int FOOD_VALUE = 200;
    private static final int HUNTINGDISTANCE = 20;
    /**
     * Constructor for objects of class WildBees
     */
    public WildeBeest(boolean randomAge, Field field, Location location, DateTime dateTime)
    {
        super(field, location, dateTime);
        setData(FOOD_VALUE,MAX_AGE, MAX_FOODLEVEL, MAX_THRISTYLEVEL,HUNTINGDISTANCE, MAX_LITTER_SIZE, BREEDING_PROBABILITY);
        addFoodSource(StarGrass.class);
        addPredator(Lion.class);
        addPredator(Hyena.class);
        if(randomAge) {
            setRandomAge();
        }
    }
}
