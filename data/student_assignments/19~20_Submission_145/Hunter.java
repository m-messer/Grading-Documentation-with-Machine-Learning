import java.util.List;
import java.util.Iterator;

/**
 * A class representing the characteristics of the hunter in
 * the simulation.
 * 
 * @version 2020.02.22
 */
public class Hunter extends Actor
{
    // The maximum number of animals a hunter can hunt.
    private static final int HUNTING_LIMIT = 15;  
    // The total animals the hunter has hunted.
    private int currentHunts;   

    /**
     * Create a new hunter at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hunter(Field field, Location location)
    {
        super(field, location);
        currentHunts = 0;
    }

    /**
     * The hunter spends most of his time hunting down animals.
     * If the hunter cannot hunt an animal directly it moves around
     * until it finds its prey.
     * The hunter is unable to hunt if the weather is rainy or foggy.
     * 
     * @param currentWeather    The current weather in the simulation. 
     * @param steps             The current step in the simulator
     */
    public void act(String currentWeather, int steps)
    {
        if(canHunt()){  //which means if they reach the limit, hunters are not moving untill the next reset
            //The hunter does not hunt if the weather is rainy or foggy.
            if(!currentWeather.equals("rainy") || !currentWeather.equals("foggy")) {
                // Move towards an animal if found.
                Location newLocation = hunt(steps);

                if(newLocation == null) { 
                    // No animal found - try to move to a free location.                 
                    newLocation = getField().freeAdjacentLocation(getLocation());

                    // hunters are mean, so if adjacent location are not free, he will try to step on the plants
                    if(newLocation == null){    
                        // Check if the bear can step on a plant.
                        newLocation = getField().freePlantAdjacentLocation(getLocation());

                        if(newLocation != null){
                            ((Plant)getField().getObjectAt(newLocation)).setDead();
                        }
                    }
                }

                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else{
                    // do nothing, and stay where they are
                }
            }
        }
        checkIfResetCurrentHunt(steps);
    }

    /**
     * Checks if the hunter has exceeded the hunting limit when the current step is multiple of 100
     * (which means check each 2 days and 2 nights, if reaches the max, reset)
     * Resets the current hunts if meets the condition.
     */
    private void checkIfResetCurrentHunt(int steps)
    {
        if(currentHunts == HUNTING_LIMIT && steps % 100 == 0)
        {
            currentHunts = 0;
            System.out.println("Hunt level reset.");
        }
    }

    /**
     * Looks for animals to hunt.
     * Only the first live animal is hunted.
     * 
     * @param steps The current steps on the simulator
     * @return Where prey was found, or null if it wasn't.
     */
    private Location hunt(int steps)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object actor = field.getObjectAt(where);
            if(actor instanceof Animal) {
                Animal animal = (Animal) actor;
                if(animal.isAlive()){
                    animal.setDead();
                    currentHunts ++;
                    System.out.println("Hunter killed " + animal.getClass().getName() + ". Now the hunt level is " + currentHunts + "/15.");
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Returns whether the hunter can still hunt animals.
     * True if the hunter has not exceeded the hunting limit,
     * else false.
     * 
     * @return Returns whether the hunter can hunt.
     */
    private boolean canHunt()
    {
        return currentHunts < HUNTING_LIMIT;
    }
}
