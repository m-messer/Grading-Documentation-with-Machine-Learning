 import java.util.List;
 import java.util.Random;
 import java.util.Iterator;
   /**
    * Write a description of class Moss here.
    *
    * @version (a version number or a date)
    */ 
    public class Moss extends Plant
  {
    // The age at which a moss can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a moss can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a moss breeding.
    private static final double BREEDING_PROBABILITY = 0.67;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 40;
    // A shared random number generator to control breeding.
    private Random rand = Randomizer.getRandom();
    
    private int age;
    /**
     * Create a new moss. A moss may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the moss will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Moss(boolean randomAge, Field field, Location location)
    {
        super(field, location);
    }
    
    public void act(List<Aquatic> newMoss)
    {
        incrementAge();
        if(isAlive()) 
        {
        proliferationOfPlant(newMoss);            
        // Try to move into a free location.
        Location newLocation = getField().freeAdjacentLocation(getLocation());
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
     * Check whether or not this  moss is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newMoss A list to return newly born moss.
     */
    private void proliferationOfPlant(List<Aquatic> newMoss)
    {
        // New grass are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int proliferation = breed();
        for(int b = 0; b < proliferation && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            Moss young = new Moss(false, field, loc);
            newMoss.add(young);
        }
    }
    
    /**
     * Returns the max age of moss.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
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
     * Returns ne mosses.
     */
    protected Plant getYoung(Location loc)
    {
        return new Moss(false, getField(), loc);
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
     * A moss can breed if it has reached the breeding age.
     * @return true if the farlowella can breed, false otherwise.
     */
    protected  boolean canBreed()
    {
       return age >= BREEDING_AGE;
    }
  }

