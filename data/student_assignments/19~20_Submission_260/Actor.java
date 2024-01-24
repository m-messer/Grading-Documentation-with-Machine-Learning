import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A class representing shared characteristics of all participants in the simulation .
 *
 * @version 2020.02
 */
public interface Actor
{

    /**
     * Make this participant act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param isDay True if it is day, false if it is night.
     * @param harshWeather - true if harsh weather, false if not
     */
    abstract public void act(List<Actor> newActors, boolean isDay, boolean harshWeather);

    /**
     * Check whether the actor is active or not.
     * @return true if the actor is still active.
     */
    boolean isActive();

    /**
     * Set the actor's state to inactive
     */
    void setInactive();
}
