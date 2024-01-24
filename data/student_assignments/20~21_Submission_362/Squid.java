import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a squid.
 * Squid age, move, eat crab, and die.
 *
 * @version 17.02.2021
 */
public class Squid extends Animal
{
    // Characteristics shared by all squid (class variables).

    // The age at which a squid can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a squid can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a squid breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single squid. In effect, this is the
    // food value its predator gets when a squid is eaten.
    private static final int FOOD_VALUE = 20;
    //The highest food value of whatever the squid eats.
    private static final int PREY_FOOD_VALUE = 20; //food value of crab  

    // A shared random number generator to generare random ages.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The squid's age.
    private int age;
    // The squid's food level, which is increased by eating crab.
    private int foodLevel;
    // The squid's sex. (male or female)
    private String sex; 

    /**
     * Create a squid. A squid can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the squid will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Squid(boolean randomAge, Field field, Location location)
    {
        super(field, location); 
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PREY_FOOD_VALUE); 
        }
        else {
            age = 0;
            foodLevel = PREY_FOOD_VALUE;
        }

        //generate a randomSex
        int randSex = rand.nextInt(2); 
        if (randSex == 0){
            sex = "male";
        } 
        else{
            sex = "female";
        } 
    }

    /**
     * This is what the squid does most of the time: it consumes
     * crabs. In the process, it might breed, die of hunger,
     * or die of old age.
     * 
     * @param newSquids A list to return newly born squids.
     */
    public void act(List<Animal> newSquids)
    {
        incrementAge();
        incrementHunger();

        //Disease
        if(isAlive() && isInfected()){
            spreadDisease(); //Infect other squid if infected
            adjustHealth();
        }

        if(isAlive()) {
            //Reproduce if possible 
            if(meet()){
                giveBirth(newSquids);   
            }

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
     * Checks neighbouring locations. 
     * If another squid of the opposite gender is found, calls giveBirth method
     * If another squid of the same gender is found, return false
     * If another animal/plant type is found, return false
     *
     */
    public boolean meet()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Squid) {
                Squid squid = (Squid) animal;
                if(! squid.getSex().equals(sex)) { 
                    return true;   
                }
            }
        }
        return false; 
    }

    /**
     * Check whether or not this squid is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSquids A list to return newly born squids.
     */
    public void giveBirth(List<Animal> newSquids)
    {
        // New squids are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Squid young = new Squid(false, field, loc);
            newSquids.add(young);
        }
    }

    /**
     * Look for crabs adjacent to the current location.
     * Only the first live crab is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Crab) {
                Crab crab = (Crab) animal;
                if(crab.isAlive()) { 
                    crab.setDead();
                    foodLevel = crab.getFoodValue();
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Look for squids in adjacent locations. 
     * If squids are found, try to infect them. 
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Squid) {
                infect(animal); 
            }
        }
    }

    /**
     * Make this squid more hungry. This could result in the squid's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        dieOfHunger(); 
    }

    /**
     * Increase the age. This could result in the squid's death.
     */
    public void incrementAge()
    {
        age++;
        dieOfAge(); 
    }

    //ACCESSOR METHODS 

    /**
     * Returns the age at which a squid can start to breed.
     *
     * @return the age at which a squid can start to breed
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE; 
    }

    /**
     * Returns the age to which a squid can live.
     *
     * @return the age to which a squid can live
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the likelihood of a squid breeding.
     *
     * @return the likelihood of a squid breeding
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY; 
    }

    /**
     * Returns the maximum number of births.
     *
     * @return the maximum number of births
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE; 
    }

    /**
     * Returns the food value of a single squid. 
     * In effect, this is the food value its predator gets when a squid is eaten.
     *
     * @return the food value of a single squid
     */
    public int getFoodValue()
    {
        return FOOD_VALUE; 
    }

    /**
     * Returns the highest food value of an squid's prey.
     * In effect, this is the food value of crab
     *
     * @return the highest food value of an squid's prey (crab)
     */
    public int getPreyFoodValue()
    {
        return PREY_FOOD_VALUE; 
    }

    /**
     * Returns the squid's age.
     *
     * @return the squid's age
     */
    public int getAge()
    {
        return age; 
    }

    /**
     * Returns the squid's food level, which is increased by eating crab.
     *
     * @return the squid's food level
     */
    public int getFoodLevel()
    {
        return foodLevel; 
    }

    /**
     * Returns the squid's sex.
     *
     * @return the squid's sex
     */
    public String getSex()
    {
        return sex; 
    }
}
