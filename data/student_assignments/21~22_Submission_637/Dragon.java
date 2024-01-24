import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a dragon.
 * dragons age, move, eat everything , and die.
 *
 * @version 2022.03.02
 */
public class Dragon extends Animal
{
    // Characteristics shared by all bears (class variables).
    
    // The age at which a bear can start to breed.
    private static final int BREEDING_AGE = 15;

    // The likelihood of a bear breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The food value of a single velociraptor.
    private static final int VELOCIRAPTOR_FOOD_VALUE = 100;
    //The food value of a single bear.
    private static final int BEAR_FOOD_VALUE = 100;
    // The food value of a single monkey.
    private static final int Monkey_FOOD_VALUE = 100;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The dragon's age.
    private int age;
    // The dragon's food level, which is increased by eating rabbits.
    private int foodLevel;
    private int MAX_AGE = 4000;
    protected List<Disease> diseases;

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the dragon will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dragon(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(VELOCIRAPTOR_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = VELOCIRAPTOR_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the bear does most of the time: it hunts for
     * dragons. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newBears A list to return newly born dragons.
     */
    public void actDay(List<Animal> newBears,List<Disease> diseases)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newBears);     
            
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
    
    public void actNight(List<Animal> newBears, List<Disease> diseases)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newBears);  
           
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
     * Increase the age. This could result in the dragon's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this dragon more hungry. This could result in the dragon's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
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
            if(animal instanceof Velociraptor) {
                Velociraptor velociraptor = (Velociraptor) animal;
                if (velociraptor.isAlive()) {
                    velociraptor.setDead();
                    foodLevel = VELOCIRAPTOR_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Monkey) {
                Monkey monkey = (Monkey) animal;
                if (monkey.isAlive()) {
                    monkey.setDead();
                    foodLevel = Monkey_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Bear) {
                Bear bear = (Bear) animal;
                if (bear.isAlive()) {
                    bear.setDead();
                    foodLevel = BEAR_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Looks for fellow dragons of the opposite sex in adjacent locations.
     * @returns True if a male bear is found and this bear is female otherwise False.
     */
    private boolean findMate()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Dragon) {
                Dragon bear = (Dragon) animal;
                // this only works if current instance of bear is female as the simulation
                // will also eventually loop through and do this same method on the adjacent bear
                // this prevents the pair of bears from breeding twice in one step.
                if(this.isFemale() && !(bear.isFemale())) { 
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check whether or not this dragon is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born dragons.
     */
    private void giveBirth(List<Animal> newFoxes)
    {
        // New dragons are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        if (findMate()){
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Dragon young = new Dragon(false, field, loc);
                newFoxes.add(young);
            }
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
     * A dragon can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
