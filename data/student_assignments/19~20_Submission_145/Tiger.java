import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a tiger.
 * Tigers age, move, breed, eat, and die.
 * Tiger's food supplies are: Chicken, deer and bear.
 * Tigers can get infected.
 * 
 * @version 2020.02.14
 */
public class Tiger extends Animal
{
    // Characteristics shared by all tiger (class variables).

    // The age at which a tiger can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a tiger can live.
    private static final int MAX_AGE = 130;
    // The likelihood of a tiger breeding.
    private static final double BREEDING_PROBABILITY = 0.055;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of prey. In effect, this is the
    // number of steps a tiger can go before it has to eat again.

    private static final int DEER_FOOD_VALUE = 9;
    private static final int BEAR_FOOD_VALUE = 13;
    private static final int WEASEL_FOOD_VALUE = 7;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The tiger's age.
    private int age;
    // The tiger's food level, which is increased by eating chicken, deer and bear.
    private int foodLevel;

    /**
     * Create a tiger. A tiger can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the tiger will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tiger(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        setHealth(15);    // from abstract Animal class

        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(BEAR_FOOD_VALUE) + 5;
        }
        else {
            age = 0;
            foodLevel = 15;
        }
    }

    /**
     * This is what the tiger does most of the time: it hunts for
     * prey. In the process, it might breed, die of hunger,
     * or die of old age.
     * Tigers only move during day time
     *
     * @param newTigers A list to return newly born tigeres.
     * @param isDayTime A boolean which tells whether the current time is day or night
     * @param weather A String to keep track of the weather.
     */
    public void act(List<Animal> newTigers, boolean isDayTime, String weather)
    {
        if(isDayTime) {
            incrementAge();
            // foggy days tiger don't want to eat, so the hunger won't decrease
            if(!weather.equals("foggy")) {
                incrementHunger();
            }   

            if(isAlive()) {
                giveBirth(newTigers);            
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
                    setLocation(newLocation);   // Move to the new location.
                    checkHealth();      // Check state of health.
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
     * This could result in the tiger's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Returns the age of the tiger.
     *
     * @return The age of the tiger.
     */
    public int getAge()
    {
        return age;
    } 

    /**
     * Make this tiger more hungry. 
     * This could result in the tiger's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for deer, bears or chicken adjacent to the current location.
     * Only the first live food source for tiger is eaten.
     *
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        while(it.hasNext() && foodLevel <= 12) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);

            // Check if the animal is the food supply for tiger.
            if(animal instanceof Deer && rand.nextDouble() < 0.8){     // deer has 20% of chance to escape
                Deer deer = (Deer) animal;
                if(deer.isAlive()){
                    if(deer.isInfected()){
                        decreaseHealth();
                    }
                    deer.setDead();
                    foodLevel += DEER_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Bear && rand.nextDouble() < 0.7){     // bear has 30% of chance to escape
                Bear bear = (Bear) animal;
                if(bear.isAlive()){
                    if(bear.isInfected()){
                        decreaseHealth();
                    }
                    bear.setDead();
                    foodLevel += BEAR_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Weasel && foodLevel <= 4) {   // tigers are only going to eat weasel when foodLevel is under 4
                Weasel weasel = (Weasel) animal;
                if(weasel.isAlive()) { 
                    if(weasel.isInfected()){
                        decreaseHealth();
                    }
                    weasel.setDead();
                    foodLevel += WEASEL_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this tiger is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newTigers A list to return newly born tigeres.
     */
    private void giveBirth(List<Animal> newTigers)
    {
        // New tigers are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = super.breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Tiger young = new Tiger(false, field, loc);
            newTigers.add(young);
        }
    }

    /**
     * Returns the probability of a tiger to breed.
     * 
     * @return the probability of a tiger to breed.
     */
    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Returns the max litter size of a tiger.
     * 
     * @return the max litter size of a tiger.
     */
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Returns the age at which a tiger starts to breed.
     * 
     * @return the age at which a tiger starts to breed.
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
