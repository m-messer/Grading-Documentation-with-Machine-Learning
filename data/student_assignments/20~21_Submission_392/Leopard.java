
/**
 * A simple model of a leopard.
 * They age, move, breed, eat preys, and die.
 *
 * @version 2021.03.03.
 */
public class Leopard extends Predator {

    /**
     * Constructor for the class Leopard.
     * @param randomAge if true, the animal randomly gets a value for their age, if false, the age gets set to 0.
     * @param field the field of the animal.
     * @param location the location of the animal.
     */
    public Leopard(boolean randomAge, Field field, Location location) {
        super(field, location, randomAge, 21, 150, 0.65, 1, 20, 0.031);
    }

    /**
     * Returns a leopard with the given attributes.
     *
     * @param randomAge true if age is random, false otherwise
     * @param newField  field of the animal
     * @param location  location of the animal
     * @return leopard
     */
    public Leopard getCreature(Boolean randomAge, Field newField, Location location) {
        return new Leopard(randomAge, newField, location);
    }
    /**
     * Checks whether a given animal is of the same type as this animal - a leopard
     *
     * @param animal given animal
     * @return true if same type, false otherwise
     */
    protected boolean isTheSame(Object animal) {
        return this.getClass().isInstance(animal);
    }
}
