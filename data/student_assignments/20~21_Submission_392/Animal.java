import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2021.03.03
 */
public abstract class Animal extends Creature {
    private static final Random rand = Randomizer.getRandom();
    //The animal's sex. True if female, false if male
    private final Boolean isFemale;
    // The age at which an animal can start to breed.
    private final int breedingAge;
    // The maximum number of births.
    private final int maxLitterSize;
    // number of steps an animal can go before it has to eat again
    private final int maxHungerLevel;
    // The animal's field
    private int hungerLevel;

    /**
     * Create a new animal at location in field.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location, boolean randomAge, int breedingAge, int maxAge, double breedingProbability, int maxLitterSize, int maxHungerLevel, double creationProbability) {
        super(field, location, breedingProbability, creationProbability, maxLitterSize, maxAge);
        this.setAlive(true);
        isFemale = rand.nextBoolean();
        this.breedingAge = breedingAge;
        this.maxLitterSize = maxLitterSize;
        this.maxHungerLevel = maxHungerLevel;
        setField(field);
        setLocation(location);
        this.setHungerLevel(getMaxHungerLevel());
        if (randomAge) {
            this.setAge(rand.nextInt(this.getMaxAge()));

        } else {
            this.setAge(0);

        }
    }

    /**
     * Make the animal look for food in the adjacent fields.
     *
     * @return location of food
     */
    public abstract Location findFood();

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newCreatures A list to receive newly born animals.
     */
    public void act(List<Creature> newCreatures, boolean dayTime) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            giveBirth(newCreatures);
            // Try to move into a free location.
            Location newLocation = findFood();
            if (newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                // Overcrowding.
                setDead();
            }
            if (isAlive() && getInfected()) {
                Field field = getField();
                List<Location> adjacent = field.adjacentLocations(getLocation());
                Iterator<Location> it = adjacent.iterator();
                while (it.hasNext()) {
                    Location where = it.next();
                    Object creature = field.getObjectAt(where);
                    if (creature instanceof Animal) {
                        if (rand.nextInt(9) <= 3) ((Animal) creature).setInfected(true);
                    }
                }
                incrementTimeUntilDeath();
            }
        }
    }

    /**
     * Increments the hunger of the animal. If the hunger level is below '0', the animal dies.
     */
    protected void incrementHunger() {
        hungerLevel--;
        if (hungerLevel <= 0) {
            setDead();
         }
    }


    /**
     * Check whether or not this creature is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newCreatures//A list to return newly born animals.
     */
    protected void giveBirth(List<Creature> newCreatures) {
        // New creatures are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        boolean alreadyHadKids=false;
        while (it.hasNext()) {
            Location where = it.next();
            Object creature = field.getObjectAt(where);
            if (this.getFemale() && this.isTheSame(creature)) {
                Animal sameAnimal = (Animal) creature;
                if (!sameAnimal.getFemale()&& !alreadyHadKids) {
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    int births = breed();
                    for (int b = 0; b < births && free.size() > 0; b++) {
                        Location loc = free.remove(0);
                        Creature young = this.getCreature(false, field, loc);
                        newCreatures.add(young);
                        alreadyHadKids=true;
                    }
                }
            }
        }
    }


    /**
     * An animal can breed if it has reached the breeding age.
     *
     * @return true if the animal can breed, false otherwise.
     */
    protected boolean canBreed() {
        return this.getAge() >= breedingAge;
    }

    /**
     * Returns whether an animal is a female or male.
     *
     * @return isFemale true if female, false otherwise.
     */
    protected Boolean getFemale() {
        return this.isFemale;
    }

    /**
     * Returns the maximum hunger level of an animal.
     *
     * @return maxHungerLevel
     */
    protected int getMaxHungerLevel() {
        return this.maxHungerLevel;
    }

    /**
     * Returns the current hunger level of an animal
     *
     * @return hungerLevel
     */
    protected int getHungerLevel() {
        return this.hungerLevel;
    }

    /**
     * Sets the hunger level of an animal to the given hunger level.
     *
     * @param hungerLevel new hunger level of the animal
     */
    protected void setHungerLevel(int hungerLevel) {
        this.hungerLevel = hungerLevel;
    }

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
     * Checks whether an animal is of the same type as the given animal-
     *
     * @param animal given animal
     * @return true if same type, false otherwise
     */
    protected abstract boolean isTheSame(Object animal);
}


