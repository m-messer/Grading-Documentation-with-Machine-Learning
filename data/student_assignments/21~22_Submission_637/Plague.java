import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * 
 *
 * @version 2022.03.02
 */
public class Plague extends Disease
{
    
    private static final Random rand = Randomizer.getRandom();
    private static final double AGE_DECREASE_MULTIPLIER = 0.7;
    
    private static final String name = "Plague";
    public Plague() {
        super(0.5,0.1 );
    }
    
    public boolean canBeInfected(Animal animal){
        if (animal instanceof Dragon) {
            return true;
        }
        else if (animal instanceof Velociraptor) {
            return true;
        }
        else{
            return false;
        }
    }
    
    public double returnAgeDecrease(){
        return AGE_DECREASE_MULTIPLIER;
    }
}
