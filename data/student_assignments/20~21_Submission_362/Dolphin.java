import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a dolphin.
 * dolphins age, move, eat squid, and die.
 *
 * @version 17.02.2021
 */
public class Dolphin extends Animal
{
    // Characteristics shared by all dolphins (class variables).

    // The age at which a dolphin can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a dolphin can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a dolphin breeding.
    private static final double BREEDING_PROBABILITY = 0.22;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single dolphin. In effect, this is the
    // food value its predator gets when a dolphin is eaten.
    private static final int FOOD_VALUE = 20;
    //The highest food value of whatever the dolphin eats. 
    private static final int PREY_FOOD_VALUE = 20; //food value of squid  

    // A shared random number generator to generare random ages.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The dolphin's age.
    private int age;
    // The dolphin's food level, which is increased by eating squid.
    private int foodLevel;
    // The dolphin's sex. (male or female)
    private String sex; 

    /**
     * Create a dolphin. A dolphin can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the dolphin will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dolphin(boolean randomAge, Field field, Location location)
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
     * This is what the dolphin does most of the time: it consumes
     * squids. In the process, it might breed, die of hunger,
     * or die of old age.
     * 
     * @param newDolphins A list to return newly born dolphins.
     */
    public void act(List<Animal> newDolphins)
    {
        incrementAge();
        incrementHunger();
        
        //Disease
        if(isAlive() && isInfected()){
            spreadDisease(); //Infect other dolphins if infected
            adjustHealth();
        }
        
        if(isAlive()) {
            //Reproduce if possible 
            if(meet()){
                giveBirth(newDolphins);   
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
     * If another dolphin of the opposite gender is found, calls giveBirth method
     * If another dolphin of the same gender is found, return false
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
            if(animal instanceof Dolphin) {
                Dolphin dolphin = (Dolphin) animal;
                if(! dolphin.getSex().equals(sex)) { 
                    return true;   
                }
            }
        }
        return false; 
    }

    /**
     * Check whether or not this dolphin is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDolphins A list to return newly born dolphins.
     */
    public void giveBirth(List<Animal> newDolphins)
    {
        // New dolphins are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Dolphin young = new Dolphin(false, field, loc);
            newDolphins.add(young);
        }
    }

    /**
     * Look for squids adjacent to the current location.
     * Only the first live squid is eaten.
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
            if(animal instanceof Squid) {
                Squid squid = (Squid) animal;
                if(squid.isAlive()) { 
                    squid.setDead();
                    foodLevel = squid.getFoodValue();
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Look for dolphins in adjacent locations. 
     * If dolphins are found, try to infect them. 
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Dolphin) {
                infect(animal); 
            }
        }
    }

    /**
     * Make this dolphin more hungry. This could result in the dolphin's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        dieOfHunger(); 
    }

    /**
     * Increase the age. This could result in the dolphin's death.
     */
    public void incrementAge()
    {
        age++;
        dieOfAge(); 
    }

    //ACCESSOR METHODS 

    /**
     * Returns the age at which a dolphin can start to breed.
     *
     * @return the age at which a dolphin can start to breed
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE; 
    }

    /**
     * Returns the age to which a dolphin can live.
     *
     * @return the age to which a dolphin can live
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the likelihood of a dolphin breeding.
     *
     * @return the likelihood of a dolphin breeding
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
     * Returns the food value of a single dolphin. 
     * In effect, this is the food value its predator gets when a dolphin is eaten.
     *
     * @return the food value of a single dolphin
     */
    public int getFoodValue()
    {
        return FOOD_VALUE; 
    }

    /**
     * Returns the highest food value of an dolphin's prey.
     * In effect, this is the food value of squid
     *
     * @return the highest food value of an dolphin's prey (squid)
     */
    public int getPreyFoodValue()
    {
        return PREY_FOOD_VALUE; 
    }

    /**
     * Returns the dolphin's age.
     *
     * @return the dolphin's age
     */
    public int getAge()
    {
        return age; 
    }

    /**
     * Returns the dolphin's food level, which is increased by eating squid.
     *
     * @return the dolphin's food level
     */
    public int getFoodLevel()
    {
        return foodLevel; 
    }

    /**
     * Returns the dolphin's sex.
     *
     * @return the dolphin's sex
     */
    public String getSex()
    {
        return sex; 
    }
}
