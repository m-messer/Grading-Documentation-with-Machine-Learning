import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a shark.
 * Sharks age, move, eat fish, and die.
 *
 * @version 16.03.2022 (2)
 */
public class Shark extends Animal
{
    // Characteristics shared by all sharks (class variables).
    
    // The age at which a shark can start to breed.
    private static final int BREEDING_AGE = 37;
    // The age to which a shark can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a shark breeding.
    private static final double BREEDING_PROBABILITY = 0.255;
    // The range in which a male and female need to be in order to breed. 
    private static final int BREEDING_RADIUS = 3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single fish and seahorse. In effect, this is the
    // number of steps a shark can go before it has to eat again.
    private static final int PREY_FOOD_VALUE = 70;
    // The probability of spreading disease.
    private static final double SPREADING_PROBABILITY = 0.02;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a shark. A shark can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the shark will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Shark(boolean randomAge, Field field, Location location , boolean randomGender)
    {
        super(field, location);
        int age;
        int foodLevel;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            setAge(age);
            foodLevel = rand.nextInt(PREY_FOOD_VALUE);
            setFoodLevel(foodLevel);
        }
        else {
            age = 0;
            setAge(age);
            foodLevel = PREY_FOOD_VALUE;
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
    * Whether the shark is active or not.
    * Returns true if the shark is active.
    */
     public boolean isActive()
    {
        return isAlive();
    }
    
    /**
     * This is what the shark does most of the time: it hunts for
     * fish. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSharks A list to return newly born sharks.
     */
    public void act(List<Actor> newSharks)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSharks);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
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
     * This is what the shark does at night.
     * It still ages and increments hunger.
     */
     public void nightAct(List<Actor> newSharks)
    {
        incrementAge();
        incrementHunger();
    }
    
    /**
     * Returns the maximum age of the shark.
     * @return Shark's maximum age.
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
    
    /**
     * Returns the breeding age of the shark.
     * @return Shark's breeding age.
     */
    public int getBreedingAge(){
        return BREEDING_AGE;
    }
    
    /**
     * Returns the breeding probability of the shark.
     * @return Shark's breeding probability.
     */
    public double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns the litter size of the shark.
     * @return Shark's maximum litter size.
     */
    public int getLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Look for fish and seahorse adjacent to the current location.
     * Only the first live fish is eaten.
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
            if (animal instanceof Seahorse) {
                Seahorse seahorse = (Seahorse) animal;
                if(seahorse.isAlive()) { 
                    seahorse.setDead();
                    setFoodLevel(PREY_FOOD_VALUE);
                    return where;
                }
            }
            else if(animal instanceof Fish) {
                Fish fish = (Fish) animal;
                if(fish.isAlive()) { 
                    fish.setDead();
                    setFoodLevel(PREY_FOOD_VALUE);
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * If the shark is next to an infected animal, disease will spread at a set
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
     * Check whether or not this shark is to give birth at this step.
     * A male and female need to be present within the set radius for this to be possible.
     * New births will be made into free adjacent locations.
     * @param newSharks A list to return newly born sharks.
     */
    private void giveBirth(List<Actor> newSharks)
    {
        // New sharks are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Shark young = new Shark(false, field, loc, true);
            newSharks.add(young);
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
            if(animal instanceof Shark) {
                Shark shark = (Shark) animal;
                boolean gender = shark.isFemale();
                if (thisGender!= gender){
                    return true;
                }
            }
        }
        return false;
    }
}