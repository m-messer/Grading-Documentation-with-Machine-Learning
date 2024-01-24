import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2019.02.22 
 */
 public abstract class Animal
 {
     // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // Another shared random number generator to control breeding.
    private static final Random rand2 = Randomizer.getRandom();
    // The animals's gender
    private boolean female;
    // Variable to generate a random number
    private static final Random rand = Randomizer.getRandom();
    // the disease probability
    private static final double DISEASE_PROBABILITY = 0.0001;
    // variable to see if diseased or not
    private boolean diseased;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        female = rand2.nextBoolean();
        diseased = false;
    }
    
    /**
     * Make this animal act during the day - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act1(List<Animal> newAnimals);
    
    /**
     * Make this animal act during the night - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act2(List<Animal> newAnimals);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
    
    /**
     * This method is to check if the animal is a male
     */
    protected boolean isMale()
    {
        return !female;
    }
    
    /**
     * Check whether the animal is diseased or not.
     * @return true if the animal is diseased.
     */
    protected boolean isDiseased()
    {
        return diseased;
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
    
    /**
     * Assigns the animal to be diseased or not.
     */
    protected void getDisease()
    {
      if(rand.nextDouble() <= DISEASE_PROBABILITY)
      {
        diseased = true;  
      }
    }
 }
