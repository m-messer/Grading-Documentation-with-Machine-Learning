package src;

import java.util.HashSet;

/**
 * The Wolf species
 *
 * @version 2021.03.03
 */
public class Wolf extends Animal
{

    /**
     * Wolf constructor
     * @param randomAge True if the Wolf should start with a random age.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, new Stats(20, 300, 40, 6, 35, 3, 0));
        HashSet<Class<?>> set = iniFood();
        super.setFood(set);
        setIsAwake(new boolean[]{true, false, false, true});
    }

    /**
     * Initialize the Wolf food list and return it.
     * @return The food list.
     */
    private HashSet<Class<?>> iniFood(){
        HashSet<Class<?>> set = new HashSet<>();
        set.add(Thrush.class);
        set.add(Frog.class);
        return set;
    }

    /**
     * Spawn a Wolf in a given location.
     * @param loc The location in which to spawn the animal.
     * @return The Wolf to be spawned.
     */
    @Override
    protected Animal spawnAnimal(Location loc) {
        return new Wolf(false, super.getField(), loc);
    }
}
