import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fish.
 * Fish age, move, breed, and die.
 *
 * @version 21.02.2020 (2)
 */
public class Fish extends Animal
{
    // Characteristics shared by all fish (class variables).

    // The age at which a fish can start to breed.
    private static final int BREEDING_AGE = 5;
    
    // The age to which a fish can live.
    private static final int MAX_AGE = 1000;
    
    // The likelihood of a fish breeding.
    private static final double BREEDING_PROBABILITY = 0.80;
    
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // The food value of a single plankton.
    private static final int PLANKTON_FOOD_VALUE = 3;
    
    // The food value of a single seaweed.
    private static final int SEAWEED_FOOD_VALUE = 2;

    // Individual characteristics (instance fields)
    
    //distinguishes between male and female
    private boolean isMale;
    
    // The fish's age.
    private int age;
    
    //Calling the Disease Class
    private Disease disease;
    
    // The fish's food level, which is increased by eating food.
    private int foodLevel;
    
    /**
     * Create a new fish. A fish may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the fish will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param rd if true, the shrimp will be male
     */
    public Fish(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANKTON_FOOD_VALUE) + rand.nextInt(SEAWEED_FOOD_VALUE);
        }
        disease = new Disease();
        Random rd = new Random(); // creating Random object
        isMale = rd.nextBoolean(); // distingushing between male and female
    }

    /**
     * This is what the fish does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newFish A list to return newly born fish.
     */
    public void act(List<Animal> newFish)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newFish);            
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Make this fish more hungry. This could result in the fish's death.
     */
    private void incrementHunger()
    {
        foodLevel--; //decreasing food level of fish by one
        //if the food level of fish is less than or equal to 0
        if(foodLevel <= 0) { 
            setDead(); //the fish will be dead
        }
    }

    /**
     * Increase the age.
     * This could result in the fish's death.
     */
    private void incrementAge()
    {
        age++; //increment the age 
        //if the current age was grater than max age 
        if(age > MAX_AGE) {
            setDead(); //the fish will be dead
        }
    }
    
    /**
     * Increase the age by 5  
     */
    private void diseaseCounter()
    {
        age += 5;
        //if the current age was grater than max age 
        if(age > MAX_AGE) {
            setDead(); //the fish will be dead
        }
    }

    /**
     * Check whether or not this fish is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFish A list to return newly born fish.
     */
    private void giveBirth(List<Animal> newFish)
    {
        // New fish are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fish young = new Fish(false, field, loc);
            newFish.add(young);
        }
    }

    /**
     * Look for fish adjacent to the current location.
     * Only the first live fish is eaten.
     * fish can only eat seaweed
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        //check the animals and plants around the fish
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);            
            SeaWeed seaWeed = (SeaWeed) plant;
            
            //if the adjacent seaweed was alive let the fish to eat it
            if(seaWeed.isAlive()) { 
                seaWeed.setDead();
                foodLevel = SEAWEED_FOOD_VALUE;
                return where;
            }
        }
        return null;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
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
     * A fish can breed if it has reached the breeding age.
     * @return true if the fish can breed, false otherwise.
     * a fish can only breed if it is next to its opposite gender
     */
    private boolean canBreed()
    {

        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            for (int i =0; i>= field.adjacentLocations(getLocation()).size(); i++)
            {
                //if the adjacent animal is a male fish 
                if(animal instanceof Fish && isMale)
                {
                    //if the fish is female and is old enough to breed
                    if (!isMale && age >= BREEDING_AGE && foodLevel>1)
                    {
                        return true;//the fish can breed
                    }
                }
                
                //if the adjacent animal is a female fish
                if(animal instanceof Fish && !isMale)
                {
                    //if the fish is male and is old enough to breed
                    if (isMale && age >= BREEDING_AGE && foodLevel>1)
                    {
                        return true; //the fish can breed
                    }
                }
            }
        }
        return age >= BREEDING_AGE;
    }
}