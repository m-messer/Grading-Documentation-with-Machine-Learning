import java.util.List;
import java.util.Iterator;
/**
 * This is a model for hens. 
 * Hens age, move, die, get sick and give birth. However they get eaten by the snakes.
 *
 * @version 22.02.2019
 */
public class Hen extends Chicken
{
    //the age at which hen can start to reproduce with the roosters
    private static final int BREEDING_AGE = 2; 
    //the max age that can be reached by a hen
    private static final int MAX_AGE = 20; 
    //the max number of births that can be given by a hen
    private static final int MAX_LITTER_SIZE = 2; 
    //the probability of a hen giving birth
    private static final double BREEDING_PROBABILITY = 0.99; 
    
    //stores whether the hen is sick or not
    private boolean isSick; 
    
    /**
     * Create a hen. A hen may be created with age
     * zero (a new born) or with a random age.
     * It also determines if the hen is sick or not.
     * 
     * @param randomAge If true, the hen will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hen(boolean randomAge, Field field, Location location )
    {
        super(randomAge, field, location);
        age = super.age; 
        isSick = super.getIsSick() ; 
        
    }

    /**
     * This is what the hen does most of the time - it runs 
     * around. Sometimes it will breed, die of old age or get sick.
     * @param newHen A list to return newly born hen.
     */
    public void act(List<Organism> newHen)
    {
        incrementAge();
        if(isAlive()) {        
            if(findRooster(newHen)){
                giveBirth(newHen); 
            }
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation()); 
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
     * Check whether or not this hen is to give birth at this step.
     * New births will be made into free adjacent locations.
     * Hen gives birth to new hens and roosters.
     * @param newHen A list to return newly born hen.
     */
    private void giveBirth(List<Organism> newHen)
    {
        // New hens are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        //Creating new hen and rooster
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Hen youngHen = new Hen(false, field, loc);
            Rooster youngRooster = new Rooster(false, field, loc);
            newHen.add(youngHen);
            newHen.add(youngRooster);
        }
    }

    /**
     * Increments the age of the hen
     */
    public void incrementAge(){
        super.incrementAge();
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * A hen can breed if it has reached the breeding age.
     * @return true if the hen can breed, false otherwise.
     */
    private boolean canBreed(){
        if(isSick){ 
            return false; 
        }
        return age >= BREEDING_AGE;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    public int breed(){
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
           births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
    
    /**
     * Finds a rooster adjacent to the current location to reproduce with.
     * @param newHen A list to return newly born chickens.
     * @return true if a rooster is found and give birth. 
     */
    public boolean findRooster(List<Organism> newHen){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object Organism = field.getObjectAt(where);
            if(Organism instanceof Rooster) {
                Rooster rooster = (Rooster) Organism;
                if(rooster.isAlive()) { 
                    giveBirth(newHen);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * @return the maximum age of hen in which it can live
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
}
