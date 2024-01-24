import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Bat.
 * Bats age, move, eat Flies, and die.
 *
 * @version 2020.02.23 (2)
 */
public class Bat extends Animal
{
    // The age at which a Bat can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Bat can live.
    private static final int MAX_AGE = 150;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single fly. In effect, this is the
    // number of steps a bat can go before it has to eat again.
    private static final int FLY_FOOD_VALUE = 9;
    // The Bat's food level, which is increased by eating Flies.
    private int foodLevel;
    // The likelihood of a Bat breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The food that the Bat eats
    private static final Class FOOD_TYPE = Fly.class;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
  
    /**
     * Create a Bat. A Bat can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Bat will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bat(boolean randomAge, Field field, Location location)
    {
        super(randomAge, MAX_AGE, field, location, FLY_FOOD_VALUE, MAX_LITTER_SIZE);
    }

    /**
     * Return The age at which a Fly starts to breed.
     * @return The age at which a Bat starts to breed.
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
     * Return the likelihood of a Bat breeding.
     * @return the likelihood of a Bat breeding
     */
    public double getBreedingProbability()
    {
        return  BREEDING_PROBABILITY;
    }
    
    /**
     * Return the type of food the Bat eats
     * @return the type of food the Bat eats
     */
    public Class getFoodType()
    {
        return FOOD_TYPE;
    }
    
    /**
     * This is what the Bat does most of the time: it hunts for
     * Flys. In the process, it might breed, die of hunger,
     * die of a disease or die of old age.
     * @param newBats A list to return newly born Bats.
     */
    public void act(List<Actor> newBats)
    {
        // age by time 
        this.incrementAge();
        // get hungry
        this.incrementHunger();
        
        if(isActive()) {
            this.giveBirth(newBats); 
            this.findFood(FOOD_TYPE);
            
            // Move towards a source of food if found.
            Location newLocation = this.findFood(FOOD_TYPE);
            
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
                
                // Reproduce more when the weather is sunny
                if(this.getField().getWeather() == Weather.SUNNY){
                    this.giveBirth(newBats);
                }
                
                this.getField().isAnimalAdjacent(this);
                
                // Die when it gets a disease
                if(this.getDisease() == Disease.RABIES || 
                    this.getDisease() == Disease.FLU ){
                        this.setDead();
                }  
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }
 
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        int births = 0;
        if(this.canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }
     
    /**
    * Check whether or not this bat is to give birth at this step.
    * New births will be made into free adjacent locations.
    * @param newBats A list to return newly born bats.
    */
    @Override
    public void giveBirth(List<Actor> newBats)
    {
      int births = this.breed();
      // List of free adjacent locations
      List<Location> free = this.getField().getFreeAdjacentLocations(getLocation());
      for(int b = 0; b < births && free.size() > 0; b++) {  
        Location loc = free.remove(0);
        // Initialise a new bat
        Bat young = new Bat(false, this.getField(), loc);
        // Add it to the list of new bats
        newBats.add(young);
      }
    }
}
