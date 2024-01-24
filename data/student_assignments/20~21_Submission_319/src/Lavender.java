package src;

/**
 * The Lavender species
 *
 * @version 2021.03.03
 */
public class Lavender extends Plant{
    /**
     * Lavender constructor
     * @param randomSize True if the Lavender should start with a random size.
     * @param field The field to place it in
     * @param location The location to place it in
     */
    public Lavender(boolean randomSize, Field field, Location location){
        super(randomSize,field,location,new Stats(10,80,10,3,2,0,0));
    }

    /**
     * Spawn a Lavender in a given location.
     * @param location The location in which to spawn the plant.
     * @return The Lavender to be spawned.
     */
    @Override
    protected Plant newPlant(Location location){
        return new Lavender(false, getField(),location);
    }

}
