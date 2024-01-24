import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a penguin.
 * Penguins age, move, eat fish, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Penguin extends Animal 
{
    // Characteristics shared by all penguins (class variables).
    
    // The age at which a penguin can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a penguin can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a penguin breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single fish. In effect, this is the
    // number of steps a penguin can go before it has to eat again.
    private static final int FISH_FOOD_VALUE = 20;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Individual characteristics (instance fields).
    // The penguin's age.
    private int age;
    // The penguin's food level, which is increased by eating fish.
    private int foodLevel;
    
    private boolean female;
    /**
     * Create a penguin. A penguin can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the penguin will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Penguin (boolean randomAge, Field field, Location location, Step step)
    {
        super(field, location, step);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FISH_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FISH_FOOD_VALUE;
        }
        
        if (rand.nextDouble() <= 0.5) {
           female = true;
        }
        else {
           female = false;
        }
    }
    
    /**
     * This is what the penguin does most of the time: it hunts for
     * fish. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newPenguins A list to return newly born penguins.
     */
    public void actDay(List<Animal> newPenguins)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newPenguins);            
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
     * This is what the Penguin does most of the time at Night time: ......Not confirmed
     * In the process, it might breed, die of hunger.
     * @param field The field currently occupied.
     * @param newPenguins A list to return newly born Penguins.
     */
    public void actNight(List<Animal> newPenguins)
    {
        incrementHunger();
        if(isAlive()) {
            giveBirth(newPenguins);            
        }
    }
    
    /**
     * Increase the age. This could result in the penguin's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
   /**
     * Increase the hunger level. This could result in the polar bear's death.
     * At night,makes polar bear hungry more than Day time, 
     * as it cannot hunt for a prey at Night.
     */
    private void incrementHunger()
    {
        if(isDay()){
            foodLevel --;
        }
        else{
            foodLevel -= 2;
        }
        
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    private boolean isFemale(){
       return female;   
    }
    
    /**
     * Look for fish adjacent to the current location.
     * Only the first live fish is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        Step step = getStep();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Fish) {
                Fish fish = (Fish) animal;
                if(fish.isAlive()) { 
                    fish.setDead();
                    foodLevel = FISH_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this penguin is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPenguins A list to return newly born penguins.
     */
    private void giveBirth(List<Animal> newPenguins)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        Step step = getStep();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Penguin young = new Penguin(false, field, loc, step);
            newPenguins.add(young);
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
     * A penguin can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
        
    private boolean nextToMale()
    {
        //check through the list of adjacent locations to see if a male polarbear is
        //next to the female polarbear
        boolean found = false;
        Field field = getField();
        List<Location> locations = field.adjacentLocations(getLocation()) ;
        Iterator<Location> it = locations.iterator();
        while(it.hasNext())
        {  
          if (field.getObjectAt(getLocation()) instanceof Penguin) {
              Penguin penguin = (Penguin) field.getObjectAt(getLocation());
              if (!penguin.isFemale()) {
                 found = true;
                 return found;
            }
           }
        }
        return found;
    }
}
