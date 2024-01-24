import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Class to depict actions and preset simulation data of animals.
 * Subclass of species which holds primary simulation data.
 *
 * @version 2022.02.27 (4)
 */
public class Animal extends Species
{
    // Characteristics shared by all animals (class variables).
    
    // The age at which an animal can start to breed.
    private int BREEDING_AGE;
    // The age to which an animal can live.
    private int MAX_AGE;
    // The likelihood of an animal breeding.
    private double BREEDING_PROBABILITY;
    // The maximum number of births.
    private int MAX_LITTER_SIZE;
    // The default food value of a any prey. Each meal increases food val by this number.
    private int DEFAULT_FOOD_VALUE;
    // Random class variable
    private static final Random rand = Randomizer.getRandom();
    
    // Key identifier for identifying type of animal
    // REDUNDANT - CAN USE name.class INSTEAD, WAS USED PRIOR TO IMPLEMENTATION OF SUB CLASS TO ANIMAL
    public String keyIdentifier;
    
    // Array of key identifiers to match to prey
    // PARTIALLY REDUNDANT - MENTIONED ABOVE - TO BE REPLACES WITH CLASS ARRAY
    private String[] preyKeyIdentifiers;
    
    
    // The animal's age.
    private int age;
    // The animal's food level
    private int foodLevel;
    // The animal's food value to predators
    private int foodValue;
    // If the animal sleeps
    private boolean doesSleep;
    
    /**
     * Constructor for objects of class Animal
     * @param randomAge - boolean for random age generation
     * @param field - Field type, field animal is on
     * @param location - Location type, location on field of animal
     * @param keyIdentifier - String of animal type identifier
     * @param preyKeyIdentifiers - String Array of animals prey type identifiers
     * @param breedingAge - Int age animals can breed
     * @param maxAge - Int max age of animals
     * @param breedingProb - Double prob of animals breed
     * @param maxLitterSize - Int of max litter size gen
     * @param defaultFoodVal - Int default food val of animal (aka food increment)
     * @param doesSleep - Boolean type defining if animal is nocturnal
     * @param foodValue - Int type defining the food value given to predators
     */
    public Animal(boolean randomAge, Field field, Location location, String keyIdentifier, String[] preyKeyIdentifiers, int breedingAge, int maxAge, double breedingProb, int maxLitterSize, int defaultFoodVal, boolean doesSleep, int foodValue)
    {
        super(field, location, rand.nextBoolean());
        this.preyKeyIdentifiers = preyKeyIdentifiers;
        this.keyIdentifier = keyIdentifier;
        this.BREEDING_AGE = breedingAge;
        this.MAX_AGE = maxAge;
        this.BREEDING_PROBABILITY = breedingProb;
        this.MAX_LITTER_SIZE = maxLitterSize;
        this.DEFAULT_FOOD_VALUE = defaultFoodVal;
        this.doesSleep = doesSleep;
        this.foodValue = foodValue;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = DEFAULT_FOOD_VALUE;
    }
    
    /**
     * Function of animals tasks / acts, animal carries out tasks set such as hunting or breeding.
     * @param newAnimals - List Type Species to return newly born animals.
     */
    public void act(List<Species> newAnimals, Clock clock)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (clock.isNight() != doesSleep)
                giveBirth(newAnimals);            
            // Move towards a source of food if found. if hungry
            Location newLocation = null;
            if (foodLevel < DEFAULT_FOOD_VALUE)
                newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                if (clock.isNight() != doesSleep)
                    setLocation(newLocation);
            } else {
                Field field = getField();
                newLocation = field.randomAdjacentLocation(getLocation());
                if (field.getObjectAt(newLocation) instanceof Plant) {
                    Plant inTheWay = (Plant) field.getObjectAt(newLocation);
                    inTheWay.setDead();
                    setLocation(newLocation);
                } else {
                    this.setDead();
                }
            }
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
     * Increase Hunger by reducing food level, check for death by starvation.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for prey adjacent to the current location. Hunt and move to first prey found.
     * @return where - Location type of prey.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(isAnimalPrey(animal)) {
                Animal prey = (Animal) animal;
                if(prey.isAlive()) { 
                    prey.setDead();
                    foodLevel += prey.getFoodValue();
                    return where;
                }
            } else if(isPlantPrey(animal)) {
                Plant prey = (Plant) animal;
                if(prey.isAlive()) { 
                    prey.setDead();
                    foodLevel += prey.getFoodValue();
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Funtion dedicated to identifying if an an instance of species is prey to the page animal.
     * @return isprey - Boolean type
     */
    private boolean isAnimalPrey(Object _species) 
    {
        if (_species instanceof Animal) {
            Animal animal = (Animal) _species;
            String animalID = animal.getKeyIdentifier();
            for (String preyID : preyKeyIdentifiers) {
                if (preyID.equals(animalID)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Funtion dedicated to identifying if an an instance of species is prey to the page animal.
     * @return isprey - Boolean type
     */
    private boolean isPlantPrey(Object _species) 
    {
        if (_species instanceof Plant) {
            Plant plant = (Plant) _species;
            String plantID = plant.getKeyIdentifier();
            for (String preyID : preyKeyIdentifiers) {
                if (preyID.equals(plantID)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Method which returns true if the animal has found an animal of the same species
     * and the animal is opposite gender.
     */
    
    private boolean foundMatch(Object _species)
    {
        if(_species instanceof Animal)
        {
            Animal animal = (Animal) _species;
            if(animal.getKeyIdentifier().equals(keyIdentifier))
            {
                if(animal.getGenderIsMale() != genderIsMale)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            
            else
            {
                return false;
            }
        }
        return false;
    }
    
    /**
     * A method which checks all adjecent spaces for a mate,
     * returns true if one is found and false if one is not found
     */
    private boolean foundMate()
    {
        Field field = getField();
        List<Location> adjacent = field.fiveAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(foundMatch(animal)) 
            {
                return (true);
            } 
        }
        return false;
    }

    
    /**
     * Function to check and birth animals adjascent to this animal.
     * @param newAnimals - List type of Species to append new animals to.
     */
    private void giveBirth(List<Species> newAnimals)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        if(foundMate())
        {
            for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            
            createChild(newAnimals, false, field, loc);
            }
        }
    }
    
    /**
     * Override function utilised to create child instances of subclass and append to Species.
     * @param newAnimals - List type species for appending new animals to
     * @param randomage - Boolean type depicting random age preconfiguration
     * @param field - Field type depicting field animal is on.
     * @param location - Location type depicting location on field of animal.
     */
    public void createChild(List<Species> newAnimals, boolean randomAge, Field field, Location location)
    {
        //newAnimals.add();
        ;    
    }
        
    /**
     * Generate random number clamped between zero and max litter.
     * @return births - Int type of number of births.
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
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
}
