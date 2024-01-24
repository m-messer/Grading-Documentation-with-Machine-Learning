package src;

import java.util.HashSet;

/**
 * The FruitFly species
 *
 * @version 2021.03.03
 */
public class FruitFly extends Animal
{
    /**
     * FruitFly constructor
     * @param randomAge True if the FruitFly should start with a random age.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public FruitFly(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, new Stats(40, 50, 10, 20, 35, 1, 2));
        HashSet<Class<?>> set = iniFood();
        super.setFood(set);
        setIsAwake(new boolean[]{false, true, true, true});
    }

    /**
     * Initialize the FruitFly food list and return it.
     * @return The food list.
     */
    private HashSet<Class<?>> iniFood(){
        HashSet<Class<?>> set = new HashSet<>();
        set.add(Mango.class);
        return set;
    }

    /**
     * Spawn a FruitFly in a given location.
     * @param loc The location in which to spawn the animal.
     * @return The FruitFly to be spawned.
     */
    @Override
    protected Animal spawnAnimal(Location loc) {
        return new FruitFly(false, super.getField(), loc);
    }
}
