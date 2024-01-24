import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A model of a Seaweed.
 * Seaweed grow.
 *
 * @version 2021.02.28
 */
public class Seaweed extends Plant
{
    // The likelihood of a seaweed growing.
    private static final double GROWTH_PROBABILTY = 0.08;
    // A shared random number generator to control growth.
    private static final Random random = Randomizer.getRandom();

    /**
     * Constructor for objects of class Seaweed
     * 
     * @param field The field the seaweed is located.
     * @param location The location the seaweed is located.
     */
    public Seaweed(Field field, Location location)
    {
        super(field, location);
    }
    
    /**
     * Make this seaweed act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newPlants A list to receive newly born plants.
     */
    public void act(List<Plant> newPlants){
        if (isAlive()) {
            double growthCheck = random.nextDouble();
        
            if (growthCheck <= getGrowthProbability()) {
                grow(newPlants);
            }
        }
    }
    
    /**
     * Creates a new seaweed object and returns it.
     * 
     * @param field The field where the new seaweed is placed.
     * @param loc The location where the new seaweed is placed.
     * 
     * @return newSeaweed The new seaweed object created.
     */
    public Plant createNewPlant(Field field, Location location) {
        Seaweed newSeaweed = new Seaweed(field, location);
        return newSeaweed;
    }
    
    /**
     * @return GROWTH_PROBABILTY The growth probability of the seaweed.
     */
    public double getGrowthProbability(){
        return GROWTH_PROBABILTY;
    }
}
