import java.util.List;
import java.util.Iterator;

/**
/**
 * This is a model for chickens. This class has two subclasses of hen and rooster.
 *
 * @version 22.02.19
 */
public abstract class Chicken extends Organism 
{
    // Characteristics shared by all chickens (class variables).

    // The age at which a chicken can start to breed.
    protected static final int BREEDING_AGE = 1;
    // The age to which a chicken can live.
    private static final int MAX_AGE = 20;
    //probability of chicken getting infected with Salmonellosis (a disease)
    protected static final double SALMONELLOSIS_PROBABILITY = 0.5; 
    
    // Individual characteristics (instance fields).
   
    //whether the chicken is infected or not
    private boolean isSick; 
    
    /**
     * Create a new chicken. A chicken may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the chicken will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Chicken(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        //generating a random number for the probability of having the disease
        double prob = rand.nextDouble(); 
        
        if(randomAge) {
            age = getRandomAge();
        }
        if(prob>= SALMONELLOSIS_PROBABILITY){
            isSick = true; 
            //If the chicken has not reached the breeding age and has the disease, they die
            if(age<BREEDING_AGE){
                setDead();
            }
            else{ 
                shortenLifeSpan();
            }
        }
        else{
            //no disease
            isSick = false; 
        }
    }
    
    /**
     * This is what the chicken does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newChickens A list to return newly born chicken.
     */
    public void act(List<Organism> newChicken)
    {
        incrementAge();
    }

    /**
     * Increase the age by one
     */
    protected void incrementAge()
    {
        age++;
    }
    
    /**
     * Decrease the lifespan of the organism by 5
     */
    protected void shortenLifeSpan(){
        age += 5; 
    }
   
    /**
     * @return true if the chicken is sick or false if not
     */
    public boolean getIsSick(){
        return isSick; 
    }

}
