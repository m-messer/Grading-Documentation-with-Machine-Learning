import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a cockatoo.
 * Foxes age, move, eat, and die.
 *
 * @version 2016.02.29 (2)
 */
    public class Cockatoo extends Animal
   {
    // Characteristics shared by all Cockatoos (class variables).
    
    // The age at which a cockatoo can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a cockatoo can live.
    private static final int MAX_AGE = 120;
    // The likelihood of a cockatoo breeding.
    private static final double BREEDING_PROBABILITY = 0.4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 50;
    // The food value of a single cockatoo. In effect, this is the
    // number of steps a cockatoo  can go before it has to eat again.
    private static final int MOLLY_FOOD_VALUE = 8;
    // The food value of a single cockatoo. In effect, this is the
    // number of steps a cockatoo  can go before it has to eat again.
    private static final int PIRANHA_FOOD_VALUE =6;
    // the likehood of diesase transmission rate.
     private static final double DISEASED_CHANCE = 0.02;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The cockatoo's age.
    private int age;
    // The cockatoo's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a cockatoo. A piranha can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the cockatoo will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cockatoo(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) 
        {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MOLLY_FOOD_VALUE + PIRANHA_FOOD_VALUE);
        }
        else 
        {
            age = 0;
            foodLevel = MOLLY_FOOD_VALUE + PIRANHA_FOOD_VALUE;       
        }
    }
    
    /**
     * This is what the cockatoo does most of the time: it hunts for
     * mollys. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newCockatoo A list to return newly born cockatoos.
     */
    public void act(List<Aquatic> newCockatoos)
    {
        incrementAge();
        
        incrementHunger();
        if(isAlive()) 
        {
            diseaseCheck();
            giveBirth(newCockatoos);            
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
     * Make this Cockatoo more hungry. This could result in the Cockatoo's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) 
        {
            setDead();
             
        }
    }
    
    /**
     * Look for cockatoos adjacent to the current location.
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
            else if(animal instanceof Piranha)
            {
                Piranha piranha = (Piranha) animal;
                if(piranha.isAlive()) 
                { 
                 piranha.setDead();
                 foodLevel = PIRANHA_FOOD_VALUE;
                 return where;
                }  
             }
        }
        return null;
    }
    
    /**
     * Check whether or not this cockatoo is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCockatoo A list to return newly born cockatoos.
     */
    private void giveBirth(List<Aquatic> newCockatoo)
    {
        if(getGender()=='F')
        {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            Cockatoo young = new Cockatoo(false, field, loc);
            
            newCockatoo.add(young);
        }
    }
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
     * Returns the max age .
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        double breeding = BREEDING_PROBABILITY;
        if(isDiseased()){
            breeding=0;
        }
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) 
        {
          births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * A cockatoo can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
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
                if(animal instanceof Cockatoo && this.getGender()!= animal.getGender())
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
        return new Cockatoo(false, getField(), loc);
    }
    /**
     * A cockatoo can get ill with a cockatoo virus. 
     */
    public void diseaseCheck()
    {
    	Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            // Check if one of the adjacent locations has a cockatoo
            if(animal instanceof Cockatoo) {
                Cockatoo cockatoo = (Cockatoo) animal;
                // Infect cockatoo if other cockatoo is infected
                if(cockatoo.isDiseased()) {
                	if(diseased == false){
                		setDiseased(rand.nextDouble() <= DISEASED_CHANCE);
                	}
                }
            }
        }
    }
}
