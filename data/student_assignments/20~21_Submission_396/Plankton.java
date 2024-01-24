import java.util.List;
import java.util.Random;

/**
 * A simple model of plankton.
 * Planktons age, move, breed, and die.
 * They can also become infected and spread disease to other animals.
 *
 * @version 2021.03.02
 */
public class Plankton extends Animal
{
    // Characteristics shared by all planktons (class variables).

    // The age at which a plankton can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a plankton can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a plankton breeding.
    private static final double BREEDING_PROBABILITY = 0.09;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value that seaweed provides to a plankton. In effect, this is the
    // number of steps a plankton can go before it has to eat again.
    private static final int FOOD_VALUE = 100;
    // The class that plankton eat (in this case seaweed)
    private static final Class CLASS_TO_EAT = Seaweed.class;

    /**
     * Create a new plankton. A plankton may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the plankton will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plankton(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setAge(0);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
    }
    
    /**
     * This is what the plankton does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * @param newPlanktons A list to return newly born planktons.
     */
    public void act(List<Animal> newPlanktons)
    {
        incrementAge();
        incrementHunger();

        if(isAlive()) {
            if (getGender() == 'f') {
                giveBirth(newPlanktons);
            }           
            move();
            becomeInfected();
            checkInfection();
        }
    }
    
    /**
     * @return CLASS_TO_EAT The class that plankon eat.
     */
    public Class getClassToEat() {
        return CLASS_TO_EAT;
    }
    
    /**
     * Creates a new plankon object and returns it.
     * 
     * @param field The field where the new plankon is placed.
     * @param loc The location where the new plankon is placed.
     * 
     * @return newPlankton The new plankon object created.
     */
    public Animal createNewAnimal(Field field, Location loc) {
        Plankton newPlankton = new Plankton(false, field, loc);
        return newPlankton;
    }
    
    /**
     * @return BREEDING_PROBABILITY The probability that the plankton breeds.
     */
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return BREEDING_AGE The minimum age a plankton has to be to breed.
     */
    public int getBreedingAge() {
        return BREEDING_AGE;        
    }
    
    /**
     * @return MAX_LITTER_SIZE The maximum number of children a plankton can produce when breeding.
     */
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * @return MAX_AGE The maximum age of plankton.
    */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return FOOD_VALUE The value that the plankton's food level gets set to when it eats.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
}
