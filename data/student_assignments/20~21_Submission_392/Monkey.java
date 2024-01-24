import java.util.List;


/**
 * A simple model of a monkey.
 * They age, move, breed,  and die.
 *
 * @version 2021.03.03.
 */
public class Monkey extends Prey {


    /**
     * Constructor for the class Monkey.
     * @param randomAge if true, the animal randomly gets a value for their age, if false, the age gets set to 0.
     * @param field the field of the animal.
     * @param location the location of the animal.
     */

    //public Animal(Field field, Location location,boolean isFemale, int breedingAge, int maxAge, double breedingProbability,int maxLitterSize,int hungerLevel)
    public Monkey(boolean randomAge, Field field, Location location) {
        super(field, location,randomAge, 11, 40, 0.99, 7, 10, 18, 0.0881);
    }

    /**
     * Returns a monkey with the given attributes.
     *
     * @param randomAge true if age is random, false otherwise
     * @param newField  field of the animal
     * @param location  location of the animal
     * @return monkey
     */
    public Monkey getCreature(Boolean randomAge, Field newField, Location location) {
        return new Monkey(randomAge, newField, location);
    }

    /**
     * Monkeys only move during the day. During the night, they still get hungrier and if infected, they get closer to death.
     * @param newAnimals A list to receive newly born animals.
     * @param dayTime True if it is during the day, false if it is the night
     */
    @Override
    public void act(List<Creature> newAnimals, boolean dayTime) {
        if (dayTime) {
            super.act(newAnimals, dayTime);
         }
        else {
            incrementHunger();
            incrementTimeUntilDeath();
        }
    }
    /**
     * Checks whether a given animal is of the same type as this animal - a monkey
     *
     * @param animal given animal
     * @return true if same type, false otherwise
     */
    protected boolean isTheSame(Object animal) {
        return this.getClass().isInstance(animal);
    }
}
