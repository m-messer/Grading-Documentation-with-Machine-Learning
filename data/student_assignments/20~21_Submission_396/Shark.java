import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.lang.reflect.*;

/**
 * A model of a shark.
 * Sharks age, move, reproduce, eat plankton, and die.
 * They can also become infected and spread disease to other animals.
 *
 * @version 2021.02.28
 */
public class Shark extends Animal
{
    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 0;
    // The age to which a shark can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The class that sharks eat (in this case plankton)
    private static final Class CLASS_TO_EAT = Plankton.class;
    // The food value that a plankton provides to a shark. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int FOOD_VALUE = 100;
    
    /**
     * Create a shark. A shark can be created as a new born (age zero)
     * or with a random age.
     * 
     * @param randomAge If true, the shark will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
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
     * This is what the shark does most of the time: it hunts for
     * plankton. In the process, it might breed, die of: hunger, old age
     * or disease.
     * 
     * @param newSharks A list to return newly born sharks.
     */
    public void act(List<Animal> newSharks)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (getGender() == 'f') {
                giveBirth(newSharks);  
            }
            move(); 
            becomeInfected();
            checkInfection();
        }
    }
    
    /**
     * @return CLASS_TO_EAT The class that sharks eat.
     */
    public Class getClassToEat() {
        return CLASS_TO_EAT;
    }
    
    /**
     * @return MAX_AGE The maximum age of sharks.
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * @return FOOD_VALUE The value that the shark's food level gets set to when it eats.
     */
    public int getFoodValue()
    {
        return FOOD_VALUE;
    }
    
    /**
     * @return BREEDING_PROBABILITY The probability that the shark breeds.
     */
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * @return BREEDING_AGE The minimum age a shark has to be to breed.
     */
    public int getBreedingAge() {
        return BREEDING_AGE;        
    }
    
    /**
     * @return MAX_LITTER_SIZE The maximum number of children a shark can produce when breeding.
     */
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Creates a new shark object and returns it.
     * 
     * @param field The field where the new shark is placed.
     * @param loc The location where the new shark is placed.
     * 
     * @return newShark The new shark object created.
     */
    public Animal createNewAnimal(Field field, Location loc) {
        Shark newShark = new Shark(false, field, loc);
        return newShark;
    }
    
    /**
     * At night, sharks do not move but they still become hungry and age.
     * 
     * @param newAnimals List to store new animals created.
     */
    public void nightBehaviour(List<Animal> newAnimals) {
        incrementAge();
        incrementHunger();
    }
}
