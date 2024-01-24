import java.util.Random;
import java.util.List;
/**
 * A simple model of a bear.
 * Bears age, move, eat deer, and die.
 *
 
 * from class Fox
 * @version 2021.03.02
 */
public class Bear extends Predator
{
    // The age after which the bear can breed
    private static final int BREEDING_AGE = 9;
    // The age to which a bear can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a bear breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single deer. In effect, this is the
    // number of steps a bear can go before it has to eat again.
    private static final int DEER_FOOD_VALUE = 20;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The bear's age.
    private int age;
    // The bear's food level, which is increased by eating rabbits.
    private int foodLevel;
    // The gender of each bear.
    private String gender;
    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        // initialise instance variables
        super(field, location);

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEER_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = DEER_FOOD_VALUE;
        }

        //Sets the bear's gender.
        if(rand.nextInt(10) % 2 == 0)
        {
            gender = "male";
        }
        else
        {
            gender = "female";
        }
    }

    /**
     * This method cast the object passed as a parameter to the type of the 
     * class bear and returns the casted object.
     * @return Animal bear.
     */
    protected Animal castAnimal(Object animal)
    {
        Bear bear = (Bear)animal;

        return bear;
    }

    /**
     * Sets the prey - deer
     */
    protected Animal castPrey(Object animal)
    {
        Deer deer = (Deer) animal;

        return deer;
    }

    /**
     * A bear can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * This method returns the maximum age that a bear can reach.
     * @return int MAX_AGE
     */
    protected int getMAX_AGE()
    {
        return MAX_AGE;
    }

    /**
     * This method returns the age that a bear has.
     * @return int age
     */
    protected int getAge()
    {
        return age;
    }

    /**
     *This method returns the breeding probability of a bear.
     *@return int BREEDING_PROBABILITY
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * This method return the random factor created in the Bear class.
     * @return Random rand
     *
     */
    protected Random getRandom()
    {
        return rand;
    }

    /**
     * This method returns the max litter size of a bear.
     * @return int MAX_LITTER_SIZE
     * 
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * This method checks wheter the object passed in the method is an instance 
     * of the type of prey that the bear hunts.
     * @return boolean value
     */
    protected boolean isPrey(Object animal)
    {
        return (animal instanceof Deer);
    }

    /**
     * This method assigns the food value of the prey to the food level of each bear.
     */
    protected void updateFoodLevel()
    {
        foodLevel =  DEER_FOOD_VALUE;
    }

    /**
     * This method returns the food level of a bear at a given time.
     * @return int foodLevel
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * This method returns the gender of each bear.
     * @return String gender
     */
    protected String getGender()
    {
        return gender;
    }

    /**
     * This method cheks if the object passed as a parameter is an instance of the 
     * class Bear and returns a boolean value.
     * @return boolean value
     */
    protected boolean checkInstance(Object animal)
    {
        return (animal instanceof Bear);
    }
}
