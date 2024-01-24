import java.util.List;
import java.util.Random;
import java.util.Iterator;

/** 
 * A model of a seal
 * Seal can swim, age, breed, eat, starve and die.
 *
 * @version 16.03.2022
 */
public class Seal extends Animal
{
    // Characteristics shared by all seal (class variables).

    // The age at which a seal can start to breed.
    private static final int BREEDING_AGE = 35;
    // The age to which a seal can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a seal breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The range in which a male and female need to be in order to breed. 
    private static final int BREEDING_RADIUS = 3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single fish. In effect, this is the
    // number of steps a seal can go before it has to eat again.
    private static final int FISH_FOOD_VALUE = 80;
    // The probability of spreading disease.
    private static final double SPREADING_PROBABILITY = 0.02;

    /**
     * Create a new seal. A seal may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the seal will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Seal(boolean randomAge, Field field, Location location, boolean randomGender)
    {
        super(field, location);
        int age;
        int foodLevel;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            setAge(age);
            foodLevel = rand.nextInt(FISH_FOOD_VALUE);
            setFoodLevel(foodLevel);
        }
        else {
            age = 0;
            setAge(age);
            foodLevel = FISH_FOOD_VALUE;
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
    * Whether the seal is active or not.
    * Returns true if the seal is active.
    */
     public boolean isActive()
    {
        return isAlive();
    }

    /**
     * This is what the seal does most of the time - it swims
     * around and hunts for fish. Sometimes it will breed or die of old age.
     * @param newFish A list to return newly born seal.
     * @param field The field currently occupied
     */
    public void act(List<Actor> newSeal)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newSeal);           
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // Try to move into a free location.
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
     * This is what the seal does at night.
     * It still ages and increments hunger.
     */
     public void nightAct(List<Actor> newSeals)
    {
        incrementAge();
        incrementHunger();
    }
    
    /**
     * Returns the maximum age of the seal.
     * @return Seal's maximum age.
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
    
    /**
     * Returns the breeding age of the seal.
     * @return Seal's breeding age.
     */
    public int getBreedingAge(){
        return BREEDING_AGE;
    }
    
    /**
     * Returns the breeding probability of the seal.
     * @return Seal's breeding probability.
     */
    public double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Returns the litter size of the seal.
     * @return Seal's maximum litter size.
     */
    public int getLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Look for fish adjacent to the current location.
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
            if(animal instanceof Fish) {
                Fish fish = (Fish) animal;
                if(fish.isAlive()) { 
                    fish.setDead();
                    setFoodLevel(FISH_FOOD_VALUE);
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * If the seal is next to an infected animal, disease will spread at a set
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
     * Check whether or not this seal is ready to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFish A list to return newly born seal.
     */
    private void giveBirth(List<Actor> newSeal)
    {
        // New seal are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Seal young = new Seal(false, field, loc, true);
            newSeal.add(young);
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
            if(animal instanceof Seal) {
                Seal seal = (Seal) animal;
                boolean gender = seal.isFemale();
                if (thisGender!= gender){
                    return true;
                }
            }
        }
        return false;
    }
}