import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a bear.
 * Bears age, move, eat and die.
 * Bear's food sources are: Deer, chicken.
 * Bears can get infected.
 * 
 * @version 2020.02.22
 */
public class Bear extends Animal
{
    // Characteristics shared by all bears (class variables).

    // The age at which a bear can start to breed.
    private static final int BREEDING_AGE = 12;
    // The age to which a bear can live.
    private static final int MAX_AGE = 105;
    // The likelihood of a bear breeding.
    private static final double BREEDING_PROBABILITY = 0.05;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of prey. In effect, this is the
    // number of steps a bear can go before it has to eat again.
    private static final int DEER_FOOD_VALUE = 15;
    private static final int CHICKEN_FOOD_VALUE = 8;
    private static final int BERRY_FOOD_VALUE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The bear's age.
    private int age;
    // The bear's food level, which is increased by eating chicken and deer.
    private int foodLevel;

    /**
     * Create a bear. A bear can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the bear will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bear(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setHealth(15);    // from abstract Animal class

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(DEER_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = DEER_FOOD_VALUE;
        }
    }

    /**
     * This is what the bear does most of the time: it hunts for
     * deer and chicken. In the process, it might breed, die of hunger,
     * or die of old age.
     * Bears only move during day time.
     * Bear does not move when it is cold.
     * 
     * @param newBears A list to return newly born bears.
     * @param isDayTime A boolean which tells whether the current time is day or night.
     * @param weather A String to denote the weather.
     */
    public void act(List<Animal> newBears, boolean isDayTime, String weather)
    {
        if(isDayTime && !weather.equals("cold")) {

            incrementAge();
            incrementHunger();
            if(isAlive()) {
                giveBirth(newBears);     

                // Move towards a source of food if found.
                Location newLocation = findFood();

                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());

                    // If adjacent locations are not free
                    if(newLocation == null){    
                        // Check if the bear can step on a plant.
                        newLocation = getField().freePlantAdjacentLocation(getLocation());

                        if(newLocation != null){

                            ((Plant)getField().getObjectAt(newLocation)).setDead();

                        }
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
     * Increase the age. This could result in the bear's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Returns the age of the bear.
     * 
     * @return The age of the bear.
     */
    public int getAge()
    {
        return age;
    }

    /**
     * Make this bear more hungry. 
     * This could result in the bear's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for deer and chicken adjacent to the current location.
     * Only the first live animal is eaten.
     * 
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        while(it.hasNext() && foodLevel <= 10) {
            Location where = it.next();
            Object object = field.getObjectAt(where);

            if(object instanceof Chicken && foodLevel < 7 && rand.nextDouble() < 0.9) { // chicken has 10% of chance to escape
                Chicken chicken = (Chicken) object;
                if(chicken.isAlive()) { 
                    if(chicken.isInfected()) {
                        decreaseHealth();
                    }
                    chicken.setDead();
                    foodLevel += CHICKEN_FOOD_VALUE;
                    return where;
                }
            }

            else if(object instanceof Deer && rand.nextDouble() < 0.4) {    // deer has 60% of chance to escape
                Deer deer = (Deer) object;
                if(deer.isAlive()){
                    if(deer.isInfected()) {
                        decreaseHealth();
                    }
                    deer.setDead();
                    foodLevel += DEER_FOOD_VALUE;
                    return where;
                }
            }

            else if(object instanceof Berry && foodLevel <= 3){ // bear's healthLevel will not directly infected by berries
                Berry berry = (Berry) object;
                if(berry.isAlive()){ 
                    berry.setDead();
                    foodLevel += BERRY_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this bear is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newBears A list to return newly born bears.
     */
    private void giveBirth(List<Animal> newBears)
    {
        // New beares are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = super.breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Bear young = new Bear(false, field, loc);
            newBears.add(young);
        }
    }

    /**
     * Returns the probability of a bear to breed.
     * 
     * @return the probability of a bear to breed.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Returns the max litter size of a bear.
     * 
     * @return the max litter size of a bear.
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the age at which a bear starts to breed.
     * 
     * @return the age at which a bear starts to breed.
     */
    public int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * To check whether the animal is in good health.
     * If the health level is less than 8, means it 
     * will get infected. 
     * If the health level is less than or equal to 0
     * then the animal is in no condition to survive.
     * Therefore it is set dead.
     */
    protected void checkHealth()
    {
        if(getHealth() < 8){
            setInfected();
            if(getHealth() <= 0){
                setDead();
            }
        }
    }
}
