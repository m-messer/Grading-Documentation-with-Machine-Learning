
/**
 * A simple model of a tiger.
 * They age, move, breed, and die.
 *
 * @version 2021.03.03.
 */
public class Sloth extends Prey {


    /**
     * Constructor for the class Sloth.
     * @param randomAge if true, the animal randomly gets a value for their age, if false, the age gets set to 0.
     * @param field the field of the animal.
     * @param location the location of the animal.
     */
    public Sloth(boolean randomAge, Field field, Location location) {
        super(field, location, randomAge, 9, 37, 0.92, 6, 8, 18, 0.083);
    }

    /**
     * Returns a sloth with the given attributes.
     *
     * @param randomAge true if age is random, false otherwise
     * @param newField  field of the animal
     * @param location  location of the animal
     * @return sloth
     */
    public Sloth getCreature(Boolean randomAge, Field newField, Location location) {
        return new Sloth(randomAge, newField, location);
    }

    /**
     * Checks whether a given animal is of the same type as this animal - a sloth
     *
     * @param animal given animal
     * @return true if same type, false otherwise
     */
    protected boolean isTheSame(Object animal) {
        return this.getClass().isInstance(animal);
    }
}
