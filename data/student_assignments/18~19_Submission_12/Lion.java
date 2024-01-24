
import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Lion.
 * Liones age, move, eat rabbits, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Lion extends Predator{
    // Characteristics shared by all Liones (class variables).
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The Lion's age.
    private int age;
    

    /**
     * Create a Lion. A Lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        Random random = Randomizer.getRandom();
        if(random.nextDouble()< 0.5){
            sexMale = true;
        }else {
            sexMale = false;
        }
        MAX_AGE = 150;
        PREY_FOOD_LEVEL = 30;
        PLANT_FOOD_LEVEL = 5;
        MAX_LITTER_SIZE = 2;
        BREEDING_PROBABILITY = 0.2;
        BREEDING_AGE = 10;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PREY_FOOD_LEVEL);
        }
        else {
            
            age = 0;
            foodLevel = PREY_FOOD_LEVEL;
        }
    }
      
    public Base returnMyType(boolean randomAge, Field field, Location location){
        return new Lion(randomAge, field, location);
    }
    
}
