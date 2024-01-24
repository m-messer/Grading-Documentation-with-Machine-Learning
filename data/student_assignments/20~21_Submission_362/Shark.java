import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a shark.
 * Sharks age, move, eat whales and dolphins, and die.
 *
 * @version 17.02.2021
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharks (class variables).

    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which a shark can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    //As sharks do not have any predators, they do not have any food value attached.
    private static final int FOOD_VALUE = 0;
    //The highest food value of whatever the shark eats.  
    private static final int PREY_FOOD_VALUE = 20;//food value of whale    

    // A shared random number generator to generare random ages.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The shark's age.
    private int age;
    // The shark's food level, which is increased by eating whales and dolphins.
    private int foodLevel;
    // The shark's sex. (male or female)
    private String sex; 

    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location)
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
     * This is what the shark does most of the time: it consumes
     * whales and dolphins. In the process, it might breed, die of hunger,
     * or die of old age.
     * 
     * If a shark has a disease, it may infect other whales. 
     * 
     * @param newSharks A list to return newly born sharks.
     */
    public void act(List<Animal> newSharks)
    {
        incrementAge();
        incrementHunger();

        //Disease
        if(isAlive() && isInfected()){
            spreadDisease(); //Infect other sharks if infected
            adjustHealth();
        }

        if(isAlive()) {
            //Reproduce if possible 
            if(meet()){
                giveBirth(newSharks);   
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
     * If another shark of the opposite gender is found, calls giveBirth method.
     * If another shark of the same gender is found, return false.
     * If another animal/plant type is found, return false.
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
            if(animal instanceof Shark) {
                Shark shark = (Shark) animal;
                if(! shark.getSex().equals(sex)) { 
                    return true;   
                }
            }
        }
        return false; 
    }

    /**
     * Check whether or not this shark is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSharks A list to return newly born sharks.
     */
    public void giveBirth(List<Animal> newSharks)
    {
        // New sharks are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shark young = new Shark(false, field, loc);
            newSharks.add(young);
        }
    }

    /**
     * Look for whales and dolphins adjacent to the current location.
     * Only the first live whale or dolphin is eaten.
     * Sharks always show a preference for whales. 
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
            if(animal instanceof Whale) {
                Whale whale = (Whale) animal;
                if(whale.isAlive()) { 
                    whale.setDead();
                    foodLevel = whale.getFoodValue();
                    return where;
                }
                else if(animal instanceof Dolphin) {
                    Dolphin dolphin = (Dolphin) animal;
                    if(dolphin.isAlive()) { 
                        dolphin.setDead();
                        foodLevel = dolphin.getFoodValue();
                        return where;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Look for sharks in adjacent locations. 
     * If sharks are found, try to infect them. 
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Shark) {
                infect(animal); 
            }
        }
    }

    /**
     * Make this shark more hungry. This could result in the shark's death.
     */
    public void incrementHunger()
    {
        foodLevel--;
        dieOfHunger(); 
    }

    /**
     * Increase the age. This could result in the shark's death.
     */
    public void incrementAge()
    {
        age++;
        dieOfAge(); 
    }

    //ACCESSOR METHODS 

    /**
     * Returns the age at which a shark can start to breed.
     *
     * @return the age at which a shark can start to breed
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE; 
    }

    /**
     * Returns the age to which a shark can live.
     *
     * @return the age to which a shark can live
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Returns the likelihood of a shark breeding.
     *
     * @return the likelihood of a shark breeding
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
     * Returns the food value of a single shark. 
     * In effect, this is the food value its predator gets when a shark is eaten.
     *
     * @return the food value of a single shark
     */
    public int getFoodValue()
    {
        return FOOD_VALUE; 
    }

    /**
     * Returns the highest food value of a shark's prey.
     * In effect, this is the food value of whale.
     *
     * @return the highest food value of a shark's prey (whale)
     */
    public int getPreyFoodValue()
    {
        return PREY_FOOD_VALUE; 
    }

    /**
     * Returns the shark's age.
     *
     * @return the shark's age
     */
    public int getAge()
    {
        return age; 
    }

    /**
     * Returns the shark's food level, which is increased by eating whales.
     *
     * @return the shark's food level
     */
    public int getFoodLevel()
    {
        return foodLevel; 
    }

    /**
     * Returns the shark's sex.
     *
     * @return the shark's sex
     */
    public String getSex()
    {
        return sex; 
    }
}