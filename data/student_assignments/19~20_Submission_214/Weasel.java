import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a weasel.
 * Weasels age, move, eat rabbits, and die.
 *
 * @version 2020.2.22
 */
public class Weasel extends Animal
{
    // Characteristics shared by all weasels (class variables).
    
    // The age at which a weasel can start to breed.
    private static final int BREEDING_AGE = 45;
    // The age to which a weasel can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a weasel breeding.
    private static final double BREEDING_PROBABILITY = 0.60;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single vole. In effect, this is the
    // number of steps a weasel can go before it has to eat again.
    private static final int VOLE_FOOD_VALUE = 60;
    
    private static final int SNAKE_FOOD_VALUE = 70;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    private static final double ILLNESS_PROBABILITY = 0.10;
    //The probability of rabbit die of sick.
    private static final double ILLNESS_DEATH_PROBABILITY = 0.60;
    //The probability of rabbit cure from sick.
    private static final double ILLNESS_CURE_PROBABILITY = 0.60;
    
    
    // Individual characteristics (instance fields).
    // The weasel's age.
    private int age;
    // The weasel's food level, which is increased by eating rabbits.
    private int foodLevel;
    
    private boolean canSick = true;

    /**
     * Create a weasel. A weasel can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the weasel will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Weasel(boolean randomAge, Field field, Location location)
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
     * This is what the weasel does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWeasels A list to return newly born weasels.
     */
    public void act(List<Animal> newWeasels)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if(getSex()){
            giveBirth(newWeasels);  
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
             if(isAlive()){
              if(canSick && rand.nextDouble() <= ILLNESS_PROBABILITY){
                getSick();
                antiBody();
              }
              if(getAnimalState()){
                sick();
              }
        }
        }
    }

    /**
     * Increase the age. This could result in the weasel's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this weasel more hungry. This could result in the weasel's death.
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
            if(animal instanceof Snake) {
                Snake snake = (Snake) animal;
                if(snake.isAlive()) { 
                    snake.setDead();
                    foodLevel = SNAKE_FOOD_VALUE;
                    return where;
                }
            }else if(animal instanceof Vole) {
                Vole vole = (Vole) animal;
                if(vole.isAlive()) { 
                    vole.setDead();
                    foodLevel = VOLE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this weasel is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWeasels A list to return newly born weasels.
     */
    private void giveBirth(List<Animal> newWeasels)
    {
        // New weasels are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Weasel) { 
               Weasel weasel1 = (Weasel) animal;
               if(weasel1.getSex() != this.getSex() ){
                List<Location> free = field.getFreeAdjacentLocationsAnimal(getLocation());
                int births = breed();
               for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Weasel young = new Weasel(false, field, loc);
                newWeasels.add(young);
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
     * A weasel can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * The sick preformence of rabbit. it will spread of disease and
     * determine if rabbits will die from disease or heal themselves
     */
    private void sick()
    {
       spread();
       if(rand.nextDouble() <= ILLNESS_DEATH_PROBABILITY ) {
            setDead();
        }else if(rand.nextDouble() <= ILLNESS_CURE_PROBABILITY){
            getCure();
        }
    }
    
    /**
     * Transmit disease to surrounding rabbits.
     */
    private void spread()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Vole) { 
               Vole vole = (Vole) animal;
               if(vole.getCanSick()){
                  vole.getSick();
                  vole.antiBody();
                 }
            }
        }
    }
        
     /**
     * A rabbit can sick if it don't have antibodies
     * @return true if the rabbit can sick, false otherwise.
     */
    public boolean getCanSick()
    {
        return canSick;
    }
    /**
     * Creat an antibody, if the rabbit sick once, it will never sick twice.
     */
    public void antiBody()
    {
        canSick = false;
    }
}