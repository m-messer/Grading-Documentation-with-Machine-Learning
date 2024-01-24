import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a farlowella.
 * Rabbits age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
    public class Farlowella extends Animal
   {
    // Characteristics shared by all rabbits (class variables).
    // The age at which a farlowella can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a farlowella can live.
    private static final int MAX_AGE = 110;
    // The likelihood of a farlowellabreeding.
    private static final double BREEDING_PROBABILITY = 0.7;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 80;
     // The food value of a single farlowella. In effect, this is the
    // number of steps a farlowella can go before it has to eat again.
    private static final int MOSS_FOOD_VALUE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The farlowella's age.
    private int age;
    private int foodLevel;
    
    /**
     * Create a new molly. A farlowella may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the farlowella will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Farlowella(boolean randomAge, Field field, Location location)
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
     * This is what the farlowella does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newFarlowella A list to return newly born farlowellas.
     */
    public void act(List<Aquatic> newFarlowellas)
    {
        incrementAge();
         
        if(isAlive()) 
        {
            giveBirth(newFarlowellas);            
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
     * Look for foods adjacent to the current location.
     * Only the first live food is eaten.
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
            Object plant = field.getObjectAt(where);
            if(plant instanceof Moss) 
            {
                Moss moss = (Moss) plant;
                if(moss.isAlive()) 
                { 
                    moss.setDead();
                    foodLevel = MOSS_FOOD_VALUE;      return where;
                }
            }
            
        }
        return null;
    }
    
    /**
     * Check whether or not this farlowella is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFarlowellas A list to return newly born farlowellas.
     */
    private void giveBirth(List<Aquatic> newFarlowellas)
    {
        if(getGender()=='F')
        {
        // New mollys are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            Farlowella young = new Farlowella(false, field, loc);
            
            newFarlowellas.add(young);
        }
        }
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
     * Returns the probobility for breeding.
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
     * Returns the max age
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * A farlowella can breed if it has reached the breeding age.
     * @return true if the farlowella can breed, false otherwise.
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
             if(object instanceof Animal){
                Animal animal = (Animal) object;
                if(animal instanceof Farlowella && this.getGender()!= animal.getGender()){
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
        return new Farlowella(false, getField(), loc);
    }
}
