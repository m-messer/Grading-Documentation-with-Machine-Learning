import java.util.Random;
import java.util.List;

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
    private Field field;
    
    //the animal's gender and if they can breed
    private boolean isFemale;
    
    // The animal's position in the field.
    private Location location;
    
    private boolean isInfected;
    //The animal's disease status.
    
    private int age;
    
    private int foodLevel;
    
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
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);
    
    /**
     * What this animal does at night
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void nightAct(List<Animal> newAnimals);
    
    abstract protected int getMaxAge();
    
    abstract protected boolean checkMate();
    
    
    protected void setAge(int nAge){
        age = nAge;
    }
    
    protected int getAge(){
        return age;
    }
    
    protected void setFoodLevel(int nFoodLevel){
        foodLevel = nFoodLevel;
    }
    
    protected int getFoodLevel(){
        return foodLevel;
    }
    
    
    
    /**
     * Increase the age. This could result in the whale's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    /**
     * Make this seal more hungry. This could result in the seal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    
    public boolean canBreed()
    {
        
        boolean canbreed= false;
        
        if (isFemale()){
            if (age >= getBreedingAge()){
                if (getFoodLevel() >= 45){
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
    
    abstract protected int getBreedingAge();
    
    abstract protected double getBreedingProb();
    
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
     * 
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
