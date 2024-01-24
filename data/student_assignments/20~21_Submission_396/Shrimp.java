import java.util.List;
import java.util.Random;

/**
 * A model of a shrimp.
 * Shrimp age, move, reproduce, eat seaweed, and die.
 * They can also become infected and spread disease to other animals.
 *
 * @version 2021.02.28
 */
public class Shrimp extends Animal
{
    // The age at which a shrimp can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a shrimp can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a shrimp breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The class that shrimp eat (in this case seaweed)
    private static final Class CLASS_TO_EAT = Seaweed.class;
    // The food value that seaweed provides to a shrimp. In effect, this is the
    // number of steps a shrimp can go before it has to eat again.
    private static final int FOOD_VALUE = 100;

    /**
     * Create a new shrimp. A shrimp may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the shrimp will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shrimp(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
        else {
            setAge(0);
        }
    }
    
    /**
     * This is what the shrimp does most of the time: it searches for
     * seaweed. In the process, it might breed, die of: hunger, old age
     * or disease.
     * 
     * @param newShrimp A list to return newly born shrimp.
     */
    public void act(List<Animal> newShrimp)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (getGender() == 'f') {
                giveBirth(newShrimp);
            }          
            move(); 
            becomeInfected();
            checkInfection();
        }
    }
    
    /**
     * @return CLASS_TO_EAT The class that shrimp eat.
     */
    public Class getClassToEat() {
        return CLASS_TO_EAT;
    }
    
    /**
     * Creates a new shrimp object and returns it.
     * 
     * @param field The field where the new shrimp is placed.
     * @param loc The location where the new shrimp is placed.
     * 
     * @return newShrimp The new shrimp object created.
     */
    public Animal createNewAnimal(Field field, Location loc) {
        Shrimp newShrimp = new Shrimp(false, field, loc);
        return newShrimp;
    }

    /**
     * @return MAX_AGE The maximum age of shrimp.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return BREEDING_PROBABILITY The probability that the shrimp breeds.
     */
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return BREEDING_AGE The minimum age a shrimp has to be to breed.
     */
    public int getBreedingAge() {
        return BREEDING_AGE;        
    }
    
    /**
     * @return MAX_LITTER_SIZE The maximum number of children a shrimp can produce when breeding.
     */
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * @return FOOD_VALUE The value that the shrimp's food level gets set to when it eats.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
}
