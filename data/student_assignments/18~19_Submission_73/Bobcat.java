import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * A simple model of a bobcat.
 * Bobcats age, move, eat hares, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Bobcat extends Animal
{
    // a map containing the prey of the bobcat and their foodvalues.
    private static final Map<Class, Integer> foodValue;

    static {
        Map<Class, Integer> map = new HashMap<>();

        map.put(Hare.class, 15);
        map.put(Squirrel.class, 13);

        foodValue = Collections.unmodifiableMap(map);       //initialises an unmodifiable map.
    }

    /**
     * Create a new bobcat. A bobcat may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the bobcat will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Bobcat(boolean randomAge, Field field, Location location)
    {
        super(field, location, randomAge, true, 31, 150, 0.7, 5, 33);
    }

    /** If there is suitable animal at the specified location, 
     * it gets eaten by the capacity for food the animal has available 
     * and its location is returned. In other case null is returned
     * @param where where the potential food is
     * @param field the field on which everything happens
     * @return location where food has been eaten
     */
    protected Location eat(Location where, Field field) {
        Object animal = field.getObjectAt(where).getKey();
        if(animal != null && foodValue.containsKey(animal.getClass())) {
            Animal prey = (Animal) animal;
            if(prey.isAlive()) { 
                increaseFoodLevel(foodValue.get(prey.getClass()));
                prey.setDead();
                return where;
            }
        }
        return null;
    }

    /**
     * Returns a new young bobcat
     * @param field
     * @param loc
     * @return new instance of young animal
     */
    public Animal getYoung(Field field, Location loc) {
        return new Bobcat(false, field, loc);
    }

}
