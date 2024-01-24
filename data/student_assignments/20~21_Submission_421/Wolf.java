import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * Create a fox. A fox can be created as a new born (age zero
 * and not hungry) or with a random age and food level.
 * from class Fox
 * @version 2021.03.02
 */
public class Wolf extends Predator
{
    // The age after which the wolf can breed
    private static final int BREEDING_AGE = 10;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.25;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a wolf can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The wolf's age.
    private int age;
    // The wolf's food level, which is increased by eating rabbits.
    private int foodLevel;
    // The gender of each wolf.
    private String gender;
    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        // initialise instance variables
        super (field, location);

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE;
        }

        //Sets the gender of the wolf.
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
     * class Wolf and returns the casted object.
     * @return Animal wolf.
     */
    protected Animal castAnimal(Object animal)
    {
        Wolf wolf = (Wolf)animal;

        return wolf;
    }

    /**
     * Sets the prey - rabbit.
     */
    protected Animal castPrey(Object animal)
    {
        Rabbit rabbit = (Rabbit) animal;

        return rabbit;
    }

    /**
     * A wolf can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * This method returns the maximum age that a wolf can reach.
     * @return int MAX_AGE
     */
    protected int getMAX_AGE()
    {
        return MAX_AGE;
    }

    /**
     * This method returns the age that a wolf has.
     * @return int age
     */
    protected int getAge()
    {
        return age;
    }

    /**
     *This method returns the breeding probability of a wolf.
     *@return int BREEDING_PROBABILITY
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * This method return the random factor created in the Wolf class.
     * @return Random rand
     */
    protected Random getRandom()
    {
        return rand;
    }

    /**
     * This method returns the max litter size of a wolf.
     * @return int MAX_LITTER_SIZE
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * This method checks wheter the object passed in the method is an instance 
     * of the type of prey that the wolf hunts.
     * @return boolean value
     */
    protected boolean isPrey(Object animal)
    {
        return (animal instanceof Rabbit);
    }

    /**
     * This method assigns the food value of the prey to the food level of each wolf.
     * 
     */
    protected void updateFoodLevel()
    {
        foodLevel =  RABBIT_FOOD_VALUE;
    }

    /**
     * This method returns the food level of a wolf at a given time.
     * @return int foodLevel
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * This method returns the gender of each wolf.
     * @return String gender
     */
    protected String getGender()
    {
        return gender;
    }

    /**
     * This method cheks if the object passed as a parameter is an instance of the 
     * class wolf and returns a boolean value.
     * @return boolean value
     */
    protected boolean checkInstance(Object animal)
    {
        return (animal instanceof Wolf);
    }
}
