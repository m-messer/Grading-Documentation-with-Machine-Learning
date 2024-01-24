/**
 * A simple model of a tiger.
 * They age, move, breed, eat preys, and die.
 *
 * @version 2021.03.03.
 */
public class Capybara extends Prey {

    /**
     * Constructor for the class Capybara.
     * @param randomAge if true, the animal randomly gets a value for their age, if false, the age gets set to 0.
     * @param field the field of the animal.
     * @param location the location of the animal.
     */
    public Capybara(boolean randomAge, Field field, Location location) {
        super(field, location, randomAge, 9, 38, 0.87, 4, 8, 30, 0.085);
    }

    /**
     * Returns a capybara with the given attributes.
     *
     * @param randomAge true if age is random, false otherwise
     * @param newField  field of the animal
     * @param location  location of the animal
     * @return capybara
     */
    public Capybara getCreature(Boolean randomAge, Field newField, Location location) {
        return new Capybara(randomAge, newField, location);
    }

    /**
     * Checks whether a given animal is of the same type as this animal - a capybara
     *
     * @param animal given animal
     * @return true if same type, false otherwise
     */
    protected boolean isTheSame(Object animal) {
        return this.getClass().isInstance(animal);
    }

}
