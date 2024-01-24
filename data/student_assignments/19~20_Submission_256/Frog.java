import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a cow
 * Cows age, move, breed, eat, and die.
 *
 * @version 2020.02.23
 */
public class Frog extends Animal
{
    // The age at which a bat can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a bat can live.
    private static final int MAX_AGE = 150;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single fly. In effect, this is the
    // number of steps a bat can go before it has to eat again.
    private static final int FLY_FOOD_VALUE = 9;
    // The likelihood of a bat breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food that the Frog eats
    private static final Class FOOD_TYPE = Fly.class;
    
    /**
     * Create a Frog. A Frog can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Bat will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Frog(boolean randomAge, Field field, Location location)
    {
       super(randomAge, MAX_AGE, field, location, FLY_FOOD_VALUE, MAX_LITTER_SIZE);
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
     * Return the type of food the frig eats
     * @return the type of food the frog eats
     */
    public Class getFoodType()
    {
        return FOOD_TYPE;
    }
    
    /**
     * Return the likelihood of a Frog breeding.
     * @return the likelihood of a Frog breeding
     */
    public double getBreedingProbability()
    {
        return  BREEDING_PROBABILITY;
    }
    
    /**
     * This is what the frog does most of the time: it hunts for
     * flies. In the process, it might breed, die of hunger,
     * die of a disease or die of old age.
     * @param newFrogs A list to return newly born frogs.
     */
    @Override
    public void act(List<Actor> newFrogs)
    {
        // age by time
        incrementAge();
        // get hungry
        incrementHunger();
        
        if(isActive()) {
            this.giveBirth(newFrogs);
            this.findFood(FOOD_TYPE);
            
            // Decrement hunger when food is found
            if(this.getField().isFoodAdjacent(this))
            {
                this.decrementHunger();
            }
            
            // Try to move into a free location.
            Location newLocation = this.findFood(this.getFoodType());
            if(newLocation != null) {
                setLocation(newLocation);
                
                // Reproduce more when the weather is sunny
                if(this.getField().getWeather() == Weather.SUNNY){
                    this.giveBirth(newFrogs);
                }
                
                // Die when it gets a disease
                if(this.getField().isAnimalAdjacent(this) && this.getDisease() 
                    == Disease.RABIES || this.getDisease() == Disease.FLU){
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
    * Check whether or not this frog is to give birth at this step.
    * New births will be made into free adjacent locations.
    * @param newFrogs A list to return newly born frogs.
    */
    @Override
    public void giveBirth(List<Actor> newFrogs)
    {
      int births = this.breed();
      // List of free adjacent locations
      List<Location> free = this.getField().getFreeAdjacentLocations(getLocation());
      for(int b = 0; b < births && free.size() > 0; b++) {  
        Location loc = free.remove(0);
        // initialise a new frog
        Frog young = new Frog(false, this.getField(), loc);
        // Add it to the list of new frogs
        newFrogs.add(young);
       }
    }
}

