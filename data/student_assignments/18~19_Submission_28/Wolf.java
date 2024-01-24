import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a wolf.
 * wolfes age, move, eat squirrels, sheep, and die.
 *
 * @version 20/02/2019
 */
public class Wolf extends LandAnimal
{
    // Characteristics shared by all wolfes (class variables).
    
    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.57;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single squirrel.
    private static final int SQUIRREL_FOOD_VALUE = 5;
    // The food value of a single sheep.
    private static final int SHEEP_FOOD_VALUE = 8;
    // The maximum amount of food it can eat.
    private static final int FOOD_LIMIT = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The wolf's age.
    private int age;
    // The wolf's food level, which is increased by eating squirrels.
    private int foodLevel;

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the wolf will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Wolf(Field field, Location location, boolean isMale1, boolean randomAge)
    {
        super(field, location, isMale1);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOOD_LIMIT);
        }
        else {
            age = 0;
            foodLevel = FOOD_LIMIT;
        }
    }
    
    /**
     * This is what the wolf does most of the time: it hunts for
     * sheep. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newwolves A list to return newly born wolfes.
     */
    public void act(List<Animal> newwolves)
    {
        incrementAge();
        incrementHunger();
        // Snow affects animals' movements.
        if(isAlive() && rand.nextDouble() >= 0.25*field.getWeather(location.getRow(), location.getCol(), 2)) {
            if (isHealthy())
            {
             Random rand = new Random();
             boolean randomSick = (rand.nextDouble() <= ANIMAL_GET_SICK_PROBABILITY ) ? true : false;
            if(randomSick)
            {
             makeSick();
             age = MAX_AGE - MAX_AGE/10;
            }
            }
            if (!isHealthy())
            {
                spreadDisease();
            }
            giveBirth(newwolves);            
            // Move towards a source of food if found.
            Location newLocation = null;
            if (rand.nextDouble() >= 0.25*field.getWeather(location.getRow(), location.getCol(), 1))
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
     * Spread the disease between two wolves
     */
    public void spreadDisease()
    {
     Field field = getField();
    // get adjacent locations
     List<Location> adjacent = field.adjacentLocations(getLocation());
     Iterator<Location> iterator = adjacent.iterator();
     //parse the adjacents locations
     while (iterator.hasNext()) {
         Location nearby = iterator.next();
         Object animal = field.getObjectAt(nearby);
         //test if the nearby animal is a wolf
         if (animal instanceof Wolf) {
             Wolf wolf = (Wolf) animal;
             //if the nearby wolf is healthy, try to make it sick
             if (wolf.isHealthy()) {
                Random rand = new Random ();
                boolean randomSpread = (rand.nextDouble() <= DISEASE_SPREAD_PROBABILITY ) ? true : false;
                if (randomSpread){
                    wolf.makeSick();
                    wolf.age = MAX_AGE - MAX_AGE/10;
                }    
            }    
        }     
    }        
    }
    
    /**
     * Increase the age. This could result in the wolf's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this wolf more hungry. This could result in the wolf's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for squirrels or sheep adjacent to the current location.
     * Only the first live sheep or squirrel is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            
            if(animal instanceof Squirrel) {
                Squirrel squirrel = (Squirrel) animal;
                if(squirrel.isAlive() && foodLevel < FOOD_LIMIT && rand.nextDouble() > 0.95) { 
                    squirrel.setDead();
                    foodLevel += SQUIRREL_FOOD_VALUE;
                    return where;
                }
            }
            if(animal instanceof Sheep && foodLevel < FOOD_LIMIT && rand.nextDouble() > 0.85) {
                Sheep sheep = (Sheep) animal;
                if(sheep.isAlive()) { 
                    sheep.setDead();
                    foodLevel += SHEEP_FOOD_VALUE;
                    return where;
                }
            }
            
        }
        return null;
    }
    
    /**
     * Check whether or not this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newwolfes A list to return newly born wolfes.
     */
    private void giveBirth(List<Animal> newwolves)
    {
        // New wolfes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        
        List<Location> locations = field.adjacentLocations(location);
        for (Location loc : locations){
            Object obj = field.getObjectAt(loc.getRow(), loc.getCol());
            if (obj != null && obj.getClass() == this.getClass())
            {
                if (((Wolf)obj).getGender() != isMale)
                {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    boolean randomGender = (rand.nextDouble()>0.5) ? true : false;
                    int births = breed(obj);
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location l = free.remove(0);
                        Wolf young = new Wolf(field, l, randomGender, false);
                        newwolves.add(young);
                    }
                    break;
                }
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed(Object obj)
    {
        int births = 0;
        if(((Wolf)obj).canBreed() && canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A wolf can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
