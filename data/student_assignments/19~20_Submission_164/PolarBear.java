import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a polar bear.
 * polar bears age, move, eat seals and fish, and die.
 *
 * @version 2016.02.29 (2)
 */
public class PolarBear extends Animal 
{
    // Characteristics shared by all polar bears (class variables).

    // The age at which a polar bear can start to breed.
    private static final int BREEDING_AGE = 2;
    // The age to which a polar bear can live.
    private static final int MAX_AGE = 700;
    // The likelihood of a polar bear breeding.
    private static final double BREEDING_PROBABILITY = 0.9;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single fish. In effect, this is the
    // number of steps a polar bear can go before it has to eat again.
    private static final int FISH_FOOD_VALUE = 13;    
    // The food value of a single seal. In effect, this is the
    // number of steps a polar bear can go before it has to eat again.
    private static final int SEAL_FOOD_VALUE = 13;
    // The food value of a single fox. In effect, this is the
    // number of steps a polar bear can go before it has to eat again.
    private static final int FOX_FOOD_VALUE = 7;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Individual characteristics (instance fields).
    // The polar bear's age.
    private int age;
    // The polar bear's food level, which is increased by eating fish , fox and seals.
    private int foodLevel;
    
    private boolean female;

    /**
     * Create a polar bear. A polar bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the polar bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param step The step that the simulator is on
     */
    public PolarBear(boolean randomAge, Field field, Location location, Step step)
    {
        super(field, location, step);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FISH_FOOD_VALUE + SEAL_FOOD_VALUE 
            + FOX_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FISH_FOOD_VALUE + SEAL_FOOD_VALUE + FOX_FOOD_VALUE;
        }
        
        if (rand.nextDouble() <= 0.7) {
           female = true;
        }
        else {
           female = false;
        }
        
    }

    /**
     * This is what the polar bear does most of the time: it hunts for
     * fish and seals. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newFoxes A list to return newly born polar bears.
     */
    public void actNight(List<Animal> newPolarBears)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newPolarBears);            
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
     * This is what the polar bear does most of the time at Day time:
     * In the process, it might breed, die of hunger.
     * @param newPolarBears A list to return newly born Polar bears.
     */
    public void actDay(List<Animal> newPolarBears)
    {
        incrementHunger();
        if(isAlive()) {
            giveBirth(newPolarBears);            
        }
    }
    
    /**
     * Increase the age. This could result in the polar bear's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Increase the hunger level. This could result in the polar bear's death.
     * At night,makes polar bear hungry more than Day time, 
     * as it cannot hunt for a prey at Night.
     */
    private void incrementHunger()
    {
        if(isDay()){
            foodLevel --;
        }
        else{
            foodLevel -= 2;
        }
        
        if(foodLevel <= 0) {
            setDead();
        }
    }


    /**
     * Look for fish, fox and seals adjacent to the current location.
     * Only the first live fish/seal/fox is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        Step step = getStep();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Fish) {
                Fish fish = (Fish) animal;
                if(fish.isAlive()) { 
                    fish.setDead();
                    foodLevel = FISH_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Seal ) {
                Seal seal = (Seal) animal;
                if(seal.isAlive()) { 
                    seal.setDead();
                    foodLevel = SEAL_FOOD_VALUE;
                    return where; 
                }
            }
            else if(animal instanceof Fox ) {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) { 
                    fox.setDead();
                    foodLevel = FOX_FOOD_VALUE;
                    return where; 
                }
            }
        }
        return null;
    }
    
     /**
     * @return true if the polar bear is female
     */
    private boolean isFemale(){
       return female;   
    }
    
    /**
     * Check whether or not this polar bear is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newPolarBears A list to return newly born polar bears.
     */
    private void giveBirth(List<Animal> newPolarBears)
    {
        // New polar bears are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        Step step = getStep();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            PolarBear young = new PolarBear(false, field, loc, step);
            newPolarBears.add(young);
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
     * Checks the adjacent locations of the instance to see if
     * there is a male of the same species in an adjacent square
     * @return true if a male is found
     */
    private boolean nextToMale()
    {
        //check through the list of adjacent locations to see if a male polarbear is
        //next to the female polarbear
        boolean found = false;
        Field field = getField();
        List<Location> locations = field.adjacentLocations(getLocation()) ;
        Iterator<Location> it = locations.iterator();
        while(it.hasNext() && !found)
        {  
          if (field.getObjectAt(it.next()) instanceof PolarBear) {
              PolarBear polarBear = (PolarBear) field.getObjectAt(getLocation());
              if (!polarBear.isFemale()) {
                 found = true;
                 return found;
            }             
          }
        }
        return found;
    }

    /**
     * A polar bear can breed if it has reached the breeding age,
     * is a female polar bear, and is next to a male polar bear
     * @return true if all these conditions apply
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE && isFemale() && nextToMale();
    }  

}
