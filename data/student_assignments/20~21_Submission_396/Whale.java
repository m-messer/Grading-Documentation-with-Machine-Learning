import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A model of a whale.
 * Whales age, move, reproduce, eat shrimp, and die.
 * They can also become infected and spread disease to other animals.
 *
 * @version 2021.02.28
 */
public class Whale extends Animal
{
    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 0;
    // The age to which a whale can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The class that whales eat (in this case shrimp)
    private static final Class CLASS_TO_EAT = Shrimp.class;
    // The food value that a shrimp provides to a whale. In effect, this is the
    // number of steps a whale can go before it has to eat again.
    private static final int FOOD_VALUE = 100;

    /**
     * Create a whale. A whale can be created as a new born (age zero)
     * or with a random age.
     * 
     * @param randomAge If true, the whale will have random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Whale(boolean randomAge, Field field, Location location)
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
     * This is what the whale does most of the time: it hunts for
     * shrimp. In the process, it might breed, die of: hunger, old age
     * or disease.
     * 
     * @param newWhales A list to return newly born whales.
     */
    public void act(List<Animal> newWhales)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (getGender() == 'f') {
                giveBirth(newWhales);
            }          
            move();  
            becomeInfected();
            checkInfection();
        }
    }

    /**
     * @return CLASS_TO_EAT The class that whales eat.
     */
    public Class getClassToEat() {
        return CLASS_TO_EAT;
    }

    /**
     * Creates a new whale object and returns it.
     * 
     * @param field The field where the new whale is placed.
     * @param loc The location where the new whale is placed.
     * 
     * @return newWhale The new whale object created.
     */
    public Animal createNewAnimal(Field field, Location loc) {
        Whale newWhale = new Whale(false, field, loc);
        return newWhale;
    }

    /**
     * @return BREEDING_PROBABILITY The probability that the whale breeds.
     */
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * @return BREEDING_AGE The minimum age a whale has to be to breed.
     */
    public int getBreedingAge() {
        return BREEDING_AGE;        
    }

    /**
     * @return MAX_LITTER_SIZE The maximum number of children a whale can produce when breeding.
     */
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * @return MAX_AGE The maximum age of whales.
     */
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * @return FOOD_VALUE The value that the whale's food level gets set to when it eats.
     */
    public int getFoodValue() {
        return FOOD_VALUE;
    }

    /**
     * At night, whales do not move but they still become hungry and age.
     * 
     * @param newAnimals List to store new animals created.
     */
    public void nightBehaviour(List<Animal> newAnimals) {
        incrementAge();
        incrementHunger();
    }
}
