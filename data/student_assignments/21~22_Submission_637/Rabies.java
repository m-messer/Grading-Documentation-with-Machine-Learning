import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**A class representing shared characteristics of rabies
 * 
 *
 * @version 2022.03.02
 */
public class Rabies extends Disease
{
    
    private static final Random rand = Randomizer.getRandom();
    private static final double AGE_DECREASE_MULTIPLIER = 0.5;
    
    private static final String name = "Rabies";
    public Rabies() {
        super(0.3,0.1 );
    }
    
    public boolean canBeInfected(Animal animal){
        if (animal instanceof Monkey) {
            return true;
        }
        else if (animal instanceof Bear) {
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
