import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of Algae
 * Algae gets eaten by fish, it breeds, ages, dies and it does not move.
 *
 * @version (a version number or a date)
 */
public class Algae extends Animal
{
    // Characteristics shared by all Algae (class variables).

    // The age at which an algae can start to reproduce.
    private static final int BREEDING_AGE = 5;
    // The age to which an Algae can live.
    private static final int MAX_AGE = 35;
    // The likelihood of a algae reproducing.
    private static final double BREEDING_PROBABILITY = 0.18;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The algae's age.


    /**
     * Create a new algae. An algae may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the algae will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Algae(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        int age = 0;
        if(randomAge) {
                age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the algae does most of the time - it does not move. Sometimes it will breed or die of old age.
     * @param newFish A list to return newly born algae.
     */
    public void act(List<Animal> newAlgae)
    {
        incrementAge();
        if(isAlive()) {
            giveBirth(newAlgae);            
        }
    }
    
    public void nightAct(List<Animal> newAlgae)
    {
        incrementAge();
    }
    
    public int getMaxAge(){
        return MAX_AGE;
    }
    
    public int getBreedingAge(){
        return BREEDING_AGE;
    }
    
    public double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }
    
    public int getLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    
    /**
     * Look for the opposite sex adjacent to the current location.
     * First mate found will be chosen.
     * @return If mate was found, or false if it wasn't.
     */
    protected boolean checkMate()
    {
        return true;
    }


    
    /**
     * Check whether or not this algae is ready to undergo reproduction at this step.
     * New births will be made into free adjacent locations.
     * @param newAlgae A list to return newly born algae.
     */
    private void giveBirth(List<Animal> newAlgae)
    {
        // New algae are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Algae young = new Algae(false, field, loc);
            newAlgae.add(young);
        }
    }
    
    
    
}
    

