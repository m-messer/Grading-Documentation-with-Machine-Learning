import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a snake.
 * snakes age, move, eat frogs, and die.
 *
 * @version 2020.2.22
 */
public class Snake extends Animal
{
    // Characteristics shared by all snakes (class variables).
    
    // The age at which a snake can start to breed.
    private static final int BREEDING_AGE = 7;
    // The age to which a snake can live.
    private static final int MAX_AGE = 60;
    // The likelihood of a snake breeding.
    private static final double BREEDING_PROBABILITY = 0.50;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single vole. In effect, this is the
    // number of steps a snake can go before it has to eat again.
    private static final int VOLE_FOOD_VALUE = 7;
    
    private static final int FROG_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The snake's age.
    private int age;
    // The snake's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a snake. A snake can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(VOLE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = VOLE_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the snake does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSnakes A list to return newly born snakes.
     */
    public void act(List<Animal> newSnakes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if(getSex()){
            giveBirth(newSnakes);  
           }
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocationAnimal(getLocation());
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
     * Increase the age. This could result in the snake's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this snake more hungry. This could result in the snake's death.
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
            if(animal instanceof Vole) {
                Vole vole = (Vole) animal;
                if(vole.isAlive()) { 
                    vole.setDead();
                    foodLevel = VOLE_FOOD_VALUE;
                    return where;
                }
            }else if(animal instanceof Frog) {
                Frog frog = (Frog) animal;
                if(frog.isAlive()) { 
                    frog.setDead();
                    foodLevel = FROG_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this snake is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSnakes A list to return newly born snakes.
     */
    private void giveBirth(List<Animal> newSnakes)
    {
        // New snakes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Snake) { 
               Snake snake1 = (Snake) animal;
               if(snake1.getSex() != this.getSex() ){
                List<Location> free = field.getFreeAdjacentLocationsAnimal(getLocation());
                int births = breed();
               for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Snake young = new Snake(false, field, loc);
                newSnakes.add(young);
               }
               }
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
     * A snake can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
}

