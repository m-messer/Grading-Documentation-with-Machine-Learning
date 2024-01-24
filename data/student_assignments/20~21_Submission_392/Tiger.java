import java.util.List;

/**
 * A simple model of a tiger.
 * They age, move, breed, eat preys, and die.
 *
 * @version 2021.03.03.
 */
public class Tiger extends Predator {

    /**
     * Constructor for the class Tiger.
     * @param randomAge if true, the animal randomly gets a value for their age, if false, the age gets set to 0.
     * @param field the field of the animal.
     * @param location the location of the animal.
     */

    public Tiger(boolean randomAge, Field field, Location location) {
        super(field, location, randomAge, 26, 100, 0.76, 2, 25, 0.03);
    }


    /**
     * Returns a tiger with the given attributes.
     *
     * @param randomAge true if age is random, false otherwise
     * @param newField  field of the animal
     * @param location  location of the animal
     * @return tiger
     */
    public Tiger getCreature(Boolean randomAge, Field newField, Location location) {
        return new Tiger(randomAge, newField, location);
    }

    /**
     * Tigers only move during the night. During the day, they still get hungrier and if infected, they get closer to death
     * @param newAnimals A list to receive newly born animals.
     * @param dayTime True if it is during the day, false if it is the night
     */
    @Override
    public void act(List<Creature> newAnimals, boolean dayTime) {
        if (!dayTime) {
            super.act(newAnimals, dayTime);
        }
        else {
            incrementHunger();
            incrementTimeUntilDeath();
        }
    }
    /**
     * Checks whether a given animal is of the same type as this animal - a tiger
     *
     * @param animal given animal
     * @return true if same type, false otherwise
     */
    protected boolean isTheSame(Object animal) {
        return this.getClass().isInstance(animal);
    }

}
