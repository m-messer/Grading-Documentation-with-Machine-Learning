import java.util.List;
import java.util.Random;
/**
 * A class representing shared characteristics of Plants.
 * It is a sub class of super class Actor.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Plant extends Actor
{
    //The Plant's growth level
    protected int growthLevel;
    //Whether Plant is edble or not
    protected boolean edible;
    //Set random growth level fro Plant
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new Plants at location in field and
     * give it a random growth level
     * 
     * @param randomAge Randomly assigned age to Plant
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge,Field field, Location location)
    {
        super(randomAge, field, location);
        growthLevel = rand.nextInt(this.getRegrowthPeriod());
    }

    /**
     * Time before Plant regrows and is edible again
     */
    abstract public int getRegrowthPeriod();

    /**
     * Plant grows and its age increments
     * as the simulation progresses
     */
    public void lifeProcesses()
    {
        grow();
        incrementAge();
    }
    
    /**
     * If Plant is inedible it's growth level increases 
     * and it becomes edible once it is past regrowth period
     */
    public void grow()
    {
        if(!edible)
        {
            growthLevel++;
        }
        if(growthLevel >= this.getRegrowthPeriod())
        {
            edible = true;
            growthLevel = 0;
        }
    }

    /**
     * Plant once consumed becomes inedible for a set period 
     */
    protected void eaten()
    {
        setInedible();
    }

    /**
     * Make Plant inedible and groth level 0
     */
    private void setInedible()
    {
        edible = false;
        growthLevel = 0;
    }
}
