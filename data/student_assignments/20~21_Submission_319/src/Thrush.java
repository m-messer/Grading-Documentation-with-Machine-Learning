package src;

import java.util.HashSet;

/**
 * The Thrush species
 *
 * @version 2021.03.03
 */
public class Thrush extends Animal
{
    /**
     * Thrush constructor
     * @param randomAge True if the Thrush should start with a random age.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public Thrush(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, new Stats(360, 1440, 20, 4, 4, 4, 0));
        HashSet<Class<?>> set = iniFood();
        super.setFood(set);
        setIsAwake(new boolean[]{false, true, true, true});
    }

    /**
     * Initialize the Thrush food list and return it.
     * @return The food list.
     */
    private HashSet<Class<?>> iniFood(){
        HashSet<Class<?>> set = new HashSet<>();
        set.add(FruitFly.class);
        set.add(DragonFly.class);
        return set;
    }

    /**
     * Spawn a Thrush in a given location.
     * @param loc The location in which to spawn the animal.
     * @return The Thrush to be spawned.
     */
    @Override
    protected Animal spawnAnimal(Location loc) {
        return new Thrush(false, super.getField(), loc);
    }
}
