import java.util.Random;
import java.util.List;

/**
 * A simple model of a cow
 * Cows age, move, breed, eat, and die.
 *
 * @version 2020.02.23
 */
public class Cow extends Animal
{
    // The age at which a cow can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a cow can live.
    private static final int MAX_AGE = 40;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single grass. In effect, this is the
    // number of steps a cow can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 9;
    // The likelihood of a cow breeding.
    private static final double BREEDING_PROBABILITY = 1.0;
    // The food that the cow eats
    private static final Class FOOD_TYPE = Grass.class;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Instance variable of class animal
    private Animal animal;
    // Instance variable of class Disease
    private Disease disease;
    
    /**
     * Create a Cow. A Cow can be created as a new born (age zero
     * and not hungry) or with a random age location.
     * 
     * @param randomAge If true, the Cow will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cow(boolean randomAge, Field field, Location location)
    {
        super(randomAge, MAX_AGE, field, location, GRASS_FOOD_VALUE, MAX_LITTER_SIZE);
    }

    /**
     * Return The age at which a Cow starts to breed.
     * @return The age at which a Cow starts to breed.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the maximum number of births.
     * @return the maximum number of births.
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Return the likelihood of a Cow breeding.
     * @return the likelihood of a Cow breeding.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
     
    /**
     * Return the type of food the Cow eats.
     * @return the type of food the Cow eats. 
     */
    public Class getFoodType()
    {
        return FOOD_TYPE;
    }
    
    /**
     ** This is what the Cow does most of the time:it looks for
     * grass. In the process, it might breed, die of hunger, 
     * die of a disease or die of old age.
     * @param newCows A list to return newly born cows.
     */
    public void act(List<Actor> newCows)
    {
        // age by time
        incrementAge();
        //get hungry
        incrementHunger();
        
        if(isActive()) {
            this.giveBirth(newCows);
            this.findFood(FOOD_TYPE);
            
            // Decrement hunger if food is found
            if(this.getField().isFoodAdjacent(this)){
                this.decrementHunger();
            }
            
            // Move towards a source of food if found.
            Location newLocation = this.findFood(this.getFoodType());
            
            if(newLocation != null) {
                
                setLocation(newLocation);
                
                // Reproduce more when the weather is sunny
                if(this.getField().getWeather() == Weather.SUNNY){
                    this.giveBirth(newCows);
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
    * Check whether or not this cow is to give birth at this step.
    * New births will be made into free adjacent locations.
    * @param newCows A list to return newly born cows.
    */
    @Override
    public void giveBirth(List<Actor> newCows)
    {
      int births = this.breed();
      // List of free adjacent locations
      List<Location> free = this.getField().getFreeAdjacentLocations(getLocation());
      for(int b = 0; b < births && free.size() > 0; b++) {  
        Location loc = free.remove(0);
        // Initialise a new Cow
        Cow young = new Cow(false, this.getField(), loc);
        // Add it to the list of new Cows
        newCows.add(young);
      }
    }
}
