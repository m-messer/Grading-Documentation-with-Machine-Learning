import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * A lion model.
 *
 * 
 */
public class Lion extends Animal
{
    // Characteristics shared by all lions (class variables).
    
    // The age at which a lion can start to breed.
    private static final int BREEDING_AGE = 8;
    // The age to which a lion can live.
    private static final int MAX_AGE = 85;
    // The likelihood of a lion breeding.
    private static final double BREEDING_PROBABILITY = 0.55;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single prey. In effect, this is the
    // number of steps a lion can go before it has to eat again.
    private static final int FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    private boolean isMale;
    private boolean hasDisease;
    
    private static final double MALE_CREATION_PROBABILITY = 0.5;
    
    private static final double DISEASE_PROBABILITY = 0.01;
    // Individual characteristics (instance fields).
    // The lion's age.
    private int age;
    // The lion's food level, which is increased by eating preys.
    private int foodLevel;

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FOOD_VALUE;
        }
        isMale = rand.nextDouble() <= MALE_CREATION_PROBABILITY;
        hasDisease = rand.nextDouble() <= DISEASE_PROBABILITY;
    }
    
    /**
     * This is what the lion does most of the time: it hunts for
     * preys. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born lions.
     */
    public void act(List <Animal> newLions)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newLions);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * Lions can't die during the night.
     */
    public void nightAct(List <Animal> newLions)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newLions);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * Lions behave normally when its rainy.
     */
        public void rainAct(List <Animal> newLions)
    {
        act(newLions);
    }
    
    /**
     * Lions don't move when its foggy.
     */
    public void fogAct(List <Animal> newLions)
    {
        
        if(isAlive() && !isMale && !hasDisease) {
            giveBirth(newLions);                 
        }
        else if(isAlive() & hasDisease)
        {
           spreadDisease();
        }
    }
    
    /**
     * Lions can't move when its stormy, but they can still breed.
     */
    public void stormAct(List <Animal> newLions)
    {
        incrementHunger();
        incrementAge();
        if(isAlive() && !hasDisease) 
        {
            giveBirth(newLions);
        }
        else if(isAlive() && hasDisease)
            spreadDisease();
    }
    
    /**
     * If a lion is sick, it can spread the disease.
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> animalsNearby = field.getAnimalLocation(getLocation());
        for(Location where : animalsNearby) {
            Object animal = field.getObjectAt(where);
            if(animal instanceof Lion){
                Lion lion = (Lion) animal;
                if(lion.isAlive())
                    lion.setDisease();
            }
        }
    }
    
    /**
     * Lions have 10 steps to live when sick.
     */
    public void setDisease()
    {
        hasDisease = true;
        if(age <= MAX_AGE - 10)
            age = MAX_AGE -10;
        else setDead();
    }
    
    /**
     * Increase the age. This could result in the lion's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this lion more hungry. This could result in the lion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for preys adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Giraffe) {
                 Giraffe giraffe = (Giraffe) animal;
                if(giraffe.isAlive()) { 
                    giraffe.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Gazelle) {
                 Gazelle gazelle = (Gazelle) animal;
                if(gazelle.isAlive()) { 
                    gazelle.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Zebra) {
                 Zebra zebra = (Zebra) animal;
                if(zebra.isAlive()) { 
                    zebra.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
             else if(animal instanceof Plant) {
                Plant plant = (Plant) animal;
                if(plant.isAlive()) { 
                     plant.setDead();
                    foodLevel = 0;
                    return where;
                
                }
            }
          }
        return null;
       
    } 
    
    /**
     * Check whether or not this lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLions A list to return newly born lions.
     */
    private void giveBirth(List<Animal> newLions)
    {
        // New lions are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lion young = new Lion(false, field, loc);
            newLions.add(young);
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
     * A lion can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        if(age >= BREEDING_AGE)
        {
            Field field = getField();
            List<Location> matingPartners = field.getAnimalLocation(getLocation());
            for(Location where : matingPartners) {
                Object animal = field.getObjectAt(where);
               
                if(animal instanceof Lion){
                    Lion lion = (Lion) animal;
                    if(lion.isMale() && lion.getAge() >= BREEDING_AGE)
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
