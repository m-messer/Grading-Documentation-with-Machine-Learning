import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Boar.
 * Boars age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Boar extends Animal
{
    // Characteristics shared by all Boars (class variables).

    // The age at which a Boar can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Boar can live.
    private static final int MAX_AGE = 60;
    // The max numebr of years a Boar can live with an infection 
    private static final int MAX_INFECTION_AGE = 10;
    // The likelihood of a Boar breeding.
    private static final double BREEDING_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single prey. In effect, this is the
    // number of steps a Boar can go before it has to eat again.
    private static final int FOOD_VALUE = 10;
    
    // Individual characteristics (instance fields).
    
    // The Boar's age.
    private int age;
    // The Boar's food level, which is increased by eating prey.
    private int foodLevel;
    // this is a counter of how long the Boar has lived with an infection 
    private int infectionAge; 
    
    /**
     * Create a new Boar. A Boar may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Boar will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Boar(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = FOOD_VALUE;
        }
    }
    
    /**
     * This is what the Boar does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newBoars A list to return newly born Boars.
     */
    public void act(List<Animal> newBoars)
    {
        incrementAge();
        if(isInfected()) {
            incrementInfectionAge();
        }
        incrementHunger();
        if(isAlive()) {
            giveBirth(newBoars);  
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // Try to move into a free location.
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
     * Increase the age.
     * This could result in the Boar's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Increase the infection age. This could result in the Boar's death.
     */
    public void incrementInfectionAge()
    {
        infectionAge++;
        if(infectionAge >= MAX_INFECTION_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this Boar more hungry. This could result in the Goat's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for Grass adjacent to the current location.
     * Only the first live Grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plants = field.getObjectAt(where);
            if(plants instanceof Grass) {
                Grass Grass = (Grass) plants;
                if(Grass.isAlive()) { 
                    Grass.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this Boar is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newBoars A list to return newly born Boars.
     */
    private void giveBirth(List<Animal> newBoars)
    {
        if(findPartner() == true && FOOD_VALUE > 3) {
           // New Boars are born into adjacent locations.
           // Get a list of adjacent free locations.
           Field field = getField();
           List<Location> free = field.getFreeAdjacentLocations(getLocation());
           int births = breed();
           for(int b = 0; b < births && free.size() > 0; b++) {
               Location loc = free.remove(0);
               Boar young = new Boar(false, field, loc);
               if(isInfected()) {
                   young.setInfected();
               }
               newBoars.add(young);
           }
        }
    }
    
    /**
     * Method to find a partner at the adjacent fields
     * @return boolean true if there is a partner.
     */
    private boolean findPartner()
    {
        boolean breedableGenders = false;
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Boar) {
                Boar BoarPartner = (Boar) animal;
                String partnerOneGender = getGender();
                String partnerGender = BoarPartner.getGender();
                // these boars met and if one of them is infected 
                // then they all are infected 
                if(BoarPartner.isInfected() || isInfected()) {
                        BoarPartner.setInfected();
                        setInfected();
                }
                if(!partnerGender.equals(partnerOneGender)) { 
                    breedableGenders = true;
                } else {
                    breedableGenders = false;
                }
            }
        }
        return breedableGenders;
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
     * A Boar can breed if it has reached the breeding age.
     * @return true if the Boar can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
