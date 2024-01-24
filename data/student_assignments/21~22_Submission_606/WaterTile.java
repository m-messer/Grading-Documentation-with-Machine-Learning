import java.util.Random;
/**
 * A class represent a waterTile which animals can drink water from.
 * The origin of the spread of disease.
 * @version 2022.03.01 (15)
 */
public class WaterTile
{
    private Disease disease;
    private static final Random rand = Randomizer.getRandom();
    private static final double DISEASE_PROBABILITY=0.05;
    /**
     * Create a new waterTile.
     * There is a chance of the water containing a disease.
     */
    public WaterTile()
    {
        if(rand.nextDouble()<=DISEASE_PROBABILITY){
            disease = new Disease();
        }
    }
    
    public Disease getDisease()
    {
        return disease;
    }

}
