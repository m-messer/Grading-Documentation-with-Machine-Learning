import java.util.Random;
import java.util.List;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal implements Actor
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    //the animal's gender and if they can breed
    private boolean isFemale;
    // The animal's position in the field.
    private Location location;
    //The animal's disease status.
    private boolean isInfected;
    
    //Individual Characteristics (Instance Fields):
    
    //The animal's age.
    private int age;
    //The animal's food level - the number of steps the animal can go without eating
    //before dying of starvation
    //The food level is increased by eating prey
    private int foodLevel;
    //A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        age= 0;
        isInfected = false;
        isFemale = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Returns the maximum age of the animal.
     */
    abstract protected int getMaxAge();
    
    /**
     * Returns whether or not there is a suitable mate in proximity of the animal
     * Returns true is there is, false if there is not.
     * @return true if there is a mate in the breeding radius
     */
    abstract protected boolean checkMate();
    
    /**
     * Sets the age of the Animal
     * @param nAge The new age of the animal
     */
    protected void setAge(int nAge){
        age = nAge;
    }
    
    /**
     * Returns the current age of the animal
     * @return the age of the animal
     */
    protected int getAge(){
        return age;
    }
    
    /**
     * Sets the food level of the animal
     * @param nFoodLevel The new food level of the animal
     */
    protected void setFoodLevel(int nFoodLevel){
        foodLevel = nFoodLevel;
    }
    
    /**
     * Returns the current food level of the animal
     * @return The food level of the animal
     */
    protected int getFoodLevel(){
        return foodLevel;
    }
    
    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Checks whether or not the animal meets the conditions to be able to breed
     * An animal can only breed if their age is greater than the breeding age, if
     * it's food level is above a certain value and if there is a mate within its
     * breeding radius.
     * @return true if the animal can breed
     */
    public boolean canBreed()
    {
        boolean canbreed= false;
        if (isFemale()){
            if (age >= getBreedingAge()){
                if (getFoodLevel() >= 33){
                    if (checkMate()){
                        canbreed = true;
                    }
                }
            }
        }
        return canbreed;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProb()) {
            births = rand.nextInt(getLitterSize()) + 1;
        }
        return births;
    }
    
    /**
     * Returns the breeding age of the animal.
     * @return Animal's breeding age.
     */
    abstract protected int getBreedingAge();
    
    /**
     * Returns the breeding probability of the animal.
     * @return Animal's breeding probability.
     */
    abstract protected double getBreedingProb();
    
    /**
     * Returns the maximum litter size of the animal.
     * @return Animal's max litter size.
     */
    abstract protected int getLitterSize();

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Check whether the animal is female or not.
     * @return true if the animal is a female.
     */
    protected boolean isFemale()
    {
        return isFemale;
    }
    
    /**
     * Check whether the animal has a disease.
     * @return true if the animal is diseased.
     */
    protected boolean isInfected()
    {
        return isInfected;
    }
    
    /**
     * Indicate that the animal has a disease.
     */
    protected void setDisease()
    {
        isInfected = true;
    }
    
    /**
     * Indicate that the animal is a male.
     * 
     */
    protected void setMale()
    {
        isFemale = false;
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
}
