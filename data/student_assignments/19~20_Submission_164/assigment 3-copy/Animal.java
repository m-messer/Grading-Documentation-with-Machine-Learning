import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    protected Field field;
    // The animal's position in the field.
    private Location location;
    private Step step;
    //Holds the probability that an animal will die if it's snowing
    private static final double DYING_PROBABILITY = 0.0025;
    
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location, Step step)
    {
        alive = true;
        this.field = field;
        this.step = step;
        setLocation(location);
    }
    
    /**
     * Make this animal act at Day time - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
        
    abstract public void actDay(List<Animal> newAnimals);
    
    /**
     * Make this animal act at Night time - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void actNight(List<Animal> newAnimals);

    //abstract public void act(List<Animal> newAnimals);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Indicate that the animal is no longer alive.
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
    
    protected boolean isDay()
    {
        return step.isDay();
    }
    
    protected Step getStep()
    {
        return step;
    }
    
    /**
     * Uses a probability to set an animal to dead if it's snowing
     */
    protected void snowingEffect()
    {
        if(rand.nextDouble() <= DYING_PROBABILITY){
            setDead();
        }
    }
    
    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
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
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
}
