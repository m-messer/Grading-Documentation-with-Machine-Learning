package src;

/**
 *  A Plant type - Mango
 *
 *  @version 2021.03.03
 */

public class Mango extends Plant{

    /**
     * Basic constructor - set the field and the location of the plant, choose if the size of the plant is assigned randomly
     * @param randomSize - boolean true if the size should be assigned randomly
     * @param field - the field of this plant
     * @param location - the location of this plant
     */
    public Mango(boolean randomSize, Field field, Location location){
        super(randomSize,field,location,new Stats(15,80,20,4,6,0,0));
    }

    /**
     * Override the newPlant method - create a child of this plant
     * @param location - the location of the new plant
     * @return the new plant
     */
    @Override
    protected Plant newPlant(Location location){
        return new Mango(false, getField(),location);
    }
}
