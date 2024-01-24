import java.util.List;
import java.util.Random;

/**
 * The humble squrril wonders round pooping out babies, 
 * eating oak trees, just living the perfect life.
 *
 */
public class Squirrel extends Prey
{
    // Characteristics shared by all Squirrels (class variables).
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The Squirrel's age.
    private int age;

    /**
     * Create a new Squirrel. A Squirrel may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Squirrel will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Squirrel(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        Random random = Randomizer.getRandom();
        if(random.nextDouble()< 0.5){
            sexMale = true;
        }else {
            sexMale = false;
        }
        age = 0;
        MAX_LITTER_SIZE = 7;
        BREEDING_PROBABILITY = 0.4;
        PLANT_FOOD_LEVEL = 20;
        MAX_AGE = 120;
        BREEDING_AGE = 5;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_LEVEL);
        }
        else {
            
            age = 0;
            foodLevel = PLANT_FOOD_LEVEL;
        }
    }
        
    /**
     * Return the type of the Squirrel
     */
    public Base returnMyType(boolean randomAge, Field field, Location location){
        return new Squirrel(true, field, location);
    }
}
