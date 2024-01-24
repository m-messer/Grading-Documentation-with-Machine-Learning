package src;

/**
 * The Flower species
 *
 * @version 2021.03.03
 */
public class Flower extends Plant{
    /**
     * Flower constructor
     * @param randomSize True if the Flower should start with a random size.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public Flower(boolean randomSize, Field field, Location location){
        super(randomSize,field,location,new Stats(10,80,10,3,2,0,0));
    }


    /**
     * Spawn a Flower in a given location.
     * @param location The location in which to spawn the plant.
     * @return The Flower to be spawned.
     */
    @Override
    protected Plant newPlant(Location location){
        return new Flower(false, getField(),location);
    }
}
