import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 *
 * @version 12.02.21 
 */
public abstract class Plant extends Organism
{

    //A shared random number generator to control shoot pdoruction.
    protected final Random rand = Randomizer.getRandom();

    /**
     * Create a new plant. A plant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
    }

    /**
     * If age exceeds max age, the plant dies. 
     */
    protected void dieOfAge()
    {
        if(getAge() > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Generate a number representing the number of new shoots,
     * if it can grow.
     * @return The number of shoots (may be zero).
     */
    protected int breed()
    {
        int shoots = 0;
        if(canBreed() && rand.nextDouble() <= getShootProductionProbability()) {
            shoots = rand.nextInt(getMaxShootProduction()) + 1;
        }
        return shoots;
    }

    /**
     * A plant can produce new shoots if it has reached the breeding age.
     * @return true if the plant can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return getAge() >= getShootProductionAge();
    }

    //ABSTRACT METHODS

    /**
     * This is what the plant does most of the time - it grows into new locations.
     * Sometimes it will grow or die of old age.
     * @param newPlants A list to return newly born plants.
     */
    abstract public void act(List<Plant> newPlants);

    /**
     * Check whether or not this plant is to grow at this step.
     * New births will be made into free adjacent locations.
     * @param newPlants A list to return newly born plants.
     */
    abstract public void giveBirth(List<Plant> newPlants); 

    /**
     * Increase the age. This could result in the plant's death.
     */
    abstract public void incrementAge();

    //ACCESSOR METHODS 

    /**
     * Returns the age at which a plant can start to produce shoots.
     * 
     * @return the age at which a plant can start to produce shoots
     */
    abstract public int getShootProductionAge();

    /**
     * Returns the age to which a plant can live.
     * 
     * @return the age to which a plant can live
     */
    abstract public int getMaxAge();

    /**
     * Returns the likelihood of a plant producing shoots. 
     *
     * @return the likelihood of a plant producing shoots
     */
    abstract public double getShootProductionProbability();

    /**
     * Returns the maximum number of shoots that can be produced.
     *
     * @return the maximum number of shoots that can be produced
     */
    abstract public int getMaxShootProduction();

    /**
     * Returns the food value of a single plant. 
     * In effect, this is the food value a plant-eater gets when a plant is eaten.
     *
     * @return the food value of a single plant
     */
    abstract public int getFoodValue();

    /**
     * Returns the plant's age.
     *
     * @return the plant's age
     */
    abstract public int getAge();
}
