import java.util.List;
/**
 * This interface represents all the actors that take part in the simulation.
 *
 * @version 2021.03.02
 */
public interface  Actor
{
    // instance variables - replace the example below with your own
    
    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
     void act(List<Actor> newActors);
     
    /**
     * Test if an actor is still alive
    */
    boolean isAlive();
    
}
