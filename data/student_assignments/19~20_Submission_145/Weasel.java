import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a weasel.
 * Weasel age, move, eat and die.
 * Weasel's food supplies are: Chicken
 * Weasels can get infected.
 * 
 * @version 2020.02.22
 */
public class Weasel extends Animal
{
    // Characteristics shared by all weaseles (class variables).

    // The age at which a weasel can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a weasel can live.
    private static final int MAX_AGE = 90;
    // The likelihood of a weasel breeding.
    private static final double BREEDING_PROBABILITY = 0.045;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of prey. In effect, this is the
    // number of steps a weasel can go before it has to eat again.
    private static final int CHICKEN_FOOD_VALUE = 15;
    private static final int BERRY_FOOD_VALUE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The weasel's age.
    private int age;
    // The weasel's food level, which is increased by eating chicken and berries.
    private int foodLevel;

    /**
     * Create a weasel. A weasel can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the weasel will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Weasel(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setHealth(6);    // from abstract Animal class

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(CHICKEN_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = CHICKEN_FOOD_VALUE;
        }
    }

    /**
     * This is what the weasel does most of the time: it hunts for
     * prey. In the process, it might breed, die of hunger,
     * or die of old age.
     * Weasel are only moving at night time
     * 
     * @param newWeasels A list to return newly born weasels.
     * @param isDayTime A boolean which tells whether the current time is day or night.
     * @param weather A String to denote the weather.
     * 
     */
    public void act(List<Animal> newWeasels, boolean isDayTime, String weather)
    {
        if(!isDayTime) {
            incrementAge();
            incrementHunger();

            if(isAlive()) {
                giveBirth(newWeasels); 
                // If the weather is sunny the weasels reproduce twice.
                if(weather.equals("sunny")) {
                    giveBirth(newWeasels); 
                }

                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());

                    // if adjacent locations are not free
                    if(newLocation == null){    
                        // Check if the weasel can step on a plant.
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
     * Increase the age. 
     * This could result in the weasel's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Returns the age of the weasel.
     * 
     * @return int  The age of the weasel.
     */
    public int getAge()
    {
        return age;
    }

    /**
     * Make this weasel more hungry. 
     * This could result in the weasel's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first live animal is eaten.
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
            Object food = field.getObjectAt(where);
            
            if(food instanceof Chicken && Math.random() < 0.75) {
                Chicken chicken = (Chicken) food;
                if(chicken.isAlive()){
                    if(chicken.isInfected()){
                        decreaseHealth();
                    }
                    chicken.setDead();
                    foodLevel += CHICKEN_FOOD_VALUE;
                    return where;
                }
            }
            
            // Eating infected berry is not going to affect the weasel's health.
            else if(food instanceof Berry && foodLevel <= 3) {
                Berry berry = (Berry) food;
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
     * Check whether or not this weasel is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newWeasels A list to return newly born weaseles.
     */
    private void giveBirth(List<Animal> newWeasels)
    {
        // New weaseles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = super.breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Weasel young = new Weasel(false, field, loc);
            newWeasels.add(young);
        }
    }

    /**
     * Returns the probability of a weasel to breed.
     * 
     * @return double   the probability of a weasel to breed.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Returns the max litter weasel of a deer.
     * 
     * @return int  the max litter weasel of a deer.
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the age at which a weasel starts to breed.
     * 
     * @return int  the age at which a weasel starts to breed.
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
        if(getHealth() < 2){
            setInfected();
            if(getHealth() <= 0){
                setDead();
            }
        }
    }
}
