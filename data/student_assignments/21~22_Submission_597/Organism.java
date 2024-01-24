import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class representing the shared characteristics of all organisms.
 *
 * @version 2022-02-21
 */
public abstract class Organism 
{

    //Whether the animal is alive.
    private boolean alive;
    //The organism's field.
    private Field field;
    //The organism's location in the field.
    private Location location;
    //The age of the organism.
    private int age;
    //The maximum age of the organism.
    private int maxAge;
    //The food value that this organism provides its predator.
    private int foodValue;
    //The chance that the organism will breed.
    private double breedingChance;
    //The max litter size of the organism.
    private int maxLitterSize;
    // This variable will control when the organsim will act, initiation value on the variable is dependent on if the act method should
    // be done in day/night rotation of species. true means not nocturnal, false represents a nocturnal specie
    private boolean shouldAct;
    
    private Random rand = Randomizer.getRandom();
    /**
     * Creates an organism in a new position with the specified age and food values.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param age The current age.
     * @param maxAge The maximum age.
     * @param foodValue The number of steps it provides a predator.
     * @param breedingChance The chance that the organism will breed.
     * @param maxLitterSize The greatest possible size of a litter.
     */
    public Organism(Field field, Location location, int age, int maxAge, int foodValue,
                    double breedingChance, int maxLitterSize, boolean shouldAct) 
    {
        this.alive = true;
        this.field = field;
        this.location = location;
        this.age = age;
        this.maxAge = maxAge;
        this.foodValue = foodValue;
        this.breedingChance = breedingChance;
        this.maxLitterSize = maxLitterSize;
        this.shouldAct = shouldAct;
    }

    /**
     * Make this organism act - that is: make it do
     * whatever it wants/needs to do.
     * @param newOrganisms A list to receive newly born organisms.
     */
    abstract public void act(List<Organism> newOrganisms);

    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the organism is no longer alive.
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
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
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
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Increase the age. This could result in the organism's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * Returns the food value of the organism.
     * @return int the number of additional steps the predator can take if this organism
     * is consumed.
     */
    protected int getFoodValue()
    { 
        return foodValue;
    }

    /**
     * Increases the food value of the organism by the specified value.
      * @param increaseInFV value foodValue is increased by.
     */
    protected void increaseFoodValue(int increaseInFV)
    {
        foodValue += increaseInFV;
    }

    /**
     * Returns the age of the organism.
     * @return int the age of the organism.
     */
    protected int getAge()
    {
        return age;
    }

    /**
     * Sets the age of the organism to that specified.
     * @param newAge the new value that the age should be set to.
     */
    protected void setAge(int newAge)
    {
        this.age = newAge;
    }

    /**
     * Returns the maximum age of the organism.
     * @return int the maximum age the organism can survive to.
     */
    protected int getMaxAge()
    {
        return maxAge;
    }

    /**
     * Sets the maximum age to the number specified.
     * @param newMaxAge the new value the maximum age is set to.
     */
    protected void setMaxAge(int newMaxAge)
    {
        this.maxAge = newMaxAge;
    }

    /**
     * Returns the breeding chance for the particular organism.
     * @return double the chance that the organism breeds.
     */
    protected double getBreedingChance()
    {
        return breedingChance;
    }

    /**
     * Sets the breeding chance to the value specified.
     * @param newBreedingChance the value the breeding chance should be set to.
     */
    protected void setBreedingChance(double newBreedingChance)
    {
        this.breedingChance = newBreedingChance;
    }

    /**
     * Returns the maximum litter size of the organism.
     * @return int the maximum number of organisms that can be birthed in one litter.
     */
    protected int getMaxLitterSize()
    {
        return maxLitterSize;
    }

    /**
     * Sets the maximum litter size to the stated value.
     * @param newMaxLitterSize the value to set the maximum litter size to.
     */
    protected void setMaxLitterSize(int newMaxLitterSize)
    {
        this.maxLitterSize = newMaxLitterSize;
    }

    /**
     * Returns a random number.
     * @return Random a random number.
     */
    protected Random getRand()
    {
        return rand;
    }
    
    /**
     * This returns the variable shouldAct of the organism
     */
    protected boolean getShouldAct()
    {
        return shouldAct;
    }
    
    /**
     * This method changes the state of the variable shouldAct after a 12h period to indicate the organism can act in sync with the time of day 
     */
    protected void changeShouldAct()
    {
        shouldAct = !shouldAct;
    }
}
