import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * A model of a Gazelle.
 *
 */
public class Gazelle extends Animal
{
    // Characteristics shared by all gazelles (class variables).

    // The age at which a gazelle can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a gazelle can live.
    private static final int MAX_AGE = 39;
    // The likelihood of a gazelle breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    private boolean isMale;
    private boolean hasDisease;
    
    private static final double MALE_CREATION_PROBABILITY = 0.5;
    private static final double DISEASE_PROBABILITY = 0.01;
    // Individual characteristics (instance fields).
    
    // The gazelle's age.
    private int age;

    /**
     * Create a new gazelle. A gazelle may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
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
     * This is what the gazelle does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newGazelles A list to return newly born gazelles.
     */
    public void act(List <Animal> newGazelles)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newGazelles);            
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
     * Gazelles sleep during the night.
     */
    public void nightAct(List <Animal> newGazelles)
    {
       
    }
    
    /**
     * Gazelles behave normally when it rains.
     */
    public void rainAct(List <Animal> newGazelles)
    {
        act(newGazelles);
    }
    
    /**
     * Gazelles can't move when there is fog.
     */
    public void fogAct(List <Animal> newGazelles)
    {
        incrementAge();
        if(isAlive() && !isMale && !hasDisease) {
            giveBirth(newGazelles);                 
        }
        else if(isAlive() & hasDisease)
        {
           spreadDisease();
        }
    }
    
    /**
     * Gazelles can't breed when there is a storm
     */
    public void stormAct(List <Animal> newGazelles)
    {
        
        if(isAlive() && !hasDisease) {
                        
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
    
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> animalsNearby = field.getAnimalLocation(getLocation());
        for(Location where : animalsNearby) {
            Object animal = field.getObjectAt(where);
            if(animal instanceof Gazelle){
                Gazelle gazelle = (Gazelle) animal;
                if(gazelle.isAlive())
                    gazelle.setDisease();
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
     * This could result in the gazelle's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this gazelle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGazelles A list to return newly born gazelles.
     */
    private void giveBirth(List<Animal> newGazelles)
    {
        // New gazelles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Gazelle young = new Gazelle(false, field, loc);
            newGazelles.add(young);
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
     * A gazelle can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        if(age >= BREEDING_AGE)
        {
            Field field = getField();
            List<Location> matingPartners = field.getAnimalLocation(getLocation());
            for(Location where : matingPartners) {
                Object animal = field.getObjectAt(where);
               
                if(animal instanceof Gazelle){
                    Gazelle gazelle = (Gazelle) animal;
                    if(gazelle.isMale() && gazelle.getAge() >= BREEDING_AGE)
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
