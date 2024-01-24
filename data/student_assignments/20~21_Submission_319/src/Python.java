package src;

import java.util.HashSet;

/**
 * The Python species
 *
 *  @version 2021.03.03
 */
public class Python extends Animal{
    /**
     * Python constructor
     * @param randomAge True if the Python should start with a random age.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public Python(boolean randomAge, Field field, Location location){
        super(randomAge, field, location, new Stats(20, 300, 30, 8, 16, 2, 0));
        HashSet<Class<?>> set = iniFood();
        super.setFood(set);
        setIsAwake(new boolean[]{true, false, false, false});
    }

    /**
     * Initialize the Python food list and return it.
     * @return The food list.
     */
    private HashSet<Class<?>> iniFood(){
        HashSet<Class<?>> set = new HashSet<>();
        set.add(Wolf.class);
        set.add(Frog.class);
        return set;
    }

    /**
     * Spawn a Python in a given location.
     * @param loc The location in which to spawn the animal.
     * @return The Python to be spawned.
     */
    @Override
    protected Animal spawnAnimal(Location loc){
        return new Python(false, super.getField(), loc);
    }
}
