import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Eagle.
 * Eaglees age, move, eat rabbits, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Eagle extends Predator{
    // Characteristics shared by all Eaglees (class variables).
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The Eagle's age.
    private int age;
    

    /**
     * Create a Eagle. A Eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        Random random = Randomizer.getRandom();
        if(random.nextDouble()< 0.5){
            sexMale = true;
        }else {
            sexMale = false;
        }
        MAX_AGE = 170;
        PREY_FOOD_LEVEL = 30;
        PLANT_FOOD_LEVEL = 4;
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
        return new Eagle(randomAge, field, location);
    }
    
}
