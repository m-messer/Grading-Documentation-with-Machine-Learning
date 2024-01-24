import java.util.List;

/**
 * This class is a model for rooster who are a pray for the snakes.
 * Rooster reproduce with hen to give birth to new chickens. 
 * They age, move, get sick and die.
 *
 * @version 22.02.19 
 */
public class Rooster extends Chicken
{
    //the max age that can be reached by a rooster
    private static final int MAX_AGE = 20; 
    //stores whether the rooster is sick or not
    private boolean isSick; 
    
    /**
     * Create a rooster. A rooster may be created with age
     * zero (a new born) or with a random age.
     * It also determines if the rooster is sick or not.
     * 
     * @param randomAge If true, the rooster will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rooster(boolean randomAge, Field field, Location location)
    {
        // gets the random age, field and lication from the superclass
        super(randomAge, field, location); 
        age = super.age; 
        
        if(randomAge != true){
            age = 0 ; 
        }
        //inherits whether the chicken is sick or not from the superclass 
        // chicken where chickens are randomly infected with disease 
        isSick = super.getIsSick() ;
        
    }
    
    /**
     * This is what the rooster does most of the time - it runs 
     * around. It will get sick or die of old age.
     * @param newRooster A list to return newly born roosters.
     */
    public void act(List<Organism> newRooster)
    {
        incrementAge();
        if(isAlive()) {        
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
     * @return the maximum age of rooster in which it can live
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
}
