import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a Seahorse
 * Seahorse age, swim, eat, and die
 *
 * @version 16.03.2022
 */
public class Seahorse extends Animal 
{
      // Characteristics shared by all seahorse (class variables).

    // The age at which a seahorse can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a seahorse can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a seahorse breeding.
    private static final double BREEDING_PROBABILITY = 0.355;
    // The range in which a male and female need to be in order to breed. 
    private static final int BREEDING_RADIUS = 4;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single algae. This is the number of steps a 
    // seahorse can go before it has to eat again.
    private static final int ALGAE_FOOD_VALUE = 55;
    // The probability of spreading disease.
    private static final double SPREADING_PROBABILITY = 0.02;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new seahorse. A seahorse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the seahorse will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Seahorse(boolean randomAge, Field field, Location location, boolean randomGender)
    {
        super(field, location);
        int age;
        int foodLevel;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            setAge(age);
            foodLevel = rand.nextInt(ALGAE_FOOD_VALUE);
            setFoodLevel(foodLevel);
        }
        else {
            age = 0;
            setAge(age);
            foodLevel = ALGAE_FOOD_VALUE;
            setFoodLevel(foodLevel);
        }
        
        if(randomGender) {
            Random random = new Random();
            boolean gender = random.nextBoolean(); // random true or false
            if (!gender){ // If it is false the gender is male otherwise it is female by default
                this.setMale();
            }
        }
    }
    
    /**
    * Whether the seahorse is active or not.
    * Returns true if the seahorse is active.
    */
     public boolean isActive()
    {
        return isAlive();
    }

    /**
     * This is what the seahorse does most of the time - it looks for algae and 
     * runs around. Sometimes it will breed or die of old age.
     * @param newFish A list to return newly born seahorse.
     */
    public void act(List<Actor> newSeahorse)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSeahorse);            
            // Try to move to a food source.
            Location newLocation = findFood();
            if(newLocation == null){
                //No food is found therefore try to move to a free location
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            //See if it is possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
                spreadDisease();
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
        
        if (isInfected()){
            int foodLevel = getFoodLevel();
            foodLevel = foodLevel-3;
            setFoodLevel(foodLevel);
        }           
    }
    
    /**
     * This is what the seahorse does at night.
     * It still ages and increments hunger.
     */
     public void nightAct(List<Actor> newSeahorse)
    {
        incrementAge();
        incrementHunger();
    }
    
    /**
     * Returns the maximum age of the seahorse.
     * @return Seahorse's maximum age.
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
    
    /**
     * Returns the breeding age of the seahorse.
     * @return Seahorse's breeding age.
     */
    public int getBreedingAge(){
        return BREEDING_AGE;
    }
    
    /**
     * Returns the breeding probability of the seahorse.
     * @return Seahorse's breeding probability.
     */
    public double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns the litter size of the seahorse.
     * @return Seahorse's maximum litter size.
     */
    public int getLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Look for algae adjacent to the current location.
     * Only the first live algae is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Algae) {
                Algae algae = (Algae) animal;
                if(algae.isActive()) { 
                    algae.setDead();
                    setFoodLevel(ALGAE_FOOD_VALUE);
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * If the seahorse is next to an infected animal, disease will spread at a set
     * probability.
     */
     private void spreadDisease()
    {
        if(this.isInfected()){
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if(animal instanceof Animal) {
                   Animal targetAnimal = (Animal) animal;
                   if(!targetAnimal.isInfected() && rand.nextDouble() <= SPREADING_PROBABILITY){
                       targetAnimal.setDisease();
                    }
                }
            }
        }
    }
    
    /**
     * Check whether or not this seahorse is ready to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeahorse A list to return newly born seahorse.
     */
    private void giveBirth(List<Actor> newSeahorse)
    {
        // New fish are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Seahorse young = new Seahorse(false, field, loc, true);
            newSeahorse.add(young);
        }
    }
   
    /**
     * Look for the opposite sex adjacent to the current location.
     * First mate found will be chosen.
     * @return If mate was found, or false if it wasn't.
     */
    protected boolean checkMate()
    {
        Field field = getField();
        List<Location> radius = field.radiusLocations(getLocation(),BREEDING_RADIUS);
        Iterator<Location> it = radius.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            boolean thisGender = this.isFemale();
            if(animal instanceof Seahorse) {
                Seahorse seahorse = (Seahorse) animal;
                boolean gender = seahorse.isFemale();
                if (thisGender!= gender){
                    return true;
                }
            }
        }
        return false;
    }
}
