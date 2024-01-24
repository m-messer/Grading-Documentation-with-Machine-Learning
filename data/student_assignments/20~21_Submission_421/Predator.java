import java.util.List;
import java.util.Iterator;
/**
 * Write a description of class Predator here.
 *
 * 
 * @version 2021.03.02
 */
public abstract class Predator extends Animal implements Actor
{
    private Simulator simulator;

    /**
     * Constructor for objects of class Predator
     */
    public Predator(Field field, Location location)
    {
        super(field,location);
    }

    /**
     * This is what the predator does most of the time: it hunts for
     * its prey. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newPredators A list to return newly born predators.
     */
    public void act(List<Actor> newPredators)
    {        
        incrementAge();
        incrementHunger();
        //The predator will act unless it is dead or during a blizzard.
        if(isAlive() && simulator.getTime() && simulator.getWeather() != "blizzard") {
            //Animals can't have offspring if it is too hot.
            if(simulator.getWeather() != "heat"){
                giveBirth(newPredators);
            }
            // Move towards a source of food if found.

            Location newLocation = findFood();

            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().randomAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null && getField().getObjectAt(newLocation) == null || getField().getObjectAt(newLocation) instanceof Plant) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Make this predator more hungry. This could result in the predator's death.
     */
    protected void incrementHunger()
    {
        int foodLevel = getFoodLevel();
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for the prey adjacent to the current location.
     * Only the first live prey animal is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {

        List<Location> adjacent = getField().adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = getField().getObjectAt(where);
            if(isPrey(animal)) {
                //A predator can only hunt if it is not too foggy in the habitat.
                if(castPrey(animal).isAlive() && simulator.getWeather() != "fog") { 
                    castPrey(animal).setDead();
                    updateFoodLevel();
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * This method returns the food level of a predator.
     * @return int foodLevel
     */
    abstract protected  int getFoodLevel();

    /**
     * This method checks if the object animal is an instance of the prey that 
     * a predator hunts and returns a boolean value.
     * @return boolean value
     */
    abstract protected boolean isPrey(Object animal);

    /**
     * This method updates the food level of a predator by assigning the food value 
     * that the prey represent for each predator.
     */
    abstract protected void updateFoodLevel();

    /**
     * This method cast the object animal to the type of prey that a predator is hunting after
     * the object animal is checked in oreder to see if the object animal is an instance of the
     * type of prey that a predator may hunt and returns the animal value casted to the type of prey.
     * @return Animal the type of prey
     */
    abstract protected Animal castPrey(Object animal);
}
