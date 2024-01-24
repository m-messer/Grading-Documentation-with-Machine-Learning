import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a whale.
 *
 * @version 21.02.2020 (2)
 */
public class Whale extends Animal
{
    // Characteristics shared by all whales (class variables).

    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 10;
    
    // The age to which a whale can live.
    private static final int MAX_AGE = 100;
    
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    
    // The food value of a single fish.
    private static final int FISH_FOOD_VALUE = 9;
    
    // The food value of a single shrimp.
    private static final int SHRIMP_FOOD_VALUE = 5;
    
    // The food value of a single plankton.
    private static final int PLANKTON_FOOD_VALUE = 2;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    
    // The whale's age.
    private int age;
    
    // The whale's food level, which is increased by eating animals.
    private int foodLevel;

    //distinguishes between male and female
    private boolean isMale;

    //calling the DayNight Class
    private DayNight dayNight;

    /**
     * Create a whale. A whale can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the whale will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param rd if true, the whale will be male
     */
    public Whale(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FISH_FOOD_VALUE) + rand.nextInt(PLANKTON_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FISH_FOOD_VALUE + PLANKTON_FOOD_VALUE ;
        }
        dayNight = new DayNight();
        Random rd = new Random(); // creating Random object
        isMale = rd.nextBoolean(); // distingushing between male and female
    }

    /**
     * This is what the whale does most of the time: it hunts for
     * fish and plankton. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWhales A list to return newly born whales.
     */
    public void act(List<Animal> newWhales)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newWhales);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null ) { 
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
     * Increase the age. This could result in the whale's death.
     */
    private void incrementAge()
    {
        age++;  //increment the age 
        //if the current age was grater than max age 
        if(age > MAX_AGE) {
            setDead(); //the whale will be dead
        }
    }

    /**
     * Make this whale more hungry. This could result in the whale's death.
     */
    private void incrementHunger()
    {
        foodLevel--;   //decreasing food level of fish by one
        //if the food level of whale is less than or equal to 0
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for fish adjacent to the current location.
     * Only the first live fish is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        //check the animals and plants around the whale
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            //only hunts if its day.
            if(dayNight.isDay()) 
            {
                //if the adjacent plankton was alive it lets the whale to eat it 
                if(animal instanceof Plankton) {           
                    Plankton plankton = (Plankton) animal;
                    if(plankton.isAlive()) { 
                        plankton.setDead();
                        foodLevel = PLANKTON_FOOD_VALUE;
                        return where;
                    }
                }
                //if the adjacent fish was alive it lets the whale to eat it 
                if(animal instanceof Fish) {
                    Fish fish = (Fish) animal;
                    if(fish.isAlive()) { 
                        fish.setDead();
                        foodLevel = FISH_FOOD_VALUE;
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this whale is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWhales A list to return newly born whales.
     */
    private void giveBirth(List<Animal> newWhales)
    {
        // New whales are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Whale young = new Whale(false, field, loc);
            newWhales.add(young);
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
     * A whale can breed if it has reached the breeding age.
     * @return true if the whale can breed, false otherwise.
     * a whale can only breed if it is next to its opposite gender
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
                //if the adjacent animal is a male shark and the food level is greater than 3
                if(animal instanceof Whale && isMale && foodLevel>3)
                {
                    //if the whale is female and is old enough to breed 
                    if (!isMale && age >= BREEDING_AGE)
                    {
                        return true;  //the whale can breed
                    }
                }
                
                //if the adjacent animal is a female whale and the food level is greater than 3
                if(animal instanceof Whale && !isMale && foodLevel >3)
                {
                    //if the whale is male and is old enough to breed
                    if (isMale && age >= BREEDING_AGE)
                    {
                        return true;  //the whale can breed
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
