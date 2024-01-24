import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a whale.
 * Whales age, move, eat dolphins, and die.
 *
 * @version 17.02.2021
 */
public class Whale extends Animal
{
    // Characteristics shared by all whales (class variables).

    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a whale can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.24;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // The food value of a single whale. In effect, this is the
    // food value its predator gets when a whale is eaten.
    private static final int FOOD_VALUE = 20; 
    //The highest food value of whatever the whale eats.
    private static final int PREY_FOOD_VALUE = 20; //food value of dolphin  

    // A shared random number generator to generare random ages.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The whale's age.
    private int age;
    // The whale's food level, which is increased by eating squids.
    private int foodLevel;
    // The whale's sex. (male or female)
    private String sex; 

    /**
     * Create a whale. A whale can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the whale will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Whale(boolean randomAge, Field field, Location location)
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
     * This is what the whale does most of the time: it consumes
     * dolphins. In the process, it might breed, die of hunger,
     * or die of old age.
     * 
     * @param field The field currently occupied.
     * @param newWhales A list to return newly born whales.
     */
    public void act(List<Animal> newWhales)
    {
        incrementAge();
        incrementHunger();
        
        //Disease
        if(isAlive() && isInfected()){
            spreadDisease(); //Infect other whales if infected
            adjustHealth();
        }
        
        if(isAlive()) {
            //Reproduce if possible 
            if(meet()){
                giveBirth(newWhales);   
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
     * If another whale of the opposite gender is found, calls giveBirth method
     * If another whale of the same gender is found, return false
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
            if(animal instanceof Whale) {
                Whale whale = (Whale) animal;
                if(! whale.getSex().equals(sex)) { 
                    return true;   
                }
            }
        }

        return false; 
    }

    /**
     * Check whether or not this whale is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newWhales A list to return newly born whales.
     */
    public void giveBirth(List<Animal> newWhales)
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
     * Look for dolphins adjacent to the current location.
     * Only the first live dolphin is eaten.
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
            if(animal instanceof Dolphin) {
                Dolphin dolphin = (Dolphin) animal;
                if(dolphin.isAlive()) { 
                    dolphin.setDead();
                    foodLevel = dolphin.getFoodValue();
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Look for whales in adjacent locations. 
     * If whales are found, try to infect them. 
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Whale) {
                infect(animal); 
            }
        }
    }

    /**
     * Make this whale more hungry. This could result in the whale's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        dieOfHunger(); 
    }

    /**
     * Increase the age. This could result in the whale's death.
     */
    public void incrementAge()
    {
        age++;
        dieOfAge(); 
    }

    //ACCESSOR METHODS 

    /**
     * Returns the age at which a whale can start to breed.
     *
     * @return the age at which a whale can start to breed
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE; 
    }

    /**
     * Returns the age to which a whale can live.
     *
     * @return the age to which a whale can live
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the likelihood of a whale breeding.
     *
     * @return the likelihood of a whale breeding
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
     * Returns the food value of a single whale. 
     * In effect, this is the food value its predator gets when a whale is eaten.
     *
     * @return the food value of a single whale
     */
    public int getFoodValue()
    {
        return FOOD_VALUE; 
    }

    /**
     * Returns the highest food value of a whale's prey.
     * In effect, this is the food value of dolphin
     *
     * @return the highest food value of a whale's prey (dolphin)
     */
    public int getPreyFoodValue()
    {
        return PREY_FOOD_VALUE; 
    }

    /**
     * Returns the whale's age.
     *
     * @return the whale's age
     */
    public int getAge()
    {
        return age; 
    }

    /**
     * Returns the whale's food level, which is increased by eating dolphin.
     *
     * @return the whale's food level
     */
    public int getFoodLevel()
    {
        return foodLevel; 
    }

    /**
     * Returns the whale's sex.
     *
     * @return the whale's sex
     */
    public String getSex()
    {
        return sex; 
    }
}