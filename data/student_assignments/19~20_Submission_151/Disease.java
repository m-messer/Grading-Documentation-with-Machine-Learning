
import java.util.Random;
import java.util.ArrayList;

/**
 * A simple model of a disease
 * 
 *
 * @version 21.02.2020 (2)
 */
public class Disease
{
    
    //calling the Field Class
    private Field field;
    
    // A shared random number generator
    private static final Random rand = Randomizer.getRandom();
    
    //calling the Animal Class
    private Animal animal;
    
    //a variable which checkes the animal has disease
    private boolean hasDisease;
    
    /**
     * Sets the disease to false by default 
     */
    public Disease()
    {
        hasDisease = false;
    }
    
    /**
     * a method which infects the animals by disease
     * and prints that they have been infected
     */
    public boolean hasDisease()
    {
        System.out.println("Animals have been infected with unknown disease!!!");
        hasDisease =true;
        return hasDisease;
        
    }
    
    /**
     * a method which cures the animals from disease
     * and prints that they have been cured
     */
    public boolean hasNotDisease()
    {
        System.out.println("Animals have been cured!!!");
        hasDisease =false;
        return hasDisease;
    }
}
