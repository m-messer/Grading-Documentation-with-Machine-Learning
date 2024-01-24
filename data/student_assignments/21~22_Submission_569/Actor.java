import java.util.List;
/**
 * An interface representing the shared characteristics of actors in the field
 *
 * @version 16.03.2022
 */
public interface Actor
{
    /**
     * Make this actor act - that is: make it do
     * whatever it wants/needs to do.
     * @param newActors A list to receive newly born actors.
     */
   void act(List<Actor> newActors);
   
   /**
     * What this actor does at night
     * @param newActors A list to receive newly born actors.
     */
   void nightAct(List<Actor> newActors);
   
   /**
    * Whether the actor is alive or not.
    */
   boolean isActive(); 
}
