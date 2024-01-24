import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals.
 *
 */
public abstract class Animal extends Base
{
    // The animals food level, which is increased by eating other animals.
    protected int foodLevel; 
    // The sex of the animal.
    protected boolean sexMale;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        alive = true;
        this.field = field;
        setLocation(location);

    }

    /**
     * Check to see if the sex is male.
     */
    public boolean getSexMale() 
    {
        return this.sexMale;
    }

    /**
     * Increment the hunger of the animal. This could result in the animals death if their hunger value drops to 0.
     */
    protected void incrementHunger()
    {
        foodLevel--;

        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Return true if the animal in the adjacent cell is male & current cell female.
     */
    protected boolean canBreed()
    {
        return (isMaleAdjacent() && !this.sexMale);
    }

    /**
     * Check if the animal in the adjacent cell is male
     * And of the same class
     */
    protected boolean isMaleAdjacent()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal!=null){
                Base animalCasted = (Base) animal;
                if(this.getClass().equals(animalCasted.getClass())){
                    if(animalCasted.getSexMale()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    abstract Base returnMyType(boolean randomAge, Field field, Location location);
}

