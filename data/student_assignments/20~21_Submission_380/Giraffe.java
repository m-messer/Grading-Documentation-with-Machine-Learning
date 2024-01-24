import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * A model of a Giraffe.
 *
 * 
 */
public class Giraffe extends Animal
{
    // Characteristics shared by all giraffes (class variables).

    // The age at which a giraffe can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a giraffe can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a giraffe breeding.
    private static final double BREEDING_PROBABILITY = 0.14;
    
    private static final double DISEASE_PROBABILITY = 0.01;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    private boolean isMale;
    private boolean hasDisease;
    
    private static final double MALE_CREATION_PROBABILITY = 0.5;
    // Individual characteristics (instance fields).
    
    // The giraffe's age.
    private int age;
    
    private int foodLevel;

    /**
     * Create a new giraffe. A giraffe may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the giraffe will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Giraffe(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        isMale = rand.nextDouble() <= MALE_CREATION_PROBABILITY;
        hasDisease = rand.nextDouble() <= DISEASE_PROBABILITY;
    }
    
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> animalsNearby = field.getAnimalLocation(getLocation());
        for(Location where : animalsNearby) {
            Object animal = field.getObjectAt(where);
            if(animal instanceof Giraffe){
                Giraffe giraffe = (Giraffe) animal;
                if(giraffe.isAlive())
                    giraffe.setDisease();
            }
        }
    }

    public void setDisease()
    {
        hasDisease = true;
        if(age <= MAX_AGE - 10)
            age = MAX_AGE -10;
        else setDead();
    }
    
    /**
     * This is what the giraffe does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newGiraffes A list to return newly born giraffes.
     */
    public void act(List <Animal> newGiraffes)
    {
        incrementAge();
        if(isAlive() && !hasDisease) {
            giveBirth(newGiraffes);            
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
        else if(isAlive() && hasDisease)
            spreadDisease();
    }
    /**
     * Giraffes sleep during the night.
     */
    public void nightAct(List <Animal> newGiraffes)
    {
     
    }
    /**
     * Giraffes can't move when it rains.
     */
    public void rainAct(List <Animal> newGiraffes)
    {
        incrementAge();
        if(isAlive() && !hasDisease) {
            giveBirth(newGiraffes);            
            
        }
        else if(isAlive() && hasDisease)
            spreadDisease();
    }
    
    /**
     * Giraffes can't die when it's foggy.
     */
    public void fogAct(List <Animal> newGiraffes)
    {
        if(isAlive() && !isMale && !hasDisease) {
            giveBirth(newGiraffes); 
            
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
        else if(isAlive() & hasDisease)
        {
           spreadDisease();
        }
    }
    
    /**
     * Giraffes can't give birth when there is a storm.
     */
    public void stormAct(List <Animal> newGiraffes)
    {
        
        if(isAlive() && !hasDisease) {
            
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
        else if(isAlive() && hasDisease)
            spreadDisease();
    }
    
    /**
     * Increase the age.
     * This could result in the giraffe's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this giraffe is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGiraffes A list to return newly born giraffes.
     */
    private void giveBirth(List<Animal> newGiraffes)
    {
        // New giraffes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Giraffe young = new Giraffe(false, field, loc);
            newGiraffes.add(young);
        }
    }
        
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Plant) {
                 Plant plant = (Plant) animal;
                if(plant.isAlive()) { 
                    plant.setDead();
                    foodLevel = 4;
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
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A giraffe can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        if(age >= BREEDING_AGE)
        {
            Field field = getField();
            List<Location> matingPartners = field.getAnimalLocation(getLocation());
            for(Location where : matingPartners) {
                Object animal = field.getObjectAt(where);
               
                if(animal instanceof Giraffe){
                    Giraffe giraffe = (Giraffe) animal;
                    if(giraffe.isMale() && giraffe.getAge() >= BREEDING_AGE)
                    return true;
                }
                    else return false;                                          
            }
        }
        return false;
    }
    
    private boolean isMale()
    {
      return isMale;
    } 
    
    private int getAge()
    {
      return age;
    }
}
