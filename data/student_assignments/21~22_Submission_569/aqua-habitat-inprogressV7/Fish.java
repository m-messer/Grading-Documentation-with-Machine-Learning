import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a fish.
 * Fish age, eat, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Fish extends Animal 
{
    // Characteristics shared by all fish (class variables).

    // The age at which a fish can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which a fish can live.
    private static final int MAX_AGE = 75;
    // The likelihood of a fish breeding.
    private static final double BREEDING_PROBABILITY = 0.52;
    // The range in which a male and female need to be in order to breed. 
    private static final int BREEDING_RADIUS = 6;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single algae. This is the number of steps a 
    // fish can go before it has to eat again.
    private static final int ALGAE_FOOD_VALUE = 60;
    // The probability of spreading disease.
    private static final double SPREADING_PROBABILITY = 0.05;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    
    /**
     * Create a new fish. A fish may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the fish will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fish(boolean randomAge, Field field, Location location, boolean randomGender)
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
     * This is what the fish does most of the time - it looks for algae and 
     * runs around. Sometimes it will breed or die of old age.
     * @param newFish A list to return newly born fish.
     */
    public void act(List<Animal> newFish)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newFish);            
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
     * This is what the animal does at night.
     * It still ages and increments hunger.
     */
     public void nightAct(List<Animal> newFish)
    {
        incrementAge();
        incrementHunger();
    }

    
    public int getMaxAge(){
        return MAX_AGE;
    }
    
    public int getBreedingAge(){
        return BREEDING_AGE;
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
                if(algae.isAlive()) { 
                    algae.setDead();
                    setFoodLevel(ALGAE_FOOD_VALUE);
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
     * Check whether or not this fish is ready to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFish A list to return newly born fish.
     */
    private void giveBirth(List<Animal> newFish)
    {
        // New fish are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Fish young = new Fish(false, field, loc, true);
            newFish.add(young);
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
            if(animal instanceof Fish) {
                Fish fish = (Fish) animal;
                boolean gender = fish.isFemale();
                if (thisGender!= gender){
                    return true;
                }
            }
        }
        return false;
    }


    
}
