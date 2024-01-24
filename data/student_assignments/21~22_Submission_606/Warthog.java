
import java.util.List;
import java.util.Random;

/**
 * A simple model of a Warthog.
 *
 * @version 2022.03.01 (15)
 */
public class Warthog extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    private static final int MAX_AGE = 3000;
    private static final int MAX_FOODLEVEL = 300;
    private static final int MAX_THRISTYLEVEL = 200;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.8;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    private static final int FOOD_VALUE = 200;
    private static final int HUNTINGDISTANCE = 10;
    // Individual characteristics (instance fields).

    /**
     * Create a Warthog. A Warthog can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Warthog will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Warthog(boolean randomAge, Field field, Location location,DateTime dateTime)
    {
        super(field, location,dateTime);
        setData(FOOD_VALUE,MAX_AGE, MAX_FOODLEVEL, MAX_THRISTYLEVEL,HUNTINGDISTANCE, MAX_LITTER_SIZE, BREEDING_PROBABILITY);
        addFoodSource(RedOatGrass.class);
        addPredator(WildDog.class);
        addPredator(Lion.class);
        if(randomAge) {
            setRandomAge();
        }
    }

    public void act(List<LivingThing> newAnimals)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        if(getDateTime().isDay()){
            super.act(newAnimals);
        }
    }
}