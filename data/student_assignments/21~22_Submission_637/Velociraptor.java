import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a velociraptor.
 * Velociraptors age, move, eat rabbits, and die.
 *
 * @version 2022.03.02
 */
public class Velociraptor extends Animal
{
    // Characteristics shared by all velociraptores (class variables).
    
    // The age at which a velociraptor can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a velociraptor can live.
    
    // The likelihood of a velociraptor breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a velociraptor can go before it has to eat again.
    private static final int MONKEY_FOOD_VALUE = 15;
    
    private static final int WORM_FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The velociraptor's age.
    private int age;
    // The velociraptor's food level, which is increased by eating rabbits.
    private int foodLevel;
    private int MAX_AGE = 200;
    protected List<Disease> diseases;
    /**
     * Create a velociraptor. A velociraptor can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the velociraptor will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Velociraptor(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MONKEY_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = MONKEY_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the velociraptor does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newVelociraptors A list to return newly born velociraptores.
     */
    public void actDay(List<Animal> newVelociraptors,List<Disease> diseases)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newVelociraptors);   
            
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
    
     public void actNight(List<Animal> newVelociraptores, List<Disease> diseases)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            for (int i = 0; i < 2; i++) {
                giveBirth(newVelociraptores);
                
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
                    return;
                }
            }
        }
    }
    
  
    
    /**
     * Increase the age. This could result in the velociraptor's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this velociraptor more hungry. This could result in the velociraptor's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for Monkeys and Worms adjacent to the current location
     * Only the first live animal nearest is eaten
     * @return where food was found, or null if it wasnt
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Monkey) {
                Monkey monkey = (Monkey) animal;
                if(monkey.isAlive()) { 
                    monkey.setDead();
                    foodLevel = MONKEY_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Worm) {
                Worm worm = (Worm) animal;
                if(worm.isAlive()) { 
                    worm.setDead();
                    foodLevel = WORM_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Looks for fellow velociraptores of the opposite sex in adjacent locations.
     * @returns True if a velociraptor of the opposite gender is found otherwise False.
     */
    private boolean findMate()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Velociraptor) {
                Velociraptor velociraptor = (Velociraptor) animal;
                if(this.isFemale() != velociraptor.isFemale()){ 
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check whether or not this velociraptor is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newVelociraptors A list to return newly born velociraptores.
     */
    private void giveBirth(List<Animal> newVelociraptors)
    {
        // New velociraptores are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        //if (findMate()){
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Velociraptor young = new Velociraptor(false, field, loc);
                newVelociraptors.add(young);
            }
        //}
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
     * A velociraptor can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
