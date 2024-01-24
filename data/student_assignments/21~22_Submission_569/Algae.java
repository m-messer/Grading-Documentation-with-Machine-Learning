import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of Algae
 * Algae gets eaten by fish, it breeds, ages, dies and it does not move.
 *
 * @version 16.03.2022
 */
public class Algae implements Actor
{
    // Characteristics shared by all Algae (class variables).

    // The age at which an algae can start to reproduce.
    private static final int BREEDING_AGE = 10;
    // The age to which an Algae can live.
    private static final int MAX_AGE = 90;
    // The likelihood of a algae reproducing.
    private static final double BREEDING_PROBABILITY = 0.06;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    //Whether the algae is alive or not.
    private boolean alive;
    //The age of the Algae.
    private int age;
    // The algae's field.
    private Field field;
    // The animal's position in the field.
    private Location location; 
    /**
     * Create a new algae. An algae may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the algae will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Algae(Field field, Location location)
    {
        age = 0;
        alive = true;
        this.field = field;
        setLocation(location);     
    }
    
    /**
     * This is what the algae does most of the time - it does not move. Sometimes it will breed or die of old age.
     * @param newFish A list to return newly born algae.
     */
    public void act(List<Actor> newAlgae)
    {
        incrementAge();
        if(isActive()) {
            giveBirth(newAlgae);            
        }
    }
    
    /**
     * What the algae does at night
     * @param newAlgae A list to receive newly born algae.
     */
    public void nightAct(List<Actor> newAlgae)
    {
        incrementAge();
    }
    
    /**
    * Whether the algae is active or not.
    * Returns true if the algae is active.
    */
    public boolean isActive()
    {
       return alive; 
    }
    
    /**
     * Set the algae to dead. 
     */
    public void setDead()
    {
       alive = false;
       if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Increase the age. This could result in the algae's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    private int reproduce()
    {
       int births = 0;
       if(rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births; 
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Check whether or not this algae is ready to undergo reproduction at this step.
     * New births will be made into free adjacent locations.
     * @param newAlgae A list to return newly born algae.
     */
    private void giveBirth(List<Actor> newAlgae)
    {
        // New algae are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = reproduce();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Algae young = new Algae(field, loc);
            newAlgae.add(young);
        }
    }
}
    

