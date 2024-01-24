import java.util.List;
import java.util.Iterator;

/**
 * Write a description of class Prey here.
 *
 * @version 2021.03.02
 */
public abstract class Prey extends Animal implements Actor
{
    private Simulator simulator;

    /**
     * Constructor for objects of class Prey
     */
    public Prey(Field field, Location location)
    {
        super(field,location);
    }

    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Actor> newPrey)
    {
        incrementAge();
        incrementHunger();
        //An animal can act unless it is dead or during a blizzard.
        if(isAlive() && !simulator.getTime() && simulator.getWeather() != "blizzard") {
            //Animals cannot propagate when it is too hot.
            if(simulator.getWeather() != "heat"){
                giveBirth(newPrey);
            }
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        int foodLevel = getFoodLevel();
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for plants adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        List<Location> adjacent = getField().adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = getField().getObjectAt(where);
            if(isFood(plant)) {
                if(castFood(plant).isAlive())
                {
                    castFood(plant).setDead();
                    updateFoodLevel();
                    return where;
                }   
            }
        }
        return null;
    }

    /**
     * This method returns the food level of an animal.
     * @return int foodLevel
     */
    abstract protected  int getFoodLevel();

    /**
     * This method updates the food level of an animal by assigning the food value 
     * that the food represents for each animal.
     */
    abstract protected void updateFoodLevel();

    /**
     * Retruns true if the object is an instance of the class
     * that is assigned as the food source for the animal.
     * @return boolean value
     */
    abstract protected boolean isFood(Object plant);

    abstract protected Plant castFood(Object plant);
}
