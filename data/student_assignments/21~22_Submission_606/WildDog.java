import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a WildDog.
 *
 * @version 2022.03.01 (15)
 */
public class WildDog extends Animal
{
    // Characteristics shared by all foxes (class variables).

    private static final int MAX_AGE = 1200;
    private static final int FOOD_VALUE = 100;
    private static final int MAX_FOODLEVEL = 150;
    private static final int MAX_THRISTYLEVEL = 30;
    private static final double BREEDING_PROBABILITY = 0.4;
    private static final int MAX_LITTER_SIZE = 5;
    private static final int HUNTINGDISTANCE = 6;
    private static final int NIGHT_HUNTINGDISTANCE = 5;
    // Individual characteristics (instance fields).
    /**
     * Create a WildDog. A WildDog can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the WildDog will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public WildDog(boolean randomAge, Field field, Location location,DateTime dateTime)
    {
        super(field, location,dateTime);
        setData(FOOD_VALUE,MAX_AGE, MAX_FOODLEVEL, MAX_THRISTYLEVEL,HUNTINGDISTANCE, MAX_LITTER_SIZE, BREEDING_PROBABILITY);
        addFoodSource(Hare.class);
        addFoodSource(Warthog.class);
        addPredator(Hyena.class);
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
