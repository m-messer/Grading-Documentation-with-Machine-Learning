import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a crab.
 * Crabs age, move, eat seaweed and phytoplankton, and die.
 * Crabs sleep during the night i.e. do they do not
 * breed or move. 
 *
 * @version 17.02.2021
 */
public class Crab extends Animal
{
    // Characteristics shared by all crabs (class variables).

    // The age at which a crab can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a crab can live.
    private static final int MAX_AGE = 60;
    // The likelihood of a crab breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    //As crabs do not have any predators, they do not have any food value attached.
    private static final int FOOD_VALUE = 20;
    //The highest food value of whatever the crab eats. 
    private static final int PREY_FOOD_VALUE = 9; //food value of seaweed  

    // A shared random number generator to generare random ages.
    private static final Random rand = Randomizer.getRandom();

    //An object to monitor the time of day (day or night); 
    private TimeOfDay time = new TimeOfDay(); 

    // Individual characteristics (instance fields).
    // The crab's age.
    private int age;
    // The crab's food level, which is increased by eating seaweed.
    private int foodLevel;
    // The crab's sex. (male or female)
    private String sex; 

    /**
     * Create a crab. A crab can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the crab will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Crab(boolean randomAge, Field field, Location location)
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
     * This is what the crab does most of the time: it hunts for
     * seaweed. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newCrabs A list to return newly born crabs.
     */
    public void act(List<Animal> newCrabs)
    {
        incrementAge();

        if(time.getTime().equalsIgnoreCase("day")){
            incrementHunger();

            //Disease
            if(isAlive() && isInfected()){
                spreadDisease(); //Infect other crabs if infected
                adjustHealth();
            }

            if(isAlive()) {

                //Reproduce if possible 
                if(meet()){
                    giveBirth(newCrabs);   
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
    }
    
    /**
     * Checks neighbouring locations. 
     * If another crab of the opposite gender is found, calls giveBirth method
     * If another crab of the same gender is found, return false
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
            if(animal instanceof Crab) {
                Crab crab = (Crab) animal;
                if(! crab.getSex().equals(sex)) { 
                    return true;   
                }
            }
        }
        return false; 
    }

    /**
     * Check whether or not this crab is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCrabs A list to return newly born crabs.
     */
    public void giveBirth(List<Animal> newCrabs)
    {
        // New crabs are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Crab young = new Crab(false, field, loc);
            newCrabs.add(young);
        }
    }

    /**
     * Look for seaweed and phytoplankton adjacent to the current location.
     * Randomly eats the first seaweed of phytoplankton found.
     * @return Where food was found, or null if it wasn't.
     */
    public Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            //generate a randPreference
            Random randomPreferenceGenerator = new Random(); 
            int randPreference = randomPreferenceGenerator.nextInt(2); 
            if (randPreference == 0) {
                //prefer seaweed
                if(organism instanceof Seaweed) {
                    Seaweed seaweed = (Seaweed) organism;
                    if(seaweed.isAlive()) { 
                        seaweed.setDead();
                        foodLevel = seaweed.getFoodValue();
                        return where;
                    }
                }
                else if(organism instanceof Phytoplankton) {
                    Phytoplankton phytoplankton = (Phytoplankton) organism;
                    if(phytoplankton.isAlive()) { 
                        phytoplankton.setDead();
                        foodLevel = phytoplankton.getFoodValue();
                        return where;
                    }
                }
            }
            else {
                //prefer phytoplankton 
                if(organism instanceof Phytoplankton) {
                    Phytoplankton phytoplankton = (Phytoplankton) organism;
                    if(phytoplankton.isAlive()) { 
                        phytoplankton.setDead();
                        foodLevel = phytoplankton.getFoodValue();
                        return where;
                    }
                }
                else if(organism instanceof Seaweed) {
                    Seaweed seaweed = (Seaweed) organism;
                    if(seaweed.isAlive()) { 
                        seaweed.setDead();
                        foodLevel = seaweed.getFoodValue();
                        return where;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Look for crabs in adjacent locations. 
     * If crabs are found, try to infect them. 
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Crab) {
                infect(animal); 
            }
        }
    }

    /**
     * Make this crab more hungry. This could result in the crab's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        dieOfHunger(); 
    }

    /**
     * Increase the age. This could result in the crab's death.
     */
    public void incrementAge()
    {
        age++;
        dieOfAge(); 
    }

    //ACCESSOR METHODS 

    /**
     * Returns the age at which a crab can start to breed.
     *
     * @return the age at which a crab can start to breed
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE; 
    }

    /**
     * Returns the age to which a crab can live.
     *
     * @return the age to which a crab can live
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the likelihood of a crab breeding.
     *
     * @return the likelihood of a crab breeding
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
     * Returns the food value of a single crab. 
     * In effect, this is the food value its predator gets when a crab is eaten.
     *
     * @return the food value of a single crab
     */
    public int getFoodValue()
    {
        return FOOD_VALUE; 
    }

    /**
     * Returns the highest food value of an crab's food.
     * In effect, this is the food value of seaweed
     *
     * @return the highest food value of an crab's food (seaweed)
     */
    public int getPreyFoodValue()
    {
        return PREY_FOOD_VALUE; 
    }

    /**
     * Returns the crab's age.
     *
     * @return the crab's age
     */
    public int getAge()
    {
        return age; 
    }

    /**
     * Returns the crab's food level, which is increased by eating seaweed.
     *
     * @return the crab's food level
     */
    public int getFoodLevel()
    {
        return foodLevel; 
    }

    /**
     * Returns the crab's sex.
     *
     * @return the crab's sex
     */
    public String getSex()
    {
        return sex; 
    }
}
