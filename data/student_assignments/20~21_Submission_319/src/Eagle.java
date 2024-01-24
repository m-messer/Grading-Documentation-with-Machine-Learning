package src;

import java.util.HashSet;

/**
 * The Eagle species
 *
 * @version 2021.03.03
 */
public class Eagle extends Animal{
    /**
     * Eagle constructor
     * @param randomAge True if the Eagle should start with a random age.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public Eagle(boolean randomAge, Field field, Location location){
        super(randomAge, field, location, new Stats(30, 300, 30, 6, 0, 7, 0));
        HashSet<Class<?>> set = iniFood();
        super.setFood(set);
        setIsAwake(new boolean[]{false, true, true, false});
    }

    /**
     * Initialize the Eagle food list and return it.
     * @return The food list.
     */
    private HashSet<Class<?>> iniFood(){
        HashSet<Class<?>> set = new HashSet<>();
        set.add(Wolf.class);
        set.add(Frog.class);
        set.add(Python.class);
        set.add(Thrush.class);
        return set;
    }

    /**
     * Spawn a Eagle in a given location.
     * @param loc The location in which to spawn the animal.
     * @return The Eagle to be spawned.
     */
    @Override
    protected Animal spawnAnimal(Location loc){
        return new Eagle(false, super.getField(), loc);
    }
}
