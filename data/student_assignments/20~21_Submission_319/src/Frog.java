package src;

import java.util.HashSet;

/**
 * The Frog species
 *
 * @version 2021.03.03
 */
public class Frog extends Animal{
    /**
     * Frog constructor
     * @param randomAge True if the Frog should start with a random age.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public Frog(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, new Stats(730, 2920, 13, 3, 4, 1, 0));
        HashSet<Class<?>> set = iniFood();
        super.setFood(set);
        setIsAwake(new boolean[]{false, true, true, true});
    }

    /**
     * Initialize the Frog food list and return it.
     * @return The food list.
     */
    private HashSet<Class<?>> iniFood(){
        HashSet<Class<?>> set = new HashSet<>();
        set.add(Butterfly.class);
        set.add(DragonFly.class);
        set.add(FruitFly.class);
        return set;
    }

    /**
     * Spawn a Frog in a given location.
     * @param loc The location in which to spawn the animal.
     * @return The Frog to be spawned.
     */
    @Override
    protected Animal spawnAnimal(Location loc) {
        return new Frog(false, super.getField(), loc);
    }
}
