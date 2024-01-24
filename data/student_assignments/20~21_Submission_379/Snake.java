import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a snake.
 * Snake age, move, reproduce, eats rats and capybaras, and die.
 *
 * @version 2021.3.3
 */
public class Snake extends Animal
{
    // Characteristics shared by all snakes (class variables).

    // The age at which a snake can start to breed.
    private static final int BREEDING_AGE = 45;
    // The age to which a snake can live.
    private static final int MAX_AGE = 120;
    // The likelihood of a snake breeding.
    private static final double BREEDING_PROBABILITY = 0.11;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value a snake gives when eaten.
    // Currently, snakes are predators, hence it will not be eaten.
    private static final int FOOD_VALUE = 0;
    // The energy level at which the snake gets hungry.
    private static final int HUNGRY = 40;
    // Indicates if the snake sleeps at nighttime.
    private static final boolean CAN_SLEEP = false;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a snake. A snake can be created as a new born (age 
     * zero and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age 
     * and hunger level.
     * @param isFemale A boolean indicating if the snake is female.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge, boolean isFemale, Field field, Location location)
    {
        super(randomAge, isFemale, field, location);
    }
    
    /**
     * Check whether the given animal is a male snake.
     * If so, then give birth to a random number of offsprings 
     * in the free locations surrounding it, if there are any.
     * @param newSnakes A list to add all the new offspring.
     * @param animal An animal that could be a male snake.
     */
    protected void giveBirth(List<Animal> newSnakes, Object animal)
    {
        if (animal instanceof Snake) {
            Snake snake = (Snake) animal;
            if (!snake.getIsFemale()) {
                // New snakes are born into surrounding locations.
                // Get a list of free surrounding locations.
                Field field = snake.getField();
                List<Location> free = field.getFreeSurroundingLocations(getLocation(), 2);
                int births = breed();
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    boolean isFemale = chooseGender();
                    Snake young = new Snake(false, isFemale, field, loc);
                    newSnakes.add(young);
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
            }else if(animal instanceof Rat) {
                Rat rat = (Rat) animal;
                if(rat.isAlive()) { 
                    rat.setDead();
                    changeEnergyLevel(rat.getFoodValue());
                    if(rat.getIsInfected()) {
                        infected();
                    }
                    return where;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Return the snake's breeding age.
     * @return snake's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the snake's breeding probability.
     * @return snake's breeding probability.
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the snake's maximum litter size.
     * @return snake's maximum litter size.
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Return the energy derived from the snake when eaten.
     * @return the energy value derived from eating the snake.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Return the energy level at which the snake will start 
     * to find food.
     * @return the energy level when the snake will start to
     * find food.
     */
    protected int getHungry()
    {
        return HUNGRY;
    }

    /**
     * Return the maximum age that a snake can live up to.
     * @return The snake's maximum age. 
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return true if the snake sleeps at nighttime.
     * @return true if the snake sleeps at night, false otherwise.
     */
    protected boolean getCanSleep()
    {
        return CAN_SLEEP;
    }
}