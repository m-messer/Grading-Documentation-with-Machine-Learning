
/**
 * A simple model of a plant.
 *
 * @version 2021.03.02 (3)
 */
public abstract class Plant extends Organism
{
    /**
     * Creates a new plant at the supplied location. (It will be stored, however in a field separate from the animal one)
     * 
     * @param randomAge Specify whether the starting age of the plant should be random (true), or zero (false)
     * @param field The field in which the plant will live (should be the plant field)
     * @param location The plant's location
     * @param simulator A reference to the main Simulator type object.
     */
    public Plant(boolean randomAge, Field field, Location location, Simulator simulator)
    {
        super(randomAge, field, location, simulator);
        
        diseaseLevel = 0;  //plants should always have diseaseLevel = 0;
                            // because plants do not currently get sick
    }
    
    /**
     * Returns maximum age this plant may live up to in its current state.
     * @return int Maximum age.
     */
    public abstract int getMaxAge();
    
    /**
     * Returns the maximum age this plant may live up to when healthy.
     * @return int Maximum age when healthy
     */
    public abstract int getDefaultMaxAge();
    
    


}
