import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a squirrel.
 * salmons age, move, breed, and die.
 *
 * @version 20/02/2019
 */
public class Salmon extends WaterAnimal
{
    // Characteristics shared by all salmons (class variables).

    // The age at which a salmon can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a salmon can live.
    private static final int MAX_AGE = 12;
    // The likelihood of a salmon breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The maximum amount of food it can eat.
    private static final int FOOD_LIMIT = 20;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The salmon's food level, which is increased by eating plants.
    private int foodLevel;
    // The salmon's age.
    private int age;

    /**
     * Create a new salmon. A salmon may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the salmon will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Salmon(Field field, Location location, boolean isMale, boolean randomAge)
    {
        super(field, location, isMale);
        age = 0;
        foodLevel = FOOD_LIMIT;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the salmon does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newsalmons A list to return newly born salmons.
     */
    public void act(List<Animal> newsalmons)
    {
        incrementAge();
        incrementHunger();
        // Snow affects animals' movements.
        if(isAlive() && rand.nextDouble() >= 0.25*field.getWeather(location.getRow(), location.getCol(), 2)) {
            //if the salmon is healthy, randomly try to make it sick
            if (isHealthy())
            {
             Random rand = new Random();
             boolean randomSick = (rand.nextDouble() <= ANIMAL_GET_SICK_PROBABILITY ) ? true : false;
            if(randomSick)
            {//make the salmon sick
             makeSick();
             //make its age to be 90% old
             age = MAX_AGE - MAX_AGE/10;
            }
            }
            //if it is unhealthy, try to spread the disease
            if (!isHealthy())
            {
                spreadDisease();
            }
            //give birth to new salmons
            giveBirth(newsalmons);            
            // Move towards a source of food if found.
            Location newLocation = null;
            //the stronger the fog, the less chance for the animal to find food
            if (rand.nextDouble() >= 0.25*field.getWeather(location.getRow(), location.getCol(), 0))
                newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = field.freeAdjacentLocation(getSuitableLocations(getLocation()));
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
     * Spread the disease between two salmons
     */
    public void spreadDisease()
    {
     Field field = getField();
    //get the adjacent locations
     List<Location> adjacent = field.adjacentLocations(getLocation());
     Iterator<Location> iterator = adjacent.iterator();
     //parse all the adjacent locations to try to spread the disease
     while (iterator.hasNext()) {
         Location nearby = iterator.next();
         Object animal = field.getObjectAt(nearby);
         //check if the animal found in the current adjacent loc is a salmon
         if (animal instanceof Salmon) {
             Salmon salmon = (Salmon) animal;
             //check if the found salmon is healthy
             if (salmon.isHealthy()) {
                //if the found salmon is healthy, try to spread the disease
                Random rand = new Random ();
                boolean randomSpread = (rand.nextDouble() <= DISEASE_SPREAD_PROBABILITY ) ? true : false;
                if (randomSpread){
                    salmon.makeSick();
                    salmon.age = MAX_AGE - MAX_AGE/10;
                }    
            }    
        }     
    }        
    }
    
    /**
     * Look for salmons adjacent to the current location.
     * Only the first live salmon is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if(object instanceof Plant) {
                Plant plant = (Plant) object;
                if(plant.isAlive() && foodLevel < FOOD_LIMIT && rand.nextDouble() > 0.875) { 
                    foodLevel += plant.getFoodValue();
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Increase the age.
     * This could result in the salmon's death.
     */
    private void incrementAge()
    {
        age++;
        //when the animal reaches the maximum age, it dies instantly
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this salmon is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newsalmons A list to return newly born salmons.
     */
    private void giveBirth(List<Animal> newsalmons)
    {
        // New salmons are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        
        List<Location> locations = field.adjacentLocations(location);
        for (Location loc : locations){
            Object obj = field.getObjectAt(loc.getRow(), loc.getCol());
            if (obj != null && obj.getClass() == this.getClass())
            {
                if (((Salmon)obj).getGender() != isMale)
                {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    boolean randomGender = (rand.nextDouble()>0.7) ? true : false;
                    int births = breed(obj);
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location l = free.remove(0);
                        Salmon young = new Salmon(field, l, randomGender, false);
                        newsalmons.add(young);
                    }
                    break;
                }
            }
        }
    }
    
    /**
     * Make this salmon more hungry. This could result in the salmon's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    /*
     * Return whether the animal is a male or not
     */
    protected boolean getGender()
    {
        return isMale;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed(Object obj)
    {
        int births = 0;
        if(((Salmon)obj).canBreed() && canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * A salmon can breed if it has reached the breeding age.
     * @return true if the salmon can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
