import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a locust.
 * Locusts age, move, breed, and die.
 *
 * @version 2020.2.22
 */
public class Locust extends Animal
{
    // Characteristics shared by all locusts (class variables).

    // The age at which a locust can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a locust can live.
    private static final int MAX_AGE = 45;
    // The likelihood of a locust breeding.
    private static final double BREEDING_PROBABILITY = 0.75;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single rice. In effect, this is the
    // number of steps a locust can go before it has to eat again.
    private static final int RICE_FOOD_VALUE = 4;
    
    // Individual characteristics (instance fields).
    
    // The locust's age.
    private int age;
    private int foodLevel;

    /**
     * Create a new locust. A locust may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the locust will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Locust(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RICE_FOOD_VALUE);
        }else {
            age = 0;
            foodLevel = RICE_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the locust does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newLocusts A list to return newly born locusts.
     */
    public void act(List<Animal> newLocusts)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if(getSex()){
               giveBirth(newLocusts); 
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
     * Increase the age.
     * This could result in the locust's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this locust more hungry. This could result in the locust's death.
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
            Object plant = field.getObjectAt(where);
            if(plant instanceof Rice) {
                Rice rice = (Rice) plant;
                if(rice.isAlive()) { 
                    rice.setDead();
                    foodLevel = RICE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this locust is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLocusts A list to return newly born locusts.
     */
    private void giveBirth(List<Animal> newLocusts)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Locust) { 
               Locust locust1 = (Locust) animal;
               if(locust1.getSex() != this.getSex() ){
                 List<Location> free = field.getFreeAdjacentLocationsAnimal(getLocation());
                 int births = breed();
                 for(int b = 0; b < births && free.size() > 0; b++) {
                  Location loc = free.remove(0);
                  Locust young = new Locust(false, field, loc);
                  newLocusts.add(young);
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
     * A locust can breed if it has reached the breeding age.
     * @return true if the locust can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}