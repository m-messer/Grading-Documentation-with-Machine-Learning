import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a zebra.
 * zebras age, eat, move, breed, and die.
 *
 *
 */
public class Zebra extends Animal
{
    // Characteristics shared by all zebras (class variables).

    // The age at which a zebra can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a zebra can live.
    private static final int MAX_AGE = 38;
    // The likelihood of a zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    private boolean isMale;
    private boolean hasDisease;
    
    private static final double MALE_CREATION_PROBABILITY = 0.5;
    
    private static final double DISEASE_PROBABILITY = 0.01;
    // Individual characteristics (instance fields).
    
    // The zebra's age.
    private int age;
    private int foodLevel;

    /**
     * Create a new zebra. A zebra may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the zebra will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        isMale = rand.nextDouble() <= MALE_CREATION_PROBABILITY;
        hasDisease = rand.nextDouble() <= DISEASE_PROBABILITY;
    }
    
    /**
     * This is what the zebra does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newzebras A list to return newly born zebras.
     */
    public void act(List<Animal> newZebras)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newZebras);            
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
     * Zebras sleep during the night.
     */
    public void nightAct(List<Animal> newZebras)
    {
       
    }
    
    /**
     * Zebras behave normally when it rains
     */
    public void rainAct(List <Animal> newZebras)
    {
        act(newZebras);
    }
    
    /**
     * Zebras can only give birth whe there is fog.
     */
    public void fogAct(List <Animal> newZebras)
    {
        incrementAge();
        if(isAlive() && !isMale && !hasDisease) {
            giveBirth(newZebras);                 
        }
        else if(isAlive() & hasDisease)
        {
           spreadDisease();
        }
    }
    
    /**
     * Zebras can't move during a storm.
     */
    public void stormAct(List <Animal> newZebras)
    {
        incrementAge();
        if(isAlive() && !hasDisease) {
            giveBirth(newZebras);            
            // Try to move into a free location.
            
        }
        else if(isAlive() && hasDisease)
            spreadDisease();
    }
    
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> animalsNearby = field.getAnimalLocation(getLocation());
        for(Location where : animalsNearby) {
            Object animal = field.getObjectAt(where);
            if(animal instanceof Zebra){
                Zebra zebra = (Zebra) animal;
                if(zebra.isAlive())
                    zebra.setDisease();
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
     * Increase the age.
     * This could result in the zebra's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this zebra is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newzebras A list to return newly born zebras.
     */
    private void giveBirth(List<Animal> newZebras)
    {
        // New zebras are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Zebra young = new Zebra(false, field, loc);
            newZebras.add(young);
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
     * A zebra can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        if(age >= BREEDING_AGE)
        {
            Field field = getField();
            List<Location> matingPartners = field.getAnimalLocation(getLocation());
            for(Location where : matingPartners) {
                Object animal = field.getObjectAt(where);
               
                if(animal instanceof Zebra){
                    Zebra zebra = (Zebra) animal;
                    if(zebra.isMale() && zebra.getAge() >= BREEDING_AGE)
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
