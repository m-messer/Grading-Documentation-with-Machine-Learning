import java.util.List;
/**
 * Write a description of class Human here.
 *
 * @version (a version number or a date)
 */
public abstract class Human implements Aquatic
{
    // instance variables - replace the example below with your own
    // Whether the human is alive or not.
    private boolean alive;
    // The field that the hunter is in
    private Field field;
    // The location in the field
    private Location location;
    private int age;
    /**
     * Constructor for objects of class Human
     */
    public Human(Field field, Location location)
    
    {
        age=0;
        alive = true;
        this.field = field;
        this.location = location;
        setLocation(location);
    }
    /**
     * Make this human act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born humans.
     */
     public abstract void act(List<Aquatic> newHumans);
    /**
     * Check whether the human is alive or not.
     * @return true if the human is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
       /**
     * Indicate that the human is no longer alive.
     * It is removed from the field.
     */
     public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    /**
     * Returns the location of this fieldunit
     * @return the location of this fieldunit
     */

    public Location getLocation(){
        return location;
    }
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The human's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) 
        {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    /**
     * Return the human's field.
     * @return The human's field.
     */
    protected Field getField()
    {
        return field;
    }
    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    protected  void incrementAge()
    {
        age++;
        if(age > getMaxAge()) 
        {
            setDead();
        }
    }
    /**
     * Sets max age.
     */
    abstract  int getMaxAge();
    
    /**
     * Returns the age of the human.
     */
    public int getAge()
    {
        return age ;
    }
    
    /**
     * Sets the max age.
     */
    public void setAge(int age)
    {
        this.age = age;
    }
    
}

