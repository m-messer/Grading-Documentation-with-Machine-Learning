import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Dragon.
 * Dragones age, move, eat Goats, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Dragon extends Animal
{
    // Characteristics shared by all Dragones (class variables).
    
    // The age at which a Dragon can start to breed.
    private static final int BREEDING_AGE = 50;
    // The age to which a Dragon can live.
    private static final int MAX_AGE = 700;
    // The max numebr of years a Dragon can live with an infection 
    private static final int MAX_INFECTION_AGE = 200;
    // The likelihood of a Dragon breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single prey. In effect, this is the
    // number of steps a Dragon can go before it has to eat again.
    private static final int FOOD_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The Dragon's age.
    private int age;
    // The Dragon's food level, which is increased by eating prey.
    private int foodLevel;
    // this is a counter of how long the Dragon has lived with an infection 
    private int infectionAge; 
    
    /**
     * Create a Dragon. A Dragon can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Dragon will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dragon(boolean randomAge, Field field, Location location)
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
     * This is what the Dragon does most of the time: it hunts for
     * Goats. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newDragones A list to return newly born Dragones.
     */
    public void act(List<Animal> newDragones)
    {
        incrementAge();
        if(isInfected()) {
            incrementInfectionAge();
        }
        incrementHunger();
        if(isAlive()) {
            giveBirth(newDragones);            
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
     * Increase the age. This could result in the Dragon's death.
     */
    public void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Increase the infection age. This could result in the Dragon's death.
     */
    public void incrementInfectionAge()
    {
        infectionAge++;
        if(infectionAge >= MAX_INFECTION_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this Dragon more hungry. This could result in the Dragon's death.
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
     * Check whether or not this Dragon is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDragones A list to return newly born Dragones.
     */
    private void giveBirth(List<Animal> newDragones)
    {
        if(findPartner() && FOOD_VALUE > 7) {
           // New Dragones are born into adjacent locations.
           // Get a list of adjacent free locations.
           Field field = getField();
           List<Location> free = field.getFreeAdjacentLocations(getLocation());
           int births = breed();
           for(int b = 0; b < births && free.size() > 0; b++) {
               Location loc = free.remove(0);
               Dragon young = new Dragon(false, field, loc);
               if(isInfected()) {
                   young.setInfected();
               }
               newDragones.add(young);
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
            if(animal instanceof Dragon) {
                Dragon DragonPartner = (Dragon) animal;
                String partnerOneGender = getGender();
                String partnerGender = DragonPartner.getGender();
                // these dragons met and if one of them is infected 
                // then they all are infected 
                if(DragonPartner.isInfected() || isInfected()) {
                        DragonPartner.setInfected();
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
     * A Dragon can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
