
/**
 * A simple plant class that grows its food value that it offers at a certain rate.
 *
 * @version 22/02/2019
 */
public class Plant
{
    // Food value that the plant offers.
    private int foodValue;
    // The rate that 
    private int growthRate;
    // Location the plant is at.
    private Location location;
    // Field the plant is on.
    private Field field;
    // Stores whether plant is alive or not.
    private boolean alive;
    // The max food value a plant can produce.
    private static final int MAX_FOOD_VALUE = 6;
    
    /**
     * Constructor for objects of class Plant
     */
    public Plant(Field field, Location location)
    {
        foodValue = 1;
        growthRate = 1;
        
        alive = true;
        
        this.field = field;
        setLocation(location);
    }
    
    // Grows food value at a certain rate.
    public void grow()
    {
        if((foodValue + growthRate) > MAX_FOOD_VALUE){
            foodValue = MAX_FOOD_VALUE;
        } else {
            foodValue += growthRate;
        }
    }
    
    // Returns whether plant is alive or not.
    public boolean isAlive() {
        return alive;
    }
    
    // Returns the max food value that the plant could offer.
    public int getFoodValue() {
        return MAX_FOOD_VALUE;
    }
    
    // Handles the death of a plant.
    public void setDead()
    {
        if(location != null) {
            alive = false;
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    // Sets a new location.
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
}
