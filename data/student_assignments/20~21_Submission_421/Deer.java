import java.util.List;
import java.util.Random;

/**
 * Write a description of class Deer here.
 *
 *         
 * from class Rabbit
 * @version 2021.03.03
 */
public class Deer extends Prey
{
    // The age at which a deer can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a deer can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a deer breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    private static final int GRASS_FOOD_VALUE = 2;

    // Individual characteristics (instance fields).

    // The deer's age.
    private int age;

    private int foodLevel;

    private String gender;
    /**
     * Create a new deer. A deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, Field field, Location location)
    {
        super(field, location);

        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE);
        }

        //Assigns a gender of the deer.
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
     * A deer can breed if it has reached the breeding age.
     * @return true if the deer can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * This method returns the maximum age that a deer can reach.
     * @return int MAX_AGE
     */
    protected int getMAX_AGE()
    {
        return MAX_AGE;
    }

    /**
     * This method returns the age of a deer.
     * @return int age
     */
    protected int getAge()
    {
        return age;
    }

    /**
     * This method returns the breeding probability of the deer.
     * @return int BREEDING_PROBABILITY
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * This method returns the random factor created in the deer class.
     * @return Random rand.
     */
    protected Random getRandom()
    {
        return rand;
    }

    /**
     * This method return the max litter size of the deer.
     * @return int MAX_LITTER_SIZE
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * This method returns the gender of each deer.
     * @return String gender
     */
    protected String getGender()
    {
        return gender;
    }
    
    /**
     * This method checks if the object passed as a parameter is an instance of the 
     * class Deer and returns a boolean value.
     * @return boolean true is the object is an instance of the class Deer
     */
    protected boolean checkInstance(Object animal)
    {
        return (animal instanceof Deer);
    }

    /**
     * This method cast the object passed as a paramter to the type deer and returns
     * the casted object.
     * @return Animal deer
     */
    protected Animal castAnimal(Object animal)
    {
        Deer deer = (Deer)animal;

        return deer;
    }
    
    /**
     * This method checks if the object passed as a parameter is an instance of the 
     * Deer food source and returns a boolean value.
     * @return boolean true if it is an instance of the food source class.
     * 
     */
    protected boolean isFood(Object plant)
    {
        return (plant instanceof Grass);
    }
    
    /**
     * This method casts the object passed on as a parameter to the type of food source of the deer.
     * and returns the casted object.
     * @return Plant grass
     */
    protected Plant  castFood(Object plant)
    {
        Grass grass = (Grass) plant;

        return grass;
    }
     
    /**
     * This method updates the food level of a deer by assigning the food value of the grass to the foodLevel 
     * variable.
     */
    protected void updateFoodLevel()
    {
        foodLevel = GRASS_FOOD_VALUE;
    }
    
    /**
     * This method returns the food level of each deer.
     * @return int foodLevel.
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }
}
