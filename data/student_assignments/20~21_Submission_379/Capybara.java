import java.util.List;
import java.util.Random;

/**
 * A simple model of a capybara.
 * Capybaras age, move, reproduce, find plants to eat, and die.
 *
 * @version 2021.3.3
 */
public class Capybara extends Animal
{
    // Characteristics shared by all capybaras (class variables).

    // The age at which a capybara can start to breed.
    private static final int BREEDING_AGE = 35;
    // The age to which a capybara can live.
    private static final int MAX_AGE = 110;
    // The likelihood of a capybara breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value a capybara gives when it is eaten by a predator.
    private static final int FOOD_VALUE = 40;
    // The energy level when the capybara gets hungry.
    private static final int HUNGRY = 35;
    // Indicates if the capybara sleeps at nighttime.
    private static final boolean CAN_SLEEP = true;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new capybara. A capybara may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the capybara will have a random age 
     * and hunger level.
     * @param isFemale A boolean indicating if the capybara is female.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Capybara(boolean randomAge, boolean isFemale, Field field, Location location)
    {
        super(randomAge, isFemale, field, location);
    }

    /**
     * Check whether the given animal is a male capybara.
     * If so, then give birth to a random number of offsprings 
     * in the free locations surrounding it, if there are any.
     * @param newCapybaras A list to add all the new offspring.
     * @param animal An animal that could be a male capybara.
     */
    protected void giveBirth(List<Animal> newCapybaras, Object animal)
    {
        if (animal instanceof Capybara) {
            Capybara capybara = (Capybara) animal;
            if (!capybara.getIsFemale()) {
                // New capybaras are born into surrounding locations.
                // Get a list of free surrounding locations.
                Field field = capybara.getField();
                List<Location> free = field.getFreeSurroundingLocations(getLocation(),2);
                int births = breed();
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    boolean isFemale = chooseGender();
                    Capybara young = new Capybara(false, isFemale, field, loc);
                    newCapybaras.add(young);
                }
            }
        }
    }

    /**
     * If one of the plants around it is of its food source, 
     * then eat it and return the location of the plant 
     * that was eaten.
     * @param plant The plant which the deer might eat.
     * @param where The location of the plant to be eaten.
     * @param weather The weather of the simulation.
     * @return The location of plant that was eaten.
     */
    protected Location checkFood(Object plant, Location where, String weather)
    {
        if(plant instanceof Grass) {
            Grass grass = (Grass) plant;
            if(grass.isAlive()) { 
                grass.setDead();
                changeEnergyLevel(grass.getFoodValue());
                return where;
            }
        } else if(plant instanceof Berry) {
            Berry berry = (Berry) plant;
            if(berry.isAlive()) { 
                berry.setDead();
                changeEnergyLevel(berry.getFoodValue());
                return where;
            }
        }
        
        return null;
    }
    
    /**
     * Return the capybara's breeding age.
     * @return capybara's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the capybara's breeding probability.
     * @return capybara's breeding probability.
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the capybara's maximum litter size.
     * @return capybara's maximum litter size.
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Return the energy derived from the capybara when eaten.
     * @return the energy value derived from eating the capybara.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Return the energy level at which the capybara will start 
     * to find food.
     * @return the energy level when the capybara will start to
     * find food.
     */
    protected int getHungry()
    {
        return HUNGRY;
    }

    /**
     * Return the maximum age that a capybara can live up to.
     * @return The capybara's maximum age. 
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return true if the capybara sleeps at nighttime.
     * @return true if the capybara sleeps at night, false otherwise.
     */
    protected boolean getCanSleep()
    {
        return CAN_SLEEP;
    }
}