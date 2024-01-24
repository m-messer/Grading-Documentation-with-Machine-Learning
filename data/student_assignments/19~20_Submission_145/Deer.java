import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a deer.
 * Deers age, move, breed, and die.
 * Deer's food supplies are: Grass and berries.
 * Deers can get infected.
 * 
 * @version 2020.02.22
 */
public class Deer extends Animal
{
    // Characteristics shared by all deers (class variables).

    // The age at which a deer can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a deer can live.
    private static final int MAX_AGE = 85;
    // The likelihood of a deer breeding.
    private static final double BREEDING_PROBABILITY = 0.06;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of prey. In effect, this is the
    // number of steps a deer can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 8;
    private static final int BERRY_FOOD_VALUE = 12;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The deer's age.
    private int age; 
    // The deer's food level, which is increased by eating grass and berries.
    private int foodLevel;

    /**
     * Create a new deer. A deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setHealth(10);    // from abstract Animal class
        if(randomAge) {
            age = rand.nextInt(MAX_AGE - 25);
            foodLevel = rand.nextInt(BERRY_FOOD_VALUE) + 15;
        }
        else {
            age = 0;
            foodLevel = 20;
        }
    }

    /**
     * This is what the deer does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * Deers only move during day time.
     * 
     * @param newDeers A list to return newly born deers.     
     * @param isDayTime A boolean which tells whether the current time is day or night.
     * @param weather A String to denote the weather.
     */
    public void act(List<Animal> newDeers, boolean isDayTime, String weather)
    {
        if(isDayTime){
            incrementAge();
            incrementHunger();
            if(isAlive()) {
                if(!weather.equals("rainy") || !weather.equals("foggy")){
                    giveBirth(newDeers);
                }
                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null) { 
                    // No food found - try to move to a free location.                 
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // If adjacent locations are not free
                if(newLocation == null){    
                    // Check if can step on a plant near by
                    newLocation = getField().freePlantAdjacentLocation(getLocation());
                    if(newLocation != null){
                        ((Plant)getField().getObjectAt(newLocation)).setDead();
                    }
                }
                
                // check what will the animal do, if have a new location, move. 
                // if not, setDead() because of overcrowd
                if(newLocation != null) {
                    setLocation(newLocation);
                    checkHealth();      // Check the state of health.
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
     * This could result in the deer's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Returns the age of the deer.
     * 
     * @return The age of the deer.
     */
    public int getAge()
    {
        return age;
    }

    /**
     * Make this deer more hungry. 
     * This could result in the deer's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for grass and berries adjacent to the current location.
     * Only the first live plant is eaten
     * 
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        while(it.hasNext() && foodLevel <= 11) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);

            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    if(grass.isInfected()){
                        this.decreaseHealth();
                    }
                    grass.setDead();
                    foodLevel += GRASS_FOOD_VALUE;
                    return where;
                }
            }
            else if(plant instanceof Berry) {
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
     * Check if the animal is of opposite gender.
     * 
     * @return True if have different gender, so it's able to breed
     *         false if not
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
     * Check whether or not this deer is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newDeers A list to return newly born deers.
     */
    private void giveBirth(List<Animal> newDeers)
    {
        // New deers are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if(checkGender()){
            int births = super.breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Deer young = new Deer(false, field, loc);
                newDeers.add(young);
            }
        }
    }

    /**
     * Returns the probability of a deer to breed.
     * 
     * @return double   The probability of a deer to breed.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Returns the max litter size of a deer.
     * 
     * @return int  The max litter size of a deer.
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the age at which a deer starts to breed.
     * 
     * @return int  The age at which a deer starts to breed.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * To check whether the animal is in good health.
     * If the health level is less than 6, means it 
     * will get infected. 
     * If the health level is less than or equal to 0
     * then the animal is in no condition to survive.
     * Therefore it is set dead.
     */
    protected void checkHealth()
    {
        if(getHealth() < 6){
            setInfected();
            if(getHealth() <= 0){
                setDead();
            }
        }
    }
}
