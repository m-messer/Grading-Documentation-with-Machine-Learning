import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a tiger.
 * Tigers age, move, reproduce, eats deers and capybaras, and die.
 *
 * @version 2021.3.3
 */
public class Tiger extends Animal
{
    // Characteristics shared by all tigers (class variables).

    // The age at which a tiger can start to breed.
    private static final int BREEDING_AGE = 40;
    // The age to which a tiger can live.
    private static final int MAX_AGE = 130;
    // The likelihood of a tiger breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value a tiger gives when eaten.
    // Currently, tigers are predators, hence it will not be eaten.
    private static final int FOOD_VALUE = 0;
    // The energy level at which the tiger gets hungry.
    private static final int HUNGRY = 40;
    // Indicates if the tiger sleeps at nighttime.
    private static final boolean CAN_SLEEP = false;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a tiger. A tiger can be created as a new born (age
     * zero and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the tiger will have random age 
     * and hunger level.
     * @param isFemale A boolean indicating if the tiger is female.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tiger(boolean randomAge, boolean isFemale, Field field, Location location)
    {
        super(randomAge, isFemale, field, location);
    }

    /**
     * Check whether the given animal is a male tiger.
     * If so, then give birth to a random number of offsprings 
     * in the free locations surrounding it, if there are any.
     * @param newTigers A list to add all the new offspring.
     * @param animal An animal that could be a male tiger.
     */
    protected void giveBirth(List<Animal> newTigers, Object animal)
    {
        if (animal instanceof Tiger) {
            Tiger tiger = (Tiger) animal;
            if (!tiger.getIsFemale()) {
                // New tigers are born into surrounding locations.
                // Get a list of free surrounding locations.
                Field field = tiger.getField();
                List<Location> free = field.getFreeSurroundingLocations(getLocation(),2);
                int births = breed();
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    boolean isFemale = chooseGender();
                    Tiger young = new Tiger(false, isFemale, field, loc);
                    newTigers.add(young);
                }
            }
        }
    }
    
    /**
     * If the weather is not foggy and one of the animals 
     * around it is of its preys, then eat it and return
     * the location of the animal that was eaten.
     * @param object The animal that might be eaten.
     * @param where The location of the animal to be eaten.
     * @param weather The weather of the simulation.
     * @return The location of the animal that was eaten.
     */
    protected Location checkFood(Object animal, Location where, String weather)
    {
        if(!weather.equals("Foggy")) {
            if(animal instanceof Capybara) {
                Capybara capybara = (Capybara) animal;
                if(capybara.isAlive()) { 
                    capybara.setDead();
                    changeEnergyLevel(capybara.getFoodValue());
                    return where;
                }
            }else if(animal instanceof Deer) {
                Deer deer = (Deer) animal;
                if(deer.isAlive()) { 
                    deer.setDead();
                    changeEnergyLevel(deer.getFoodValue());
                    if(deer.getIsInfected()) {
                        infected();
                    }
                    return where;
                }
            }
        }

        return null;
    }
    
    /**
     * Return the tiger's breeding age.
     * @return tiger's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the tiger's breeding probability.
     * @return tiger's breeding probability.
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the tiger's maximum litter size.
     * @return tiger's maximum litter size.
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Return the energy derived from the tiger when eaten.
     * @return the energy value derived from eating the snake.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Return the energy level at which the tiger will start 
     * to find food.
     * @return the energy level when the tiger will start to
     * find food.
     */
    protected int getHungry()
    {
        return HUNGRY;
    }

    /**
     * Return the maximum age that a tiger can live up to.
     * @return The tiger's maximum age. 
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return true if the tiger sleeps at nighttime.
     * @return true if the tiger sleeps at night, false otherwise.
     */
    protected boolean getCanSleep()
    {
        return CAN_SLEEP;
    }
}