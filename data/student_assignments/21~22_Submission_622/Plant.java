import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Class to depict actions and preset simulation data of animals.
 * Subclass of species which holds primary simulation data.
 *
 * @version 2022.02.27 (3)
 */
public class Plant extends Species
{
    // Characteristics shared by all plants (class variables).
    
    // The age at which an plant can start to breed.
    private int BREEDING_AGE;
    // The age to which an plant can live.
    private int MAX_AGE;
    // The likelihood of an plant breeding.
    private double BREEDING_PROBABILITY;
    // The likelihood of an plant breeding multiplyer
    private double BREEDING_PROBABILITY_MULT = 1;
    // The maximum number of births.
    private int MAX_LITTER_SIZE;
    // The default food value of a any prey. Each meal increases food val by this number.
    private int DEFAULT_FOOD_VALUE;
    // Random class variable
    private static final Random rand = Randomizer.getRandom();
    
    // Key identifier for identifying type of plant
    // REDUNDANT - CAN USE name.class INSTEAD, WAS USED PRIOR TO IMPLEMENTATION OF SUB CLASS TO PLANT
    public String keyIdentifier;
    
    // Array of key identifiers to match to prey
    // PARTIALLY REDUNDANT - MENTIONED ABOVE - TO BE REPLACES WITH CLASS ARRAY
    private String[] preyKeyIdentifiers;
    
    // The plant's age.
    private int age;
    // The plant's food level
    private int foodLevel;
    // The animal's food value to predators
    private int foodValue;
    
    /**
     * Constructor for objects of class Plant
     * @param randomAge - boolean for random age generation
     * @param field - Field type, field plant is on
     * @param location - Location type, location on field of plant
     * @param keyIdentifier - String of plant type identifier
     * @param preyKeyIdentifiers - String Array of plants prey type identifiers
     * @param breedingAge - Int age plants can breed
     * @param maxAge - Int max age of plants
     * @param breedingProb - Double prob of plants breed
     * @param maxLitterSize - Int of max litter size gen
     * @param defaultFoodVal - Int default food val of plant (aka food increment)
     * @param foodValue - Int type defining the food value given to predators
     */
    public Plant(boolean randomAge, Field field, Location location, String keyIdentifier, String[] preyKeyIdentifiers, int breedingAge, int maxAge, double breedingProb, int maxLitterSize, int defaultFoodVal, int foodValue)
    {
        super(field, location, rand.nextBoolean());
        this.preyKeyIdentifiers = preyKeyIdentifiers;
        this.keyIdentifier = keyIdentifier;
        this.BREEDING_AGE = breedingAge;
        this.MAX_AGE = maxAge;
        this.BREEDING_PROBABILITY = breedingProb;
        this.MAX_LITTER_SIZE = maxLitterSize;
        this.DEFAULT_FOOD_VALUE = defaultFoodVal;
        this.foodValue = foodValue;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEFAULT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = DEFAULT_FOOD_VALUE;
        }
    }
    
    /**
     * Function of plants tasks / acts, plant carries out tasks set such as aging or breeding.
     * @param newPlants - List Type Species to return newly born plants.
     */
    public void act(List<Species> newPlants, Clock clock)
    {
        incrementAge();
        if(isAlive()) {
            if (!clock.isNight())
                giveBirth(newPlants);
        }
    }
    
    /**
     * Accessor function for Key Identifier
     * REDUNDANT
     * @return keyIdentifier - Type String
     */
    public String getKeyIdentifier() {
        return keyIdentifier;
    }
    
    /**
     * Accessor function for Food Level
     * @return foodLevel - Type Int
     */
    public int getFoodLevel() {
        return foodLevel;
    }
    
    /**
     * Accessor function for Food Value
     * @return foodValue - Type Int
     */
    public int getFoodValue() {
        return foodValue;
    }

    /**
     * Increase Age and Check For Death By Max Age.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Function to check and birth plants adjascent to this plant.
     * @param newPlants - List type of Species to append new plants to.
     */
    private void giveBirth(List<Species> newPlants)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            
            createChild(newPlants, false, field, loc);
        }
    }
    
    /**
     * Override function utilised to create child instances of subclass and append to Species.
     * @param newPlants - List type species for appending new plants to
     * @param randomage - Boolean type depicting random age preconfiguration
     * @param field - Field type depicting field plant is on.
     * @param location - Location type depicting location on field of plant.
     */
    public void createChild(List<Species> newPlants, boolean randomAge, Field field, Location location)
    {
        //newPlants.add();
        ;    
    }
        
    /**
     * Generate random number clamped between zero and max litter.
     * @return births - Int type of number of births.
     */    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY * BREEDING_PROBABILITY_MULT) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Boolean checking if animal can breed.
     * @return canBreed - boolean type.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * Mutator func for breed prob mult
     * @param - double breed prob mult
     */
    public void setBreedingProbMult(double breedProb) {
        BREEDING_PROBABILITY_MULT = breedProb;
    }
    
    /**
     * Accessor func for breed prob mult
     * @return - double breed prob mult
     */
    public double getBreedingProb() {
        return BREEDING_PROBABILITY_MULT;
    }
}
