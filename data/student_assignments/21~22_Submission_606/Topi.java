
/**
 * A simple model of a Topi.
 * Provide the data for the animal class.
 *
 * @version 2022.03.01 (15)
 */
public class Topi extends Animal
{
    private static final int MAX_AGE = 3000;
    private static final int MAX_FOODLEVEL = 400;
    private static final int MAX_THRISTYLEVEL = 120;
    private static final double BREEDING_PROBABILITY = 0.9;
    private static final int MAX_LITTER_SIZE = 8;
    private static final int FOOD_VALUE = 200;
    private static final int HUNTINGDISTANCE = 8;

    /**
     * Constructor for objects of class Topi
     */
    public Topi(boolean randomAge, Field field, Location location, DateTime dateTime)
    {
         super(field, location, dateTime);
         setData(FOOD_VALUE,MAX_AGE, MAX_FOODLEVEL, MAX_THRISTYLEVEL,HUNTINGDISTANCE, MAX_LITTER_SIZE, BREEDING_PROBABILITY);
        if(randomAge) {
            setRandomAge();
        }
        addFoodSource(RedOatGrass.class);
        addPredator(Lion.class);
        addPredator(Hyena.class);
    }
}
