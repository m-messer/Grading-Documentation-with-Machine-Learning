import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a shark.
 *
 * @version 21.02.2020 (2)
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharks (class variables).

    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 15;

    // The age to which a shark can live.
    private static final int MAX_AGE = 120;

    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.15;

    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;

    // The food value of a single fish. 
    private static final int FISH_FOOD_VALUE = 9;

    //the food value of a single plankton
    private static final int PLANKTON_FOOD_VALUE = 2;

    // The food value of a single shrimp
    private static final int SHRIMP_FOOD_VALUE = 5;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The shark's age.
    private int age;
    // The shark's food level, which is increased by eating food
    private int foodLevel;

    //distinguishes between male and female
    private boolean isMale;

    //calling the DayNight Class
    private DayNight dayNight;
    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param rd if true, the shark will be male
     */
    public Shark(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FISH_FOOD_VALUE) + rand.nextInt(SHRIMP_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FISH_FOOD_VALUE + SHRIMP_FOOD_VALUE;    
        }
        dayNight = new DayNight();
        Random rd = new Random(); // creating Random object
        isMale = rd.nextBoolean(); // distingushing between male and female
    }

    /**
     * This is what the shark does most of the time: it hunts for
     * fish. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSharkes A list to return newly born sharkes.
     */
    public void act(List<Animal> newSharkes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSharkes);            
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
     * Increase the age. This could result in the shark's death.
     */
    private void incrementAge()
    {
        age++;//increment the age 
        //if the current age was grater than max age 
        if(age > MAX_AGE) {
            setDead();//the shark will be dead
        }
    }

    /**
     * Make this shark more hungry. This could result in the shark's death.
     */
    private void incrementHunger()
    {
        foodLevel--; //decreasing food level of fish by one
        //if the food level of shark is less than or equal to 0
        if(foodLevel <= 0) {
            setDead();//the shark will be dead
        }
    }

    /**
     * Look for fish adjacent to the current location.
     * Only the first live fish is eaten.
     * shark can eat plankton and shrimp only if there is 
     * no whale around it
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        //check the animals and plants around the shark
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            boolean whaleEx = animal instanceof Whale;

            //if the adjacent shrimp was alive and there isnt any whale around the shark let the shark to eat it 
            if(animal instanceof Shrimp && !whaleEx) {            
                Shrimp shrimp = (Shrimp) animal;
                if(shrimp.isAlive()) { 
                    shrimp.setDead();
                    foodLevel = SHRIMP_FOOD_VALUE;
                    return where;
                }
            }

            //if the adjacent plankton was alive and there isnt any whale around the shark let the shark to eat it 
            if(animal instanceof Plankton && !whaleEx) {           
                Plankton plankton = (Plankton) animal;
                if(plankton.isAlive()) { 
                    plankton.setDead();
                    foodLevel = PLANKTON_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSharkes A list to return newly born sharkes.
     */
    private void giveBirth(List<Animal> newSharkes)
    {
        // New sharkes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shark young = new Shark(false, field, loc);
            newSharkes.add(young);
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
        if(dayNight.isDay() && canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {   //breeds only in day.
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A shark can breed if it has reached the breeding age.
     * @return true if the shark can breed, false otherwise.
     * a shark can only breed if it is next to its opposite gender
     */
    private boolean canBreed()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            for (int i =0; i>= field.adjacentLocations(getLocation()).size(); i++)
            {
                //if the adjacent animal is a male shark 
                if(animal instanceof Shark && isMale)
                {
                    //if the shark is female and is old enough to breed and the food level is grater than 2
                    if (!isMale && age >= BREEDING_AGE && foodLevel>2)
                    {
                        return true; //the shark can breed
                    }
                }

                //if the adjacent animal is a female shark
                if(animal instanceof Shark && !isMale )
                {
                    //if the shark is male and is old enough to breed and the food level is grater than 2
                    if (isMale && age >= BREEDING_AGE && foodLevel>2)
                    {
                        return true; //the shark can breed
                    }
                }
            }
        }
        return age >= BREEDING_AGE;
    }

    /**
     * returns the food level of sahrk
     */
    public int returnHunger()
    {
        return foodLevel;
    }
}