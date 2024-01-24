import java.util.Iterator;
import java.util.Random;
import java.util.List;

/**
 * A class for Cycads where I will simulate their growth with initial population where if it survives it can "create" other plants.
 *
 * @version 21/02/2022
 */
public class Cycad extends Plant
{
    /**
     * Constructor for objects of class Cycads
     */
    public Cycad(boolean randomAge, Field field, Location location)
    {
        super(field, location, 0, 40, 12, 0.086, 1, 4, true);
        if(randomAge) {
            this.setAge(this.getRand().nextInt(this.getMaxAge()));
        }
        else {
            this.setAge(0);
        }
    }
    
    
    /**
     * A method that allows the plant cycad to spread on the grid depending on chance.
     */
    protected void giveBirth(List<Organism> newCycad) 
    {
        // New cycads are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Cycad young = new Cycad(false, field, loc);
            newCycad.add(young);
        }
    }
    
    /**
     * 
     */
    protected int breed() 
    {
        int births = 0;
        if(this.getRand().nextDouble() <= this.getBreedingChance()) {
            births = this.getRand().nextInt(this.getMaxLitterSize()) + 1;
        }
        return births;
    }
}
