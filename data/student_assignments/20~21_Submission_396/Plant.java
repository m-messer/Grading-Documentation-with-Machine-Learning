import java.util.List;
import java.util.Iterator;

/**
 *  A class representing shared characteristics of plants.
 *
 * @version 2021.02.28
 */
public abstract class Plant extends Organism {
    //The plant's growth
    private int growth;
    
    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
    }
    
    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newPlants A list to receive newly created plants.
     */
    abstract public void act(List<Plant> newPlants);
    
    /**
     * @return The grwoth probability of the plant.
     */
    abstract public double getGrowthProbability();
    
    /**
     * Creates a new plant and places it in the field.
     * 
     * @param field The field to place the new plant in.
     * @param loc The location to place the new plant in.
     * 
     * @return A newly created plant.
     */
    abstract public Plant createNewPlant(Field field, Location location);
    
    /**
     * Checks for any empty spaces to grow.
     * Creates a new plant if there is an empty space.
     * 
     * @param newPlants A list to receive newly created plants.
     */
    protected void grow(List<Plant> newPlants) {
        Field retrievedField = getField();
        List<Location> emptyLocations = retrievedField.getFreeAdjacentLocations(getLocation());
        
        for (Location freeLocation : emptyLocations) {
            Plant newPlant = createNewPlant(retrievedField, freeLocation);
            newPlants.add(newPlant);
        } 
    }
}
