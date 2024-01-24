import java.util.Iterator;
import java.util.List;

/**
 * A class representing plants. Plants can breed, die and be eaten but they do not move.
 *
 * @version 2021.03.03
 */
public class Plant extends Creature {
    private final int nutritionalValue = 10;


    /**
     * Create a new plant at location in field.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(boolean randomAge, Field field, Location location) {
        super(field, location, 0.76, 0.2, 8, 300);
        setLocation(location);
        this.setAge(0);
        setField(field);
    }

    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newCreatures A list to receive newly born animals.
     */
    @Override
    public void act(List<Creature> newCreatures, boolean dayTime) {
        incrementAge();
        if (isAlive()) {
            giveBirth(newCreatures);
            if (getInfected()) {
                Field field = getField();
                List<Location> adjacent = field.adjacentLocations(getLocation());
                Iterator<Location> it = adjacent.iterator();
                while (it.hasNext()) {
                    Location where = it.next();
                    Object creature = field.getObjectAt(where);
                    if (creature instanceof Plant) {
                        if (rand.nextInt(5) <= 3) ((Plant) creature).setInfected(true);
                    }
                    incrementTimeUntilDeath();
                }
            }
        }
    }

    /**
     * Returns whether or not a plant can breed.
     */
    @Override
    public boolean canBreed() {
        return true;
    }

    /**
     * Returns a creature with the given attributes.
     *
     * @param randomAge true if age is random, false otherwise
     * @param newField  field of the creature
     * @param location  location of the creature
     * @return creature
     */
    public Plant getCreature(Boolean randomAge, Field newField, Location location) {
        return new Plant(true, newField, location);
    }

    /**
     * @param newCreatures a place for new creatures
     */
@Override
    protected void giveBirth(List<Creature> newCreatures) {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Creature young = this.getCreature(false, field, loc);
            newCreatures.add(young);
        }
    }

    /**
     * Returns the nutritional value of a plant.
     *
     * @return nutritionalValue
     */
    public int getNutritionalValue() {
        return nutritionalValue;
    }
}
