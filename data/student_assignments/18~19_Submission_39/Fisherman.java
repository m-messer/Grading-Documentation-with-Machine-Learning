import java.util.List;
import java.util.Iterator;
/**
 * Write a description of class Fisherman here.
 *
 * @version (a version number or a date)
 */
public  class Fisherman extends Human 
{
    // The field that the fisherman is in
    private Field field;
    // The location in the field
    private Location location;
    // The age to which a fisherman can live.
    private static final int MAX_AGE = 80;
    //private int age;
     /**
     * Create a fisherman.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fisherman(Field field, Location location)
    {
         super(field, location);
    }
    
    /**
     * This is what the fisherman does most of the time: it hunts farlowellas,
     *  In the process it might die by a bear of old age.
     */
    public void act(List<Aquatic> newHumans)
    {
            incrementAge();
            if(isAlive())
            {
            // Move towards a source of food if found.
            Location newLocation = animalFishing();
            if(newLocation == null) 
            { 
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) 
            {
                setLocation(newLocation);
            } else 
            {
                setDead();
            }
        }
    }
    
    /**
     * Tell the fisherman to look for farlowellas in a 5 square radius to its current location.
     * 
     * @param location Where in the field it is located.
     *
     */
    private Location animalFishing()
    {
        Field field = getField();
        
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) 
        {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal instanceof Farlowella) 
            {
                Farlowella farlowella = (Farlowella) animal;
                if (farlowella.isAlive()) 
                {
                    farlowella.setDead();
                    return where;
                }
            }
        }
        return null;
    }
   
    /**
     * Returns the fisherman's max age.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
}
