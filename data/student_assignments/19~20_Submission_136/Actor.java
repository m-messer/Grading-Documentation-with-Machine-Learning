import java.util.*;

/**
 * Actor is the interface that creates all the different types of actors
 * actor can create animal and plant objects that
 * interact in the simulation
 *
 * @version 2020.02.23
 */
public interface Actor
{
    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do. Make them look for food, etc.
     * @param newActors A list to receive newly born animals/plants.
     */
    public void act(List<Actor> newActors);
    
    /**
     * This method purpose its to call an overriden method
     * from the animal class, it makes objects of animal type
     * to stay in the same location without looking for food
     * in case of weather events, making them unable to hunt
     */
    public void act();
    
    /**
     * Checks if the actor is still alive in the simulation
     */
    public boolean isAlive();
}
