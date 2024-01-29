import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Hyena.
 * Provide the data for the animal class.
 *
 * @version 2022.03.01 (15)
 */
public class Hyena extends Animal
{
    private static final int MAX_AGE = 1000;
    private static final int FOOD_VALUE = 30;
    private static final int MAX_FOODLEVEL = 200;
    private static final int MAX_THRISTYLEVEL = 35;
    private static final double BREEDING_PROBABILITY = 0.085;
    private static final int MAX_LITTER_SIZE = 5;
    private static final int HUNTINGDISTANCE = 15;
    private static final int NIGHT_HUNTINGDISTANCE = 13;
    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hyena(boolean randomAge, Field field, Location location,DateTime dateTime)
    {
        super(field, location,dateTime);
        setData(FOOD_VALUE,MAX_AGE, MAX_FOODLEVEL, MAX_THRISTYLEVEL,HUNTINGDISTANCE, MAX_LITTER_SIZE, BREEDING_PROBABILITY);
        addFoodSource(WildDog.class);
        addFoodSource(Topi.class);
        addFoodSource(WildeBeest.class);
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