import java.util.List;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 *
 * @version 2021.03.02
 */
public class Rabbit extends Prey
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    private static final int GRASS_FOOD_VALUE = 10;

    // Individual characteristics (instance fields).

    // The rabbit's age.
    private int age;

    private int foodLevel;

    private String gender;
    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rabbit(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        foodLevel = GRASS_FOOD_VALUE;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE);
        }

        //Sets a rabbit's gender.
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
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * This method returns the maximum age that a rabbit can reach.
     * @return int MAX_AGE
     */
    protected int getMAX_AGE()
    {
        return MAX_AGE;
    }

    /**
     * This method returns the age of a rabbit.
     * @return int age
     */
    protected int getAge()
    {
        return age;
    }

    /**
     * This method returns the breeding probability of the rabbit.
     * @return int BREEDING_PROBABILITY
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * This method returns the random factor created in the Rabbit class.
     * @return Random rand.
     */
    protected Random getRandom()
    {
        return rand;
    }

    /**
     * This method return the max litter size of the rabbit.
     * @return int MAX_LITTER_SIZE
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * This method returns the gender of each rabbit.
     * @return String gender
     */
    protected String getGender()
    {
        return gender;
    }
    
    /**
     * This method checks if the object passed as a parameter is an instance of this class.
     * and returns a boolean value as a result
     * @return true is the object is an instance of the class.
     */
    protected boolean checkInstance(Object animal)
    {
        return (animal instanceof Rabbit);
    }

    
    /**
     * This method cast the object passed as a paramter to the type Rabbit and returns
     * the casted object.
     * @return Animal rabbit
     */
    protected Animal castAnimal(Object animal)
    {
        Rabbit rabbit = (Rabbit)animal;
        
        return rabbit;
    }
    
    /**
     * This method checks if the object passed as a parameter is an instance of the rabbit's 
     * food source.
     * @return true is the object is an instance of the rabbit's food source.
     */
    protected boolean isFood(Object plant)
    {
        return (plant instanceof Grass);
    }
    
    /**
     * This method cast the object passed as a paramter to the rabbit's food source type
     * and returns the casted object.
     */
    protected Plant  castFood(Object plant)
    {
       Grass grass = (Grass) plant;
       
       return grass;
    }
    
    /**
     * This method updates the foodLevel of each rabbit by assigning the value of the food source
     * to the variable foodLevel
     * 
     */
    protected void updateFoodLevel()
    {
        foodLevel = GRASS_FOOD_VALUE;
    }
    
    /**
     * Returns the current food level of a rabbit.
     * @return int foodLevel
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }
}
