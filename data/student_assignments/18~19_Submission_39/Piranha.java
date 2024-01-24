import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a piranha.
 * Piranhas age, move, eat and die.
 *
 * @version 2016.02.29 (2)
 */
    public class Piranha extends Animal
   {
    // Characteristics shared by all piranhas (class variables).
    
    // The age at which a piranha can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a piranha can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a piranha breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 30;
    // The food value of a single piranha. In effect, this is the
    // number of steps a piranha can go before it has to eat again.
    private static final int MOLLY_FOOD_VALUE = 8;
    // The food value of a single  piranha. In effect, this is the
    // number of steps a piranha can go before it has to eat again.
    private static final int COCKATOO_FOOD_VALUE = 8;
    // The food value of a single  piranha. In effect, this is the
    // number of steps a piranha can go before it has to eat again.
    private static final int FARLOWELLA_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The  piranha's age.
    private int age;
    // The  piranha's food level, which is increased by eating.
    private int foodLevel;

    /**
     * Create a piranha. A piranha can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the piranha will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Piranha(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) 
        {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MOLLY_FOOD_VALUE +COCKATOO_FOOD_VALUE +FARLOWELLA_FOOD_VALUE);
        }
        else 
        {
            age = 0;
            foodLevel = MOLLY_FOOD_VALUE + COCKATOO_FOOD_VALUE + FARLOWELLA_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the piranha does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newPiranha A list to return newly born piranhas.
     */
    public void act(List<Aquatic> newPiranhas)
    {
        incrementAge(); 
        incrementHunger();
        if(isAlive()) 
        {
            giveBirth(newPiranhas);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) 
            { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * Returns the max age .
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Make this  piranha more hungry. This could result in the  piranha's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) 
        {
            setDead();
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
            Object animal = field.getObjectAt(where);
            if(animal instanceof Molly) 
            {
                Molly molly = (Molly) animal;
                if(molly.isAlive()) 
                { 
                    molly.setDead();
                    foodLevel = MOLLY_FOOD_VALUE; 
                    return where;
                }
            }
            else if(animal instanceof Cockatoo)
            {
                Cockatoo cockatoo = (Cockatoo) animal;
                if(cockatoo.isAlive()) 
                { 
                    cockatoo.setDead();
                    foodLevel = COCKATOO_FOOD_VALUE;  
                    return where;
                } 
            }   
            else if(animal instanceof Farlowella)
                {
                 Farlowella farlowella = (Farlowella) animal;
                if(farlowella.isAlive()) 
                { 
                    farlowella.setDead();
                    foodLevel = FARLOWELLA_FOOD_VALUE;  return where;
                }  
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this piranha is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    private void giveBirth(List<Aquatic> newPiranha)
    {
        if(getGender()=='F')
        {
        // New  piranhas are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            Piranha young = new Piranha(false, field, loc);
            newPiranha.add(young);
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
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) 
        {
           births = rand.nextInt(getMaxLitterSize()) + 1;
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
     * A  piranha can breed if it has reached the breeding age.
     */
    public boolean canBreed()
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
                if(animal instanceof Piranha && this.getGender()!= animal.getGender())
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
        return new Piranha(false, getField(), loc);
    }
}
