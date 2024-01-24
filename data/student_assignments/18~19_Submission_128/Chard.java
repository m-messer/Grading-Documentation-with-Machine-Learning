import java.util.List; 

/**
 * This class is a model for a type of plant called Chard. 
 * They get eaten by the worms.
 * Chards grow, breed but do not move around.
 *
 * @version 2019.02.22
 */
public class Chard extends Organism
{
    //max age that can be reached by the chards
    private static final int MAX_AGE = 10;
    //maturity age of the chards so they can be used as food source
    private static final int MAT_AGE = 2; 
    //max number of seeds that can be given out by a chard
    private static final int MAX_SEEDS = 6; 
  
    //stores the growth rate of the chards
    private static int growthRate;

    /**
     * Create a new chard. Chards may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the chards will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Chard(boolean random, Field field, Location location)
    {
        super(field, location); 
        growthRate = 1; 
        if(!random){ 
            age = 0;
        }
        else{ 
            age =getRandomAge();
        }
    }
 
    /**
     * Make this chard act.They age and give birth to new chards.
     * Chards do not move.
     * @param newChards A list to receive newly born chards.
     */
    public void act(List<Organism> newChards){
        incrementAge(); 
        if(isAlive()){
            giveBirth(newChards) ; 
        }
    }

    /**
     * Increments age if not reached max age
     */
    public void incrementAge(){
        age++; 
        if(age > getMaxAge()){
            setDead();
        }
    }
        
    /**
     * Changes the value of the growth rate according to the weather condition
     * It is static as it is being accessed from the Simulator class in
     * simulateOneStep() method.
     * 
     * @param newGrowthRate It is the rate of the growth of the chards
     */
    public static void changeGrowthRate(int newGrowthRate){
        growthRate = newGrowthRate; 
    }

    /**
     * Check whether or not this chard is to give birth at this step.
     * New chards will be made into free adjacent locations.
     * @param newChards A list to return newly born chards.
     */
    private void giveBirth(List<Organism> newChards){
        // New owles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Chard young = new Chard(false, field, loc);
            newChards.add(young);
        }
    }

    /**
     * This checks if the chard has reached the maturity age yet.
     * If it has, the chard breeds.
     * @return the number of the seeds given out by the chard
     */
    public int breed(){
        int seeds = 0 ; 
        if(isMature()){
            seeds = (rand.nextInt(MAX_SEEDS) + 1) * growthRate ; 
        }
        return seeds; 
    }

    /**
     * A chard can breed if it has reached the maturity age.
     * Also gets if the plant is mature enough to give out seeds.
     * @return true if the chard can breed, false otherwise.
     */
    public boolean isMature(){ 
        return age >= getMaxAge(); 
    }
    
    /**
     * @return the maximum age of chard in which it can live
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
}
