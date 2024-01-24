import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a shrimp.
 * Shrimps age, move, breed, and die.
 *
 * @version 21.02.2020 (2)
 */
public class Shrimp extends Animal
{
    // Characteristics shared by all shrimps (class variables).

    // The age at which a shrimp can start to breed.
    private static final int BREEDING_AGE = 5;
    
    // The age to which a shrimp can live.
    private static final int MAX_AGE = 10;
    
    // The likelihood of a shrimp breeding.
    private static final double BREEDING_PROBABILITY = 0.25;
    
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    //distinguishes between male and female
    private boolean isMale;
    
    // The shrimp's age.
    private int age;

    /**
     * Create a new shrimp. A shrimp may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the shrimp will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param rd if true, the shrimp will be male
     */
    public Shrimp(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        Random rd = new Random();    // creating Random object
        isMale = rd.nextBoolean();  // distingushing between male and female
    }
    
    /**
     * This is what the shrimp does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newShrimps A list to return newly born shrimps.
     */
    public void act(List<Animal> newShrimps)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newShrimps);            
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
     * Increase the age.
     * This could result in the shrimp's death.
     */
    private void incrementAge()
    {
        age++; //increment the age 
        //if the current age was grater than max age 
        if(age > MAX_AGE) {
            setDead(); //the shrimp will be dead
        }
    }
    
    /**
     * Check whether or not this shrimp is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newShrimps A list to return newly born shrimps.
     */
    private void giveBirth(List<Animal> newShrimps)
    {
        // New shrimps are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shrimp young = new Shrimp(false, field, loc);
            newShrimps.add(young);
        }
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
     * A shrimp can breed if it has reached the breeding age.
     * @return true if the shrimp can breed, false otherwise.
     * a shrimp can only breed if it is next to its opposite gender
     */
    private boolean canBreed()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            // checks for opposite gender
            for (int i =0; i>= field.adjacentLocations(getLocation()).size(); i++)
            {
                //if the adjacent animal is a male shrimp 
                if(animal instanceof Shrimp && isMale)
                {
                    //if the shrimp is female and is old enough to breed
                    if (!isMale && age >= BREEDING_AGE)
                    {
                        return true; //it can breed
                    }
                }
                
                //if the adjacent animal is a female shrimp 
                if(animal instanceof Shrimp && !isMale)
                {
                    //if the shrimp is male and is old enough to breed
                    if (isMale && age >= BREEDING_AGE)
                    {
                        return true; //it can breed
                    }
                }
            }
        }
        return age >= BREEDING_AGE;
    }
}