import java.util.List;

/**
 * A class representing shared characteristics of preys.
 *
 * @version 2019.02.22 (2)
 */
public abstract class Prey extends Animal {

    // The food value of a single plant. In effect, this is the
    // number of steps a prey can go before it has to eat again.
    static final int PLANT_FOOD_VALUE = 100;

    /**
     * Create a new prey.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param female If true, the animal is female.
     */
    public Prey(Field field, Location location, boolean female, boolean isSick)
    {
        super(field, location, female, isSick);
        
    }

    /**
     * This is what the prey does most of the time: it looks for
     * plants. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newPreys A list to return newly born preys.
     */
    public void act(List<Animal> newPreys) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newPreys);
            // Move towards a source of food if found.
            Location newLocation = findFood();
            newLocation = newLocation == null ? getField().freeAdjacentLocation(getLocation()) : null;
                if(newLocation != null) setLocation(newLocation); else setDead();
        }
    }


    abstract protected Location findFood();

    protected abstract void incrementAge();

    abstract protected void incrementHunger();

    abstract protected void giveBirth(List<Animal> newPreys);

    abstract protected int breed();

    abstract protected boolean canBreed();

}
