import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a squirrel.
 * Squirrels age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Squirrel extends Animal
{
    /**
     * Create a new squirrel. A squirrel may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the squirrel will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Squirrel(boolean randomAge, Field field, Location location)
    {
        super(field, location, randomAge, false,  12, 60, 0.9, 6, 11);
    }

    /** If there is a plant higher than 2 at the specified location, 
     * it gets eaten by the capacity for food the animal has available 
     * and its location is returned. In other case null is returned
     * @param where where the potential food is
     * @param field the field on which everything happens
     * @return location where food has been eaten
     */
    protected Location eat(Location where, Field field) {
        Object plant = field.getObjectAt(where).getValue();
        if(plant != null) {
            Plant prey = (Plant) plant;
            if(prey.getLength() >= 2) { 
                int amount = Math.min(getFoodCapacity(), (int) prey.getLength());
                increaseFoodLevel(amount);
                prey.getEaten(amount);
                return where;

            }
        }
        return null;
    }

    /**
     * Returns a new young squirrel
     * @param field
     * @param loc
     * @return new instance of young animal
     */
    public Animal getYoung(Field field, Location loc) {
        return new Squirrel(false, field, loc);
    }
}

