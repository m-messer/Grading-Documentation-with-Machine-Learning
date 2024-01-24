import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of creatures.
 *
 * @version 2021.03.03
 */
public abstract class Creature {
    private final double breedingProbability;
    private Field field;
    private Location location;
    private Boolean alive;
    private final int maxAge;
    private int age;
    private boolean isInfected;
    private int timeUntilDeath;
    private final double creationProbability;
    protected static final Random rand = Randomizer.getRandom();
    private final int maxLitterSize;

    /**
     * Create a new creature at location in field.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Creature(Field field, Location location, double breedingProbability, double creationProbability, int maxlittersize, int maxAge) {
        this.field = field;
        this.location = location;
        this.alive = true;
        this.breedingProbability = breedingProbability;
        this.creationProbability = creationProbability;
        this.maxLitterSize = maxlittersize;
        this.maxAge = maxAge;
        isInfected = false;


    }

    /**
     * Make this creature act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newCreatures A list to receive newly born animals.
     */
    public abstract void act(List<Creature> newCreatures, boolean dayTime);

    protected void setDead() {
        alive = false;
        if (location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Returns the field of a creature.
     *
     * @return field of the creature
     */
    protected Field getField() {
        return field;
    }

    /**
     * Sets the fieldof a creature to the given field.
     *
     * @param field new field
     */
    protected void setField(Field field) {
        this.field = field;
    }

    /**
     * Returns the location of a creature.
     *
     * @return location of the creature
     */
    protected Location getLocation() {
        return location;
    }

    /**
     * Place the creature at the new location in the given field.
     *
     * @param newLocation of the animal
     */
    protected void setLocation(Location newLocation) {
        if (location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Returns whether a creature is alive or not.
     *
     * @return true if is alive, false otherwise
     */
    protected boolean isAlive() {
        return alive;
    }

    /**
     * Makes the creature alive or dead.
     *
     * @param alive if true it makes the creature alive, dead othwerise
     */
    protected void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    protected int breed() {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= breedingProbability) {
            births = rand.nextInt(maxLitterSize) + 1;
        }
        return births;
    }

    /**
     * Returns the creation probability of a creature.
     *
     * @return creationProbability
     */
    public double getCreationProbability() {
        return creationProbability;
    }

    /**
     * A creature can breed if it has reached the breeding age.
     *
     * @return true if the creature can breed, false otherwise.
     */
    protected abstract boolean canBreed();

    /**
     * Returns a list of newly born animals.
     *
     * @param newAnimals list of newly born animals
     */
    protected abstract void giveBirth(List<Creature> newAnimals);

    /**
     * Returns a creature with the given attributes.
     *
     * @param randomAge true if age is random, false otherwise
     * @param newField  field of the animal
     * @param location  location of the animal
     * @return creature
     */
    public abstract Creature getCreature(Boolean randomAge, Field newField, Location location);

    /**
     * Returns the maximum age of a creature.
     *
     * @return maxAge maximum age of a creature
     */
    protected int getMaxAge() {
        return maxAge;
    }

    /**
     * Returns the age of a creature.
     *
     * @return age of creature
     */
    protected int getAge() {
        return age;
    }

    /**
     * Increments the age of a creature.
     */
    protected void incrementAge() {
        age++;
        if (age > this.maxAge) {
            setDead();
        }
    }

    /**
     * Sets the age of a creature to the given age.
     *
     * @param age new age of the creature
     */
    protected void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets the infected status of a creature to the given one
     *
     * @param isInfected new infected status of the creature, if true it is now infected, false othwerise.
     */
    protected void setInfected(boolean isInfected) {
        this.isInfected = isInfected;
    }

    /**
     * Returns whether a creature is infected or not.
     *
     * @return isInfected true if creature is infected, false otherwise
     */
    protected boolean getInfected() {
        return isInfected;
    }

    /**
     * Increment the time until death of the creature.
     */
    protected void incrementTimeUntilDeath() {
        timeUntilDeath--;
        if (timeUntilDeath == 0) {
            setDead();
        }
    }

    public void setTimeUntilDeath(int timeUntilDeath){
        this.timeUntilDeath=timeUntilDeath;
    }
}
