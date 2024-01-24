import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Tiger.
 * Tigeres age, move, eat Goats, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Tiger extends Animal
{
    // Characteristics shared by all Tigeres (class variables).
    
    // The age at which a Tiger can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Tiger can live.
    private static final int MAX_AGE = 73;
    // The max numebr of years a Tiger can live with an infection 
    private static final int MAX_INFECTION_AGE = 11;
    // The likelihood of a Tiger breeding.
    private static final double BREEDING_PROBABILITY = 0.45;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single prey. In effect, this is the
    // number of steps a Tiger can go before it has to eat again.
    private static final int FOOD_VALUE = 20;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The Tiger's age.
    private int age;
    // The Tiger's food level, which is increased by eating prey.
    private int foodLevel;
    // this is a counter of how long the Tiger has lived with an infection 
    private int infectionAge; 

    /**
     * Create a Tiger. A Tiger can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Tiger will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tiger(boolean randomAge, Field field, Location location)
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
     * This is what the Tiger does most of the time: it hunts for
     * Goats. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newTigeres A list to return newly born Tigeres.
     */
    public void act(List<Animal> newTigeres)
    {
        incrementAge();
        if(isInfected()) {
            incrementInfectionAge();
        }
        incrementHunger();
        if(isAlive()) {
            giveBirth(newTigeres);            
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
     * Increase the age. This could result in the Tiger's death.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Increase the infection age. This could result in the Tiger's death.
     */
    public void incrementInfectionAge()
    {
        infectionAge++;
        if(infectionAge >= MAX_INFECTION_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this Tiger more hungry. This could result in the Tiger's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for Goats adjacent to the current location.
     * Only the first live Goat is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Goat) {
                Goat Goat = (Goat) animal;
                if(Goat.isAlive()) { 
                    // if the prey was infected than the Dragon gets the infection 
                    if(Goat.isInfected()) {
                        setInfected();
                    }
                    Goat.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Boar) {
                Boar Boar = (Boar) animal;
                if(Boar.isAlive()) { 
                    // if the prey was infected than the Dragon gets the infection 
                    if(Boar.isInfected()) {
                        setInfected();
                    }
                    Boar.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Deer) {
                Deer Deer = (Deer) animal;
                if(Deer.isAlive()) { 
                    // if the prey was infected than the Dragon gets the infection 
                    if(Deer.isInfected()) {
                        setInfected();
                    }
                    Deer.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this Tiger is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newTigeres A list to return newly born Tigeres.
     */
    public void giveBirth(List<Animal> newTigeres)
    {
        if(findPartner() == true && FOOD_VALUE > 4) {
           // New Tigeres are born into adjacent locations.
           // Get a list of adjacent free locations.
           Field field = getField();
           List<Location> free = field.getFreeAdjacentLocations(getLocation());
           int births = breed();
           for(int b = 0; b < births && free.size() > 0; b++) {
               Location loc = free.remove(0);
               Tiger young = new Tiger(false, field, loc);
               if(isInfected()) {
                   young.setInfected();
               }
               newTigeres.add(young);
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
            if(animal instanceof Tiger) {
                Tiger TigerPartner = (Tiger) animal;
                String partnerOneGender = getGender();
                String partnerGender = TigerPartner.getGender();
                // these tigers met and if one of them is infected 
                // then they all are infected 
                if(TigerPartner.isInfected() || isInfected()) {
                        TigerPartner.setInfected();
                        setInfected();
                }
                // continue with everything else 
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
     * A Tiger can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
