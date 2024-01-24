import java.util.List;
import java.util.Random;

/**
 * A simple model of a rat.
 * Rats age, move, reproduce, find plants to eat, and die.
 *
 * @version 2021.3.3
 */
public class Rat extends Animal
{
    // Characteristics shared by all rats (class variables).

    // The age at which a rat can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which a rat can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a rat breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value a rat gives when it is eaten by a predator.
    private static final int FOOD_VALUE = 30;
     // The energy level at which the rat gets hungry.
    private static final int HUNGRY = 30;
    // Indicates if the rat sleeps at nighttime.
    private static final boolean CAN_SLEEP = true;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new rat. A rat may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rat will have a random age 
     * and hunger level.
     * @param isFemale A boolean indicating if the rat is female.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rat(boolean randomAge, boolean isFemale, Field field, Location location)
    {
        super(randomAge, isFemale, field, location);
    }

    /**
     * Check whether the given animal is a male rat.
     * If so, then give birth to a random number of offsprings 
     * in the free locations surrounding it, if there are any.
     * @param newRats A list to add all the new offspring.
     * @param animal An animal that could be a male rat.
     */
    protected void giveBirth(List<Animal> newRats, Object animal)
    {
        if (animal instanceof Rat) {
            Rat rat = (Rat) animal;
            if (!rat.getIsFemale()) {
                // New rats are born into surrounding locations.
                // Get a list of free surrounding locations.
                Field field = rat.getField();
                List<Location> free = field.getFreeSurroundingLocations(getLocation(),2);

                int births = breed();
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    boolean isFemale = chooseGender();
                    Rat young = new Rat(false, isFemale, field, loc);
                    newRats.add(young);
                }
            }
        }
    }

    /**
     * If one of the plants around it is of its food source, 
     * then eat it and return the location of the plant 
     * that was eaten.
     * @param plant The plant which the rat might eat.
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
     * Return the rat's breeding age.
     * @return rat's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the rat's breeding probability.
     * @return rat's breeding probability.
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the rat's maximum litter size.
     * @return rat's maximum litter size.
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Return the energy derived from the rat when eaten.
     * @return the energy value derived from eating the rat.
     */
    protected int getFoodValue()
    {
        return FOOD_VALUE;
    }

    /**
     * Return the energy level at which the rat will start 
     * to find food.
     * @return the energy level when the rat will start to
     * find food.
     */
    protected int getHungry()
    {
        return HUNGRY;
    }

    /**
     * Return the maximum age that a rat can live up to.
     * @return The rat's maximum age. 
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return true if the rat sleeps at nighttime.
     * @return true if the rat sleeps at night, false otherwise.
     */
    protected boolean getCanSleep()
    {
        return CAN_SLEEP;
    }
}