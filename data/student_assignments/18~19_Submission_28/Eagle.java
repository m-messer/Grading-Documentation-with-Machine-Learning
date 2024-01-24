import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a eagle.
 * eaglees age, move, eat squirrels, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Eagle extends Animal
{
    // Characteristics shared by all eaglees (class variables).
    
    // The age at which a eagle can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a eagle can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single squirrel.
    private static final int SQUIRREL_FOOD_VALUE = 7;
    // The food value of a single squirrel.
    private static final int KINGFISHER_FOOD_VALUE = 9;
    // The food value of a single squirrel.
    private static final int SALMON_FOOD_VALUE = 5;
    // The maximum amount of food it can eat.
    private static final int FOOD_LIMIT = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The eagle's age.
    private int age;
    // The eagle's food level, which is increased by eating squirrels.
    private int foodLevel;

    /**
     * Create a eagle. A eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(Field field, Location location, boolean isMale, boolean randomAge)
    {
        super(field, location, isMale);
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
     * This is what the eagle does most of the time: it hunts for
     * squirrels. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param neweaglees A list to return newly born eaglees.
     */
    public void act(List<Animal> neweagles)
    {
        incrementAge();
        incrementHunger();
        // Snow affects animals' movements.
        if(isAlive() && rand.nextDouble() >= 0.25*field.getWeather(location.getRow(), location.getCol(), 2)) {
            //if the eagle is healthy, randomly try to make it sick
            if (isHealthy())
            {
             Random rand = new Random();
             boolean randomSick = (rand.nextDouble() <= ANIMAL_GET_SICK_PROBABILITY ) ? true : false;
            if(randomSick)
            {//make the salmon sick
             makeSick();
             //make its age to 90% old
             age = MAX_AGE - MAX_AGE/10;
            }
            }
            //if it is unhealthy, try to spread the disease
            if (!isHealthy())
            {
                spreadDisease();
            }
            //give birth to new eagles
            giveBirth(neweagles);            
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
     * Spread the disease betweeen two eagles
     */
    public void spreadDisease()
    {
     Field field = getField();
    // if(getLocation()==null) return;
    //get adjacent locations
     List<Location> adjacent = field.adjacentLocations(getLocation());
     Iterator<Location> iterator = adjacent.iterator();
      //parse all the adjacent locations to try to spread the disease
     while (iterator.hasNext()) {
         Location nearby = iterator.next();
         Object animal = field.getObjectAt(nearby);
         //check if the animal fount in the current adjacent loc is an eagle
         if (animal instanceof Eagle) {
             Eagle eagle = (Eagle) animal;
             if (eagle.isHealthy()) {
                 //if the found eagle is healthy, try to spr
                Random rand = new Random ();
                boolean randomSpread = (rand.nextDouble() <= DISEASE_SPREAD_PROBABILITY ) ? true : false;
                if (randomSpread){
                    eagle.makeSick();
                    eagle.age = MAX_AGE - MAX_AGE/10;
                }    
            }    
        }     
    }        
    }

    /**
     * Increase the age. This could result in the eagle's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this eagle more hungry. This could result in the eagle's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for squirrels, kingfishers, or salmon adjacent to the current location.
     * Only the first live prey is eaten.
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
                if(squirrel.isAlive() && foodLevel < FOOD_LIMIT && rand.nextDouble() > 0.875) { 
                    squirrel.setDead();
                    foodLevel += SQUIRREL_FOOD_VALUE;
                    return where;
                }
            }
            if(animal instanceof Kingfisher) {
                Kingfisher kingfisher = (Kingfisher) animal;
                if(kingfisher.isAlive() && foodLevel < FOOD_LIMIT && rand.nextDouble() > 0.875) { 
                    kingfisher.setDead();
                    foodLevel += KINGFISHER_FOOD_VALUE;
                    return where;
                }
            }
            if(animal instanceof Salmon) {
                Salmon salmon = (Salmon) animal;
                if(salmon.isAlive() && foodLevel < FOOD_LIMIT && rand.nextDouble() > 0.875) { 
                    salmon.setDead();
                    foodLevel += SALMON_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this eagle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param neweaglees A list to return newly born eaglees.
     */
    private void giveBirth(List<Animal> neweagles)
    {
        // New eaglees are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        
        List<Location> locations = field.adjacentLocations(location);
        for (Location loc : locations){
            Object obj = field.getObjectAt(loc.getRow(), loc.getCol());
            if (obj != null && obj.getClass() == this.getClass())
            {
                if (((Eagle)obj).getGender() != isMale)
                {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    boolean randomGender = (rand.nextDouble()>0.5) ? true : false;
                    int births = breed(obj);
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location l = free.remove(0);
                        Eagle young = new Eagle(field, l, randomGender, false);
                        neweagles.add(young);
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
        if(((Eagle)obj).canBreed() && canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A eagle can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
