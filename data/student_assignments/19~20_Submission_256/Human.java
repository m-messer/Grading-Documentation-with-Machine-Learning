import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 *  A simple model of a Human.
 *  Humans age, move, breed, eat, and die.
 * @version 2020.02.23
 */
public class Human extends Animal
{
    // The age at which a human can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a human can live.
    private static final int MAX_AGE = 150;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a human can go before it has to eat again.
    private static final int COW_FOOD_VALUE = 9;
    // The likelihood of a human breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food that the Human eats
    private static final Class FOOD_TYPE = Cow.class;
     
    /**
     * Create a Human. A Human can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * @param randomAge If true, the Human will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Human(boolean randomAge, Field field, Location location)
    {
        super(randomAge, MAX_AGE, field, location, COW_FOOD_VALUE, MAX_LITTER_SIZE);
    }
  
    /**
     * This is what the human does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newhumans A list to return newly born humans.
     */
    public void act(List<Actor> newHumans)
    {
        // age by time
        this.incrementAge();
        // get hungry
        this.incrementHunger();
        
        if(isActive()) {
            this.giveBirth(newHumans);
            
            // Move towards a source of food if found.
            Location newLocation = this.findFood(FOOD_TYPE);
            
            if(this.getField().getWeather() == Weather.SNOWY || 
                this.getField().getWeather() == Weather.RAINY)
            {
                this.incrementHunger();
            }
            
            // See if it was possible to move.
            if(this.isActive() && newLocation != null) {
                setLocation(newLocation);
                if(this.getField().getWeather() == Weather.SUNNY){
                    this.giveBirth(newHumans);
                }
                
                this.getField().isAnimalAdjacent(this);
                
                if(this.getDisease() == Disease.RABIES 
                    || this.getDisease() == Disease.FLU ){
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
     * Return The age at which a Fly starts to breed.
     * @return The age at which a Human starts to breed.
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
     * Return the likelihood of a Human breeding.
     * @return the likelihood of a Human breeding
     */
    public double getBreedingProbability()
    {
        return  BREEDING_PROBABILITY;
    }
    
    /**
     * Return the type of food the human eats
     * @return the type of food the Human eats
     */
    public Class getFoodType()
    {
        return FOOD_TYPE;
    }
    
    /**
     * Check whether or not this human is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newHuman A list to return newly born human.
     */
    @Override
    public void giveBirth(List<Actor> newHumans)
    {
      int births = this.breed();
      // Lists of free adjacent locations
      List<Location> free = this.getField().getFreeAdjacentLocations(getLocation());
      for(int b = 0; b < births && free.size() > 0; b++) {  
        Location loc = free.remove(0);
        // Initialise a new human
        Human young = new Human(false, this.getField(), loc);
        // Add it to the list of new humans
        newHumans.add(young);
      }
    }
}
