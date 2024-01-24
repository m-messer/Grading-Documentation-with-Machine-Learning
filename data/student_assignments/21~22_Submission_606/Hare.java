import java.util.List;
import java.util.Random;

/**
 * A simple model of a Hare.
 * Provide the data for the animal class.
 *
 * @version 2022.03.01 (15)
 */
public class Hare extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    private static final int MAX_AGE = 1300;
    private static final int MAX_FOODLEVEL = 50;
    private static final int MAX_THRISTYLEVEL = 70;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.9;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 25;
    private static final int FOOD_VALUE = 40;
    private static final int HUNTINGDISTANCE = 5;
    private static final int NIGHT_HUNTINGDISTANCE = 3;
    // Individual characteristics (instance fields).

    /**
     * Create a new Hare. A Hare may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Hare will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hare(boolean randomAge, Field field, Location location,DateTime dateTime)
    {
        super(field, location,dateTime);
        setData(FOOD_VALUE,MAX_AGE, MAX_FOODLEVEL, MAX_THRISTYLEVEL,HUNTINGDISTANCE, MAX_LITTER_SIZE, BREEDING_PROBABILITY);
        addFoodSource(StarGrass.class);
        addFoodSource(RedOatGrass.class);
        addPredator(WildDog.class);
        if(randomAge) {
            setRandomAge();
        }

    }

    public void act(List<LivingThing> newAnimals)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        setHuntingDistance(HUNTINGDISTANCE);
        if(!getDateTime().isDay()){
            setHuntingDistance(NIGHT_HUNTINGDISTANCE);
        }
        super.act(newAnimals);
    }
}
