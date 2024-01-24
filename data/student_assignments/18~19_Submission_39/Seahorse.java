import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a seahorse.
 * Rabbits age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
    public class Seahorse extends Animal
   {
    // Characteristics shared by all seahorses (class variables).

    // The age at which a seahorse can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a seahorse can live.
    private static final int MAX_AGE = 90;
    // The likelihood of a seahorse breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 50;
    // The food value of a single seahorse . In effect, this is the
    // number of steps a seahorse  can go before it has to eat again.
    private static final int MOSS_FOOD_VALUE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The farlowella's age.
    private int age;
    private int foodLevel;
    
    /**
     * Create a new seahorse . A seahorse  may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the seahorse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Seahorse(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) 
        {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MOSS_FOOD_VALUE);
        }
        else 
        {
            age = 0;
            foodLevel = MOSS_FOOD_VALUE ;        
     }
    }
    
    /**
     * This is what the seahorse  does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newSeahorse  A list to return newly born seahorses.
     */
    public void act(List<Aquatic> newSeahorses)
    {
        incrementAge();
        if(isAlive()) 
        {
            giveBirth(newSeahorses);            
            // Try to move into a free location.
            Location newLocation = findFood();
            if(newLocation == null) 
            { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if(newLocation != null) 
            {
                setLocation(newLocation);
            }
            else 
            {
                // Overcrowding.
                setDead();
            }
        }
    }
    
    /**
     * Check whether or not this seahorse  is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeahorse  A list to return newly born seahorses.
     */
    private void giveBirth(List<Aquatic> newSeahorses)
    {
        if(getGender()=='M')
        {
        // New seahorses are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            Seahorse  young = new Seahorse (false, field, loc);
            newSeahorses.add(young);
        }
        }
    }  
    /**
     * Look for seahorses adjacent to the current location.
     * Only the first live seahorse is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) 
        {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Moss) 
            {
                Moss moss = (Moss) animal;
                if(moss.isAlive()) 
                { 
                    moss.setDead();
                    foodLevel = MOSS_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) 
        {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * Returns the max litter size.
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     *  Returns the probobility for breeding.
     */
    protected double  getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns the breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Returns the max age .
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * A seahorse can breed if it has reached the breeding age.
     * @return true if the seahorse can breed, false otherwise.
     */
    protected  boolean canBreed()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) 
        {
             Location where = it.next();
             Object object = field.getObjectAt(where);
             if(object instanceof Animal)
             {
                Animal animal = (Animal) object;
                if(animal instanceof Seahorse && this.getGender()!= animal.getGender())
                {
                     return age >= BREEDING_AGE;
                }
             }   
        }
         return false;
    }
    
    /**
     * Return the young of this animal
     * @return The young of this animal
     */
     protected Animal getYoung(Location loc)
    {
        return new Seahorse(false, getField(), loc);
    }
}
