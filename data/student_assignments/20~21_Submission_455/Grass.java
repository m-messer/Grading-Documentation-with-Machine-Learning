
/**
 * A simple model of a grass plant. It does nothing but age. It may be eaten by herbivorous animals.
 *
 * @version 2021.03.02 (3)
 */
public class Grass extends Plant
{
    private static final int MAX_AGE=100;
    
    /**
     * Creates a new grass plant at the supplied location. (It will be stored, however in a field separate from the animal one)
     * 
     * @param randomAge Specify whether the starting age of the grass plant should be random (true), or zero (false)
     * @param field The field in which the grass plant will live (should be the plant field)
     * @param location The grass plant's location
     * @param simulator A reference to the main Simulator type object.
     */
       public Grass(boolean randomAge, Field field, Location location, Simulator simulator)
    {
        super(randomAge, field, location, simulator);
        
    }
    
    /**
     * Returns maximum age this grass plant may live up to in its current state. Grass plants do not get sick, so this value
     * is the same as the default maximum age.a
     * @return int Maximum age.
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
    
    /**
     * Grass plants do not get sick, therefore this method has no effect.
     */
    protected void getDisease(){
    }
    
    /**
     * Returns the maximum age this plant may live up to when healthy.
     * @return int Maximum age when healthy
     */
    public int getDefaultMaxAge(){
        return MAX_AGE;
    }
    }


