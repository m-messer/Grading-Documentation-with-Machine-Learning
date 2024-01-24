import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a kingfisher.
 * kingfisheres age, move, eat salmons, and die.
 *
 * @version 22/02/2019
 */
public class Kingfisher extends Animal
{
    // Characteristics shared by all kingfisheres (class variables).
    
    // The age at which a kingfisher can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a kingfisher can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a kingfisher breeding.
    private static final double BREEDING_PROBABILITY = 0.6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single salmon.
    private static final int SALMON_FOOD_VALUE = 7;
    // The maximum amount of food it can eat.
    private static final int FOOD_LIMIT = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The kingfisher's age.
    private int age;
    // The kingfisher's food level, which is increased by eating salmons.
    private int foodLevel;

    /**
     * Create a kingfisher. A kingfisher can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the kingfisher will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Kingfisher(Field field, Location location, boolean isMale, boolean randomAge)
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
     * This is what the kingfisher does most of the time: it hunts for
     * salmons. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newkingfisheres A list to return newly born kingfisheres.
     */
    public void act(List<Animal> newkingfishers)
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
            giveBirth(newkingfishers);            
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
     * Spread the disease.
     */
    public void spreadDisease()
    {
     Field field = getField();
     List<Location> adjacent = field.adjacentLocations(getLocation());
     Iterator<Location> iterator = adjacent.iterator();
     while (iterator.hasNext()) {
         Location nearby = iterator.next();
         Object animal = field.getObjectAt(nearby); // Checks through neighbouring elements to detect animal.
         if (animal instanceof Kingfisher) { // Only animals of the same species can infect each other.
             Kingfisher kingfisher = (Kingfisher) animal;
             if (kingfisher.isHealthy()) { // Spreads disease only if animal is currently healthy
                Random rand = new Random ();
                boolean randomSpread = (rand.nextDouble() <= DISEASE_SPREAD_PROBABILITY ) ? true : false;
                if (randomSpread){ // Random chance that it spreads.
                    kingfisher.makeSick();
                    kingfisher.age = MAX_AGE - MAX_AGE/10;
                }    
            }    
        }     
    }        
    }

    /**
     * Increase the age. This could result in the kingfisher's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this kingfisher more hungry. This could result in the kingfisher's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
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
            Object animal = field.getObjectAt(where);
            if(animal instanceof Salmon) {
                Salmon salmon = (Salmon) animal;
                if(salmon.isAlive() && foodLevel < FOOD_LIMIT && rand.nextDouble() > 0.875) { 
                    salmon.setDead();
                    foodLevel += SALMON_FOOD_VALUE;
                    return where;
                }
            }
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
     * Check whether or not this kingfisher is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newkingfisheres A list to return newly born kingfisheres.
     */
    private void giveBirth(List<Animal> newkingfishers)
    {
        // New kingfisheres are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        
        List<Location> locations = field.adjacentLocations(location);
        for (Location loc : locations){
            Object obj = field.getObjectAt(loc.getRow(), loc.getCol());
            if (obj != null && obj.getClass() == this.getClass())
            {
                if (((Kingfisher)obj).getGender() != isMale)
                {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    boolean randomGender = (rand.nextDouble()>0.5) ? true : false;
                    int births = breed(obj);
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location l = free.remove(0);
                        Kingfisher young = new Kingfisher(field, l, randomGender, false);
                        newkingfishers.add(young);
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
        if(((Kingfisher)obj).canBreed() && canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A kingfisher can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
