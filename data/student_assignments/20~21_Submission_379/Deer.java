import java.util.List;
import java.util.Random;

/**
 * A simple model of a deer.
 * Deers age, move, reproduce, find plants to eat, and die.
 *
 * @version 2021.3.3
 */
public class Deer extends Animal
{
    // Characteristics shared by all deer (class variables).

    // The age at which a deer can start to breed.
    private static final int BREEDING_AGE = 40;
    // The age to which a deer can live.
    private static final int MAX_AGE = 105;
    // The likelihood of a deer breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value a deer gives when it is eaten by a predator.
    private static final int FOOD_VALUE = 45;
    // The energy level when the deer gets hungry.
    private static final int HUNGRY = 40;
    // Indicates if the deer sleeps at nighttime.
    private static final boolean CAN_SLEEP = true;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new deer. A deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the deer will have a random age
     * and hunger level.
     * @param isFemale A boolean indicating if the deer is female.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, boolean isFemale, Field field, Location location)
    {
        super(randomAge, isFemale, field, location);
    }
    
    /**
     * Check whether the given animal is a male deer.
     * If so, then give birth to a random number of offsprings 
     * in the free locations surrounding it, if there are any.
     * @param newDeer A list to add all the new offspring.
     * @param animal An animal that could be a male deer.
     */
    protected void giveBirth(List<Animal> newDeer, Object animal)
    {
        if (animal instanceof Deer) {
            Deer deer = (Deer) animal;
            if (!deer.getIsFemale()) {
                // New deer are born into surrounding locations.
                // Get a list of free surrounding locations.
                Field field = deer.getField();
                List<Location> free = field.getFreeSurroundingLocations(getLocation(),2);
                int births = breed();
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    boolean isFemale = chooseGender();
                    Deer young = new Deer(false, isFemale, field, loc);
                    newDeer.add(young);
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
        } else if(plant instanceof Castor) {
            Castor castor = (Castor) plant;
            if(castor.isAlive()) { 
                castor.setDead();
                changeEnergyLevel(castor.getFoodValue());
                infected();
                return where;
            }
        }

        return null;
    }
    
    /**
     * Return the deer's breeding age.
     * @return deer's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the deer's breeding probability.
     * @return deer's breeding probability.
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the deer's maximum litter size.
     * @return deer's maximum litter size.
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * Return the energy derived from the deer when eaten.
     * @return the energy value derived from eating the deer.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Return the energy level at which the deer will start 
     * to find food.
     * @return the energy level when the deer will start to
     * find food.
     */
    protected int getHungry()
    {
        return HUNGRY;
    }

    /**
     * Return the maximum age that a deer can live up to.
     * @return The deer's maximum age. 
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return true if the deer sleeps at nighttime.
     * @return true if the deer sleeps at night, false otherwise.
     */
    protected boolean getCanSleep()
    {
        return CAN_SLEEP;
    }
}