package src;

import java.util.HashSet;

/**
 * The Butterfly species
 *
 * @version 2021.03.03
 */
public class Butterfly extends Animal{
    /**
     * Butterfly constructor
     * @param randomAge True if the Butterfly should start with a random age.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public Butterfly(boolean randomAge, Field field, Location location){
        super(randomAge, field, location, new Stats(52, 240, 15, 4, 30, 1, 4));
        HashSet<Class<?>> set = iniFood();
        super.setFood(set);
        setIsAwake(new boolean[]{false, true, true, true});
    }

    /**
     * Initialize the Butterfly food list and return it.
     * @return The food list.
     */
    private HashSet<Class<?>> iniFood(){
        HashSet<Class<?>> set = new HashSet<>();
        set.add(Lavender.class);
        set.add(Flower.class);
        return set;
    }

    /**
     * Spawn a Butterfly in a given location.
     * @param loc The location in which to spawn the animal.
     * @return The Butterfly to be spawned.
     */
    @Override
    protected Animal spawnAnimal(Location loc){
        return new Butterfly(false, super.getField(), loc);
    }
}
