import java.util.Random;
import java.util.List; 
/**
 * This class represents a single piece of algae.
 * Algae can grow, be eaten and therefore decrease in health, and eventuallly die.
 *
 * @version (1.0)
 */
public class Algae extends Animal 
{
    // The age to which algae can live.
    private static final int MAX_AGE = 80;
    //The amount each plant is close to dying
    private int health = 60;
    // The likelihood of algae growing.
    private static double GROWTH_PROBABILITY = 0.01;
    // The maximum number of locations the algae can grow into.
    private static final int GROWTH_SIZE = 4;
    // A shared random number generator to control growth.
    private static final Random rand = Randomizer.getRandom();
    // the age of a given plant
    private int age;
    
    /**
     * Constructor for objects of class Algae
     * @param randomAge If true, the algae will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Algae(boolean randomAge, Field field, Location location, Step step)
    {
        super(field, location, step);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
    }
    
    /**
     * This is what the algae does most of the time at Day time: it 
     * checks to see if it can grow, and it ages and eventually dies.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void actDay(List<Animal> newAlgae)
    {
        incrementAge();
        if(isAlive()) {
            grow(newAlgae);            
        }
    }
    
    /**
     * As algae is a plant, it doesn't tend to act at night, when
     * there is no sunlight.
     */
    public void actNight(List<Animal> newAlgae)
    {
        
    }
    
    /**
     * Increase the age. This could result in the algae's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * This decreases the probability that the algae will grow
     */
    public static void decreaseGrowth(){
        GROWTH_PROBABILITY -= 0.0002;
    }
    
    /**
     * Check whether or not this plant can grow at this step.
     * New plants will be put into free adjacent locations.
     * @param newAlgae A list to return newly grown algae.
     */
    private void grow(List<Animal> newAlgae)
    {
        // New algae grows into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        Step step = getStep();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = canGrow();
        for(int g = 0; g < births && free.size() > 0; g++) {
            Location loc = free.remove(0);
            Algae sapling = new Algae(false, field, loc, step);
            newAlgae.add(sapling);
        }
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int canGrow() 
    {
        int growths = 0;
        if(rand.nextDouble() <= GROWTH_PROBABILITY) {
            growths = rand.nextInt(GROWTH_SIZE) + 1;
        }
        return growths;
    }
   
    public void decreaseHealth()
    {
        health--; 
        if( health <= 0 ){
            setDead();
        }
    }
    
    public void resetGrowth()
    {
       GROWTH_PROBABILITY = 0.01;
    }
}
