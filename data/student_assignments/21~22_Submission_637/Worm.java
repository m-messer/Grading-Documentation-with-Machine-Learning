import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a worm.
 * Worms age, move, eat mushrooms, and die.
 *
 * @version 2022.03.02
 */
public class Worm extends Animal
{
    // Characteristics shared by all worms (class variables).
    
    // The age at which a worm can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a worm can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a worm breeding.
    private static final double BREEDING_PROBABILITY = 0.9;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single mushroom. In effect, this is the
    // number of steps a Worm can go before it has to eat again.
    private static final int PSILOCYBINMUSHROOM_FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The worm's age.
    private int age;
    // The worm's food level, which is increased by eating mushrooms.
    private int foodLevel;

    /**
     * Create a worm. A worm can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the worm will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Worm(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PSILOCYBINMUSHROOM_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = PSILOCYBINMUSHROOM_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the worm does most of the time: it hunts for
     * mushrooms. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWorms A list to return newly born worms.
     */
    public void actDay(List<Animal> newWorms,List<Disease> diseases)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newWorms);            
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
    
     public void actNight(List<Animal> newWormes,List<Disease> diseases)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            for (int i = 0; i < 2; i++) {
                giveBirth(newWormes);            
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
     * Increase the age. This could result in the worm's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this worm more hungry. This could result in the worm's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for mushrooms adjacent to the current location.
     * Only the first mushroom is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof PsilocybinMushroom) {
                PsilocybinMushroom psilocybinmushroom = (PsilocybinMushroom) plant;
                if(psilocybinmushroom.isAlive()) { 
                    psilocybinmushroom.setDead();
                    foodLevel = PSILOCYBINMUSHROOM_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Looks for fellow worms of the opposite sex in adjacent locations.
     * @returns True if a worm of the opposite gender is found otherwise False.
     */
    private boolean findMate()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Worm) {
                Worm worm = (Worm) animal;
                if(this.isFemale() != worm.isFemale()){ 
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Check whether or not this worm is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWorms A list to return newly born worms.
     */
    private void giveBirth(List<Animal> newWorms)
    {
        // New worms are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        //if (findMate()){
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Worm young = new Worm(false, field, loc);
                newWorms.add(young);
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
     * A worm can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
