import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of tuna.
 * Tuna age, move, breed, and die.
 * They can also become infected and spread disease to other animals.
 *
 * @version 2021.03.02
 */
public class Tuna extends Animal
{
    // Characteristics shared by all tuna (class variables).

    // The age at which a tuna can start to breed.
    private static final int BREEDING_AGE = 0;
    // The age to which a tuna can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a tuna breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value that a plankton provides to a tuna. In effect, this is the
    // number of steps a tuna can go before it has to eat again.
    private static final int FOOD_VALUE = 100;
    // The class that tuna eat (in this case plankton)
    private static final Class CLASS_TO_EAT = Plankton.class;
    // Individual characteristics (instance fields).
    /**
     * Create a new tuna. A tuna may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the tuna will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tuna(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setAge(0);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
    }
    
    /**
     * This is what the tuna does most of the time - it swims 
     * around. Sometimes it will breed, die of old age or become infected.
     * 
     * @param newTuna A list to return newly born tuna.
     */
    public void act(List<Animal> newTuna)
    {
        incrementAge();
        incrementHunger(); 
        if(isAlive()) {
            if (getGender() == 'f') {
                giveBirth(newTuna);
            }
            move();
            becomeInfected();
            checkInfection();
        }
    }
    
    /**
     * Creates a new tuna object and returns it.
     * 
     * @param field The field where the new tuna is placed.
     * @param loc The location where the new tuna is placed.
     * 
     * @return newTuna The new tuna object created.
     */
    public Animal createNewAnimal(Field field, Location loc) {
        Tuna newTuna = new Tuna(false, field, loc);
        return newTuna;
    }
    
    /**
     * @return CLASS_TO_EAT The class that tuna eat.
     */
    public Class getClassToEat() {
        return CLASS_TO_EAT;
    }
    
    /**
     * @return BREEDING_PROBABILITY The probability that the tuna breeds.
     */
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return BREEDING_AGE The minimum age a tuna has to be to breed.
     */
    public int getBreedingAge() {
        return BREEDING_AGE;        
    }
    
    /**
     * @return MAX_LITTER_SIZE The maximum number of children a tuna can produce when breeding.
     */
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }
   
    /**
     * @return MAX_AGE The maximum age of tuna.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return FOOD_VALUE The value that the tuna food level gets set to when it eats.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * During the night, tuna will not move but will still age and get hungry.
     * @param newAnimals List to store new animals created.
     */
    public void nightBehaviour(List<Animal> newAnimals) {
        incrementAge();
        incrementHunger();
    }
}
