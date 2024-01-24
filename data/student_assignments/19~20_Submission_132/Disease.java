import java.util.List;
import java.util.Random;
import java.util.ArrayList;
/**
 * Disease class gives animals the disease 
 * and helps in the spreading of it to adjacent animals.
 * Animals die in a certain period of time after
 * getting infected by the disease.
 *
 * @version (a version number or a date)
 */
public class Disease
{
    // Chance of getting infected from an already infected Animal
    private static final double INFECTION_CHANCE = 0.005;
    // A random number generator to randomly see chance of infection.
    private Random rand = new Random();
    // Time period for which an animal can survive after getting disease
    private static final int SURVIVAL_TIME = 10;
    // Increment time animal has spent with disease
    private int timeWithDisease;
    // An object of class Actor for animal who acts host of disease
    private Actor host;

    /**
     * The actor becomes a host of the disease
     * and gets the disease
     */
    public Disease(Actor actor)
    {
        host = actor;
        actor.giveDisease();
    }

    /**
     * Spread disease through host and increment time 
     * host has lived with disease
     * @param loacation Location of host in field
     * @param field Field in which the hos is situated
     */    
    public void diseaseFunctions(Location location, Field field)
    {
        spreadDisease(location,field);
        incrementTimeWithDisease();
    }
    
    /**
     * Spread disease to actors neighbouirng the host
     * @param loacation Location of host in field
     * @param field Field in which the hos is situated
     */
    private void spreadDisease(Location location, Field field)
    {
        //assert field != null;
        List<Location> adjacent = field.adjacentLocations(location);
        for(Location position : adjacent)
        {
            Object adjacentActor = field.getObjectAt(position);
            if(adjacentActor instanceof Actor)
            {
                Actor actor = (Actor) adjacentActor;
                if(rand.nextDouble() <= INFECTION_CHANCE)
                {
                    new Disease(actor);
                }
            }
        }
    }
    
    /**
     * Increment time host has lived with disease
     * If it is over the surivival time 
     * make the host die
     */
    private void incrementTimeWithDisease()
    {
        timeWithDisease++;
        if(timeWithDisease >= SURVIVAL_TIME)
        {
            host.setDead();  
        }
    }
}
