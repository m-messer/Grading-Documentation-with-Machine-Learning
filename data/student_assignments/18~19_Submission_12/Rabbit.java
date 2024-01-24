import java.util.List;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, sex, and die.
 *
 */
public class Rabbit extends Prey
{
    // Characteristics shared by all rabbits (class variables).
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The rabbit's age.
    private int age;

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
        super(randomAge, field, location);
        Random random = Randomizer.getRandom();
        if(random.nextDouble()< 0.5){
            sexMale = true;
        }else {
            sexMale = false;
        }
        age = 0;
        //Initial age of the rabbit.
        MAX_LITTER_SIZE = 10;
        //Each rabbit can only breed 10 new rabbits into the simulator.
        BREEDING_PROBABILITY = 0.4;
        //When a rabbit goes to reproduce theres only a 40% chance of a new rabbit being added to the simulator.
        PLANT_FOOD_LEVEL = 20;
        //The amount by which the foodlevel of the rabbit is incressed by after eating a plant.
        MAX_AGE = 120;
        //After this age the rabbit will die.
        BREEDING_AGE = 5;
        //The minimum age of the rabbit before it can breed.
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            //Places a rabbit of random age into the simulator.
            foodLevel = rand.nextInt(PLANT_FOOD_LEVEL);
            //Places a rabbit of ramdom foodlevel into the simulator.
        }
        else {
            age = 0;
            //Places a newly born rabbit into the simulator.
            foodLevel = PLANT_FOOD_LEVEL;
            //Sets the newly born rabbits food level to the plant food level.
        }
    }
        
    /**
     * Return the type of the Rabbit
     */
    public Base returnMyType(boolean randomAge, Field field, Location location){
        return new Rabbit(true, field, location);
    }
    
}
