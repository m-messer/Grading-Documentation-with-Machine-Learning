package src;

import java.util.HashSet;

/**
 * The DragonFly species
 *
 * @version 2021.03.03
 */
public class DragonFly extends Animal{
    /**
     * DragonFly constructor
     * @param randomAge True if the DragonFly should start with a random age.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public DragonFly(boolean randomAge, Field field, Location location){
        super(randomAge, field, location, new Stats(60, 120, 20, 15, 4, 1, 0));
        HashSet<Class<?>> set = iniFood();
        super.setFood(set);
        setIsAwake(new boolean[]{false, true, true, true});
    }

    /**
     * Initialize the DragonFly food list and return it.
     * @return The food list.
     */
    private HashSet<Class<?>> iniFood(){
        HashSet<Class<?>> set = new HashSet<>();
        set.add(Butterfly.class);
        set.add(FruitFly.class);
        return set;
    }

    /**
     * Spawn a DragonFly in a given location.
     * @param loc The location in which to spawn the animal.
     * @return The DragonFly to be spawned.
     */
    @Override
    protected Animal spawnAnimal(Location loc){
        return new DragonFly(false, super.getField(), loc);
    }
}