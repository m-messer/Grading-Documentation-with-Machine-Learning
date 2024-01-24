import java.util.List; 

/** 
 * A class that represents the very basics of an organism in the simulation. 
 *
 * @version 2021.02.28 
 */ 
public abstract class Organism { 
    // The organism's field.
    private Field field; 
    // The organism's position in the field.
    private Location location; 
    // Whether the organism is alive or not.
    private boolean alive; 

    /** 
     * Create a new organism at a location in the field. 
     *  
     * @param field The field currently occupied. 
     * @param location The location within the field. 
     */ 
    public Organism(Field field, Location location) { 
        alive = true; 
        this.field = field; 
        this.location = location; 
        setLocation(location); 
    } 

    /** 
     * Check whether the organism is alive or not. 
     *  
     * @return true if the organism is still alive. 
     */ 
    protected boolean isAlive() { 
        return alive; 
    } 

    /** 
     * Indicate that the organism is no longer alive. 
     * It is removed from the field. 
     */ 
    protected void setDead() { 
        alive = false; 

        if(location != null) { 
            field.clear(location); 
            location = null; 
            field = null; 
        } 
    } 

    /** 
     * @return location The organism's location. 
     */ 
    protected Location getLocation() { 
        return location; 
    } 

    /** 
     * Place the organism at the new location in the given field. 
     *  
     * @param newLocation The organism's new location. 
     */ 
    protected void setLocation(Location newLocation) { 
        if(location != null) { 
            field.clear(location); 
        } 

        location = newLocation; 
        field.place(this, newLocation); 
    } 

    /** 
     * @return field The organism's field. 
     */ 
    protected Field getField() { 
        return field; 
    } 
} 

