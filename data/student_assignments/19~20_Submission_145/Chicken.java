import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a chickens.
 * Chickens age, move, breed, and die.
 * 
 * @version 2020.02.22
 */
public class Chicken extends Animal
{
    // Characteristics shared by all chickens (class variables).

    // The age at which a chicken can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a chicken can live.
    private static final int MAX_AGE = 55;
    // The likelihood of a chicken breeding.
    private static final double BREEDING_PROBABILITY = 0.05;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single chicken. In effect, this is the
    // number of steps a chicken can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 10;
    private static final int BERRY_FOOD_VALUE = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The chicken's age.
    private int age;
    // The chicken's food level, which is increased by eating grass and berries.
    private int foodLevel;

    /**
     * Create a new chicken. A chicken may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the chicken will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Chicken(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setHealth(8);    // from abstract Animal class
        if(randomAge) {
            age = rand.nextInt(MAX_AGE - 15);
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE) + 5;
        }
        else {
            age = 0;
            foodLevel = 15;
        }
    }

    /**
     * This is what the chicken does most of the time - it runs 
     * around and hunts for prey. Sometimes it will breed or die of old age or die of hunger.
     * Chickens move at night time
     * 
     * @param newChickens A list to return newly born chickens.
     * @param isDayTime A boolean which tells whether the current time is day or night.
     * @param weather A String to denote the weather.
     */
    public void act(List<Animal> newChickens, boolean isDayTime, String weather)
    {
        if(!isDayTime) {
            incrementAge();
            incrementHunger();
            if(isAlive()) {
                giveBirth(newChickens);
                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // If adjacent locations are not free
                if(newLocation == null) {    
                    // Check if can step on a plant.
                    newLocation = getField().freePlantAdjacentLocation(getLocation());
                    if(newLocation != null) {
                        
                        ((Plant)getField().getObjectAt(newLocation)).setDead();
                        
                    }
                }
                
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                    checkHealth();      // then check the health condition, dead or not
                }
                else {
                    // Overcrowding.
                    setDead();
                }
            }
        }

    }

    /**
     * Increase the age.
     * This could result in the chicken's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Returns the age of the chicken.
     * 
     * @return int  The age of the chicken.
     */
    public int getAge()
    {
        return age;
    }

    /**
     * Make this chicken more hungry. 
     * This could result in the chicken's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for grass or berries adjacent to the current location.
     * Only the first live food source for chicken is eaten.
     * 
     * @return Location     Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        while(it.hasNext() && foodLevel <= 10) {    
            Location where = it.next();
            Object plant = field.getObjectAt(where);

            // check if the plant is the food supply for chicken
            if(plant instanceof Grass){
                Grass grass = (Grass) plant;
                if(grass.isAlive()){
                    if(grass.isInfected()){
                        decreaseHealth();
                    }
                    grass.setDead();
                    foodLevel += GRASS_FOOD_VALUE;
                    return where;
                }
            }
            else if(plant instanceof Berry && foodLevel < 4){
                Berry berry = (Berry) plant;
                if(berry.isAlive()){
                    if(berry.isInfected()){
                        decreaseHealth();
                    }
                    berry.setDead();
                    foodLevel += BERRY_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this chicken is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newChickens A list to return newly born chickens.
     */
    private void giveBirth(List<Animal> newChickens)
    {
        // New chickens are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());

        // check if the animal meets the condition for breeding
        if(checkGender()){
            int births = super.breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Chicken young = new Chicken(false, field, loc);
                newChickens.add(young);
            }
        }
    }

    /**
     * Check if the animal is of opposite gender
     * 
     * @return boolean   True if have different gender, so it's able to breed
     *                   false if not
     */
    private boolean checkGender()
    {
        Field field = getField();
        List<Location> occupied = field.getOccupiedAdjacentLocations(getLocation(), this);
        for(Location next : occupied){
            if(this.getGender() != ((Animal)field.getObjectAt(next)).getGender()){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the probability of a chicken to breed.
     * 
     * @return double   The probability of a chicken to breed.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Returns the max litter size of a chicken.
     * 
     * @return int  The max litter size of a chicken.
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the age at which a chicken starts to breed.
     * 
     * @return int  The age at which a chicken starts to breed.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * To check whether the animal is in good health.
     * If the health level is less than 5, means it 
     * will get infected. 
     * If the health level is less than or equal to 0
     * then the animal is in no condition to survive.
     * Therefore it is set dead.
     */
    protected void checkHealth()
    {
        if(getHealth() < 3){
            setInfected();
            if(getHealth() <= 0){
                setDead();
            }
        }
    }
}
