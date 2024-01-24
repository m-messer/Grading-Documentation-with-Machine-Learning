import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2020.2.22
 */
public abstract class Animal extends Actor
{
    private static final Random rand = Randomizer.getRandom();
    private boolean sex;
    private boolean sick;
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field,location);
        sex = rand.nextBoolean();
        sick = false;
    }
    
    /**
     * Get gender of animal.
     * @return true if animal is female, false is male. 
     */
    protected boolean getSex()
    {
        return sex;
    }
    
    /**
     * Get state of animal to determain weather the animal is sick.
     * @return true if animal is sick, return false if animal is not sick.
     */
    protected boolean getAnimalState()
    {
        return sick;
    }
    
    /**
     * Set the animal to sick
     */
    protected void getSick()
    {
        sick = true;
    }
    
    /**
     * Set animal not to sick.
     */
    protected void getCure()
    {
        sick = false;
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);
}
