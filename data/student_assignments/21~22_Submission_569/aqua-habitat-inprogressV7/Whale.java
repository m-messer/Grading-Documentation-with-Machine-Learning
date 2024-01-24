import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A model of a Whale
 * Whales eat, move, starve, breed and die.
 *
 * @version (a version number or a date)
 */
public class Whale extends Animal
{
    
    // Characteristics shared by all whales (class variables).
    
    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 40;
    // The age to which a whale can live.
    private static final int MAX_AGE = 300;
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.30;
    // The range in which a male and female need to be in order to breed. 
    private static final int BREEDING_RADIUS = 3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single seal. In effect, this is the
    // number of steps a whale can go before it has to eat again.
    private static final int SEAL_FOOD_VALUE = 70;
    // The probability of spreading disease.
    private static final double SPREADING_PROBABILITY = 0.05;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The whale's age.
    //private int age;
    // The whale's food level, which is increased by eating seal.
    private int foodLevel;
    //The whale's gender
    private boolean gender;

    /**
     * Create a whale. A whale can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the whale will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomGender If true the whale will be given a random gender.
     * 
     */
    public Whale(boolean randomAge, Field field, Location location , boolean randomGender)
    {
        super(field, location);
        int age;
        int foodLevel;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            setAge(age);
            foodLevel = rand.nextInt(SEAL_FOOD_VALUE);
            setFoodLevel(foodLevel);
        }
        else {
            age = 0;
            setAge(age);
            foodLevel = SEAL_FOOD_VALUE;
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
     * This is what the animal does at night.
     * It still ages and increments hunger.
     */
     public void nightAct(List<Animal> newWhales)
    {
        incrementAge();
        incrementHunger();
    }

    /**
     * This is what the whale does most of the time: it hunts for
     * seal. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSharks A list to return newly born whale.
     */
    public void act(List<Animal> newWhale)
    {
        incrementAge();
        incrementHunger();
        
        if(isAlive()) {
                giveBirth(newWhale);            
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
                
                if (isInfected()){
                    int foodLevel = getFoodLevel();
                    foodLevel = foodLevel-3;
                    setFoodLevel(foodLevel);
                }
            
        }
        
        
    }
    
    
    
    public int getMaxAge(){
        return MAX_AGE;
    }
    
    public int getBreedingAge(){
        return BREEDING_AGE;
    }
    
    
    /**
     * Look for seal adjacent to the current location.
     * Only the first live seal is eaten.
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
            if(animal instanceof Seal) {
                Seal seal = (Seal) animal;
                if(seal.isAlive()) { 
                    seal.setDead();
                    foodLevel = SEAL_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * If the animal is next to an infected animal, disease will spread at a set
     * probability.
     * 
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
     * Check whether or not this whale is to give birth at this step.
     * A male and female need to be present within the set radius for this to be possible.
     * New births will be made into free adjacent locations.
     * @param newWhales A list to return newly born whale.
     */
    private void giveBirth(List<Animal> newWhales)
    {
        // New whale are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = this.breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Whale young = new Whale(false, field, loc, true);
            
            newWhales.add(young);
        }
    }
    
    public double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }
    
    public int getLitterSize()
    {
        return MAX_LITTER_SIZE;
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
            if(animal instanceof Whale) {
                Whale whale = (Whale) animal;
                boolean gender = whale.isFemale();
                if (thisGender!= gender){
                    return true;
                }
            }
        }
        return false;
    }
    

    
    
}
