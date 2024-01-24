import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
/**
 * A class representing shared characteristics of Actors.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Actor
{
    // Whether the Actor is alive or not.
    protected boolean alive;
    // The Actor's field.
    protected Field field;
    // The Actor's position in the field.
    protected Location location;
    // The Actor's disease state
    protected Disease disease;
    // The chance that the Actor is born with a disease
    private static final double DISEASE_PROBABILITY = 0.05;
    // A random number generator to randomly assign age to Actor.
    private static final Random rand = Randomizer.getRandom();
    // The Actor's age
    protected int age;
    // If the Actor (plant) is edible
    protected boolean edible;
    // If the Actor has a disease or not
    protected boolean diseased;
    // To check if an Actor prey on another Actor
    public FoodChain foodChain;
    // Adding new Actor's to the pre existing ones
    protected ArrayList<Actor> newActors;
    
    /**
     * Create a new Actor at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Actor(boolean randomAge,Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        newActors = new ArrayList();
        // Actor (plant) is edible
        edible = true;
        diseased = false;
        foodChain = new FoodChain();
        if(randomAge)
        {
            // set random age of Actor from within it's max age range
            age = rand.nextInt(this.getMaxAge());
        }
        else 
        {
            age = 0;
        }
        if(rand.nextDouble() <= DISEASE_PROBABILITY && this instanceof Animal)
        {
            disease = new Disease(this);
        }
    }
    
    /**
     * Make this Actor act during day - that is: make it do
     * whatever it wants/needs to do.
     * @param isRaning To know if it is raining or not
     */
    abstract public void actDay(boolean isRaning);
    
    /**
     * Make this Actor act during night - that is: make it do
     * whatever it wants/needs to do.
     * @param isRaning To know if it is raining or not
     */
    abstract public void actNight(boolean isRaning);
    
    /**
     * Implemented when an Actor eats another Actor
     */
    abstract protected void eaten();
    
    /**
     * Get the food value an Actor provides when consumed
     */
    abstract public int getFoodValue();
    
    /** 
     * Get maximum age of Actor
     */
    abstract public int getMaxAge();

    /**
     * Increment age of the Actor
     * if age is greater than max age 
     * then make the Actor die
     */
    protected void incrementAge()
    {
        age++;
        if(age > this.getMaxAge())
            setDead();
    
    }
    
    /**
     * Indicate that the Actor is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
        
    /**
     * Give Actor disease by setting boolean true
     */
    public void giveDisease()
    {
        diseased = true;
    }
           
    /**
     * Place the Actor at the new location in the given field.
     * @param newLocation The Actor's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * @return true if the Actor is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }  
    
    /**
     * @return true if the Actor is still edible 
     */
    protected boolean isEdible()
    {
        return edible;
    }
    
    /**
     * @return true if the Actor has disease.
     */
    public boolean isDiseased()
    {
        return diseased;
    }

    /**
     * Return the Actor's location.
     * @return The Actor's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Return the Actor's field.
     * @return The Actor's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * @return List of new Actors added
     */
    public ArrayList<Actor> getNewActors()
    {
        return newActors;
    }
}
