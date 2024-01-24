import java.util.List;
import java.util.Random;

/**
 * A simple model of a Fly.
 * Flies age, move, breed, and die.
 *
 * @version 2020.02.20 (2)
 */
public class Fly extends Animal
{
    // The age at which a Fly can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Fly can live.
    private static final int MAX_AGE = 40;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single flower. In effect, this is the
    // number of steps a fly can go before it has to eat again.
    private static final int FLOWER_FOOD_VALUE = 9;
    // The likelihood of a Fly breeding.
    private static final double BREEDING_PROBABILITY = 0.8;
    // The food that the fly eats
    private static final Class FOOD_TYPE = Flower.class;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    private Animal animal;
    
    /**
     * Create a Fly. A Bat can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Fly will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fly(boolean randomAge, Field field, Location location)
    {
        super(randomAge, MAX_AGE, field, location, FLOWER_FOOD_VALUE, MAX_LITTER_SIZE);
    }
    
    /**
     * Return The age at which a Fly starts to breed.
     * @return The age at which a Fly starts to breed.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the maximum number of births.
     * @return the maximum number of births
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Return the likelihood of a Fly breeding.
     * @return the likelihood of a Fly breeding
     */
    public double getBreedingProbability()
    {
        return  BREEDING_PROBABILITY;
    }
    
    /**
     * Return the type of food the Fly eats
     * @return the type of food the Fly eats 
     */
    public Class getFoodType()
    {
        return FOOD_TYPE;
    }
    
    /**
     **This is what the fly does most of the time:it looks for
     * flowers. In the process, it might breed, die of hunger,
     * die of disease, or die of old age.
     * @param newFlies A list to return newly born flies.
     */
    @Override
    public void act(List<Actor> newFlies)
    {
        // age by time
        incrementAge();
        //get hungry
        incrementHunger();
        
        if(isActive()) {
            this.giveBirth(newFlies);
            this.findFood(FOOD_TYPE);
            
            // Decrement hunger if food is found
            if(this.getField().isFoodAdjacent(this)){
                this.decrementHunger();
            }
            
            // Move towards a source of food if found.
            Location newLocation = this.findFood(this.getFoodType());
            
            if(newLocation != null) {
                setLocation(newLocation);
                // Reproduce more if the weather is sunny
                if(this.getField().getWeather() == Weather.SUNNY){
                    this.giveBirth(newFlies);
                }
            }
            else {
                // Overcrowding.
                setDead();
            }
            
            // Fly dies when it has a disease
            if(this.getDisease() == Disease.RABIES || 
                this.getDisease() == Disease.FLU )
            {
                this.setDead();
            }
        }
    }
    
    /**
    * Check whether or not this fly is to give birth at this step.
    * New births will be made into free adjacent locations.
    * @param newFlies A list to return newly born flies.
    */
    @Override
    public void giveBirth(List<Actor> newFlies)
    {
        int births = this.breed();
        // List of free adjacent locations 
        List<Location> free = this.getField().getFreeAdjacentLocations(getLocation());
        for(int b = 0; b < births && free.size() > 0; b++) {  
            Location loc = free.remove(0);
            // Initialise a new fly
            Fly young = new Fly(false, this.getField(), loc);
            // Add it to the list of new Flies
            newFlies.add(young);
        }
    }
}
