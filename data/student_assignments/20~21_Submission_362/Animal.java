import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A class representing shared characteristics of animals. 
 *
 * @version 12.02.21
 */
public abstract class Animal extends Organism
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Records whether the animal is infected or not.
    private boolean infected = false; 
    // Records the animal's health value.
    private int health = 10;

    /**
     * Create an Animal. An Animal can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * The animal has a 1 in 10 chance of being infected.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field, location);

        Random randomInfectedGenerator = new Random(); 
        int randInfected = randomInfectedGenerator.nextInt(10); 
        if (randInfected == 5){
            infected = true;
        }
    }

    /**
     * If age exceeds max age, this animal dies. 
     */
    protected void dieOfAge()
    {
        if(getAge() > getMaxAge()) {
            setDead();
        }
    }

    /**
     * If food level is less than or equal to zero, this animal dies of hunger. 
     */
    protected void dieOfHunger()
    {
        if(getFoodLevel() <= 0) {
            setDead();
        }
    }

    /**
     * Generate a number representing the number of births,
     * if this animal can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * An animal can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }

    /**
     *Returns whether this animal is infected or not.
     *
     * @return Whether this animal is infected or not.
     */
    public boolean isInfected()
    {
        return infected;
    }
    
    /**
     * Set this animal's infected field to true 
     *
     */
    public void setInfected()
    {
        infected = true;
    }

    /**
     * Returns the health value of this animal.
     *
     * @return The health value of this animal.
     */
    private int getHealth()
    {
        return health;
    }

    /**
     * Increments or decrements this animal's health value.
     *
     */
    public void adjustHealth()
    {
        Random randomHealthGenerator = new Random(); 
        int randHealth = randomHealthGenerator.nextInt(2); 
        if (randHealth == 0){
            health++;
        }
        else{
            health--;
        }

        if(health<=0){
            setDead();
        }
    }

    /**
     * If a neighbouring animal is infected, 
     * this animal becomes infected as well.
     * There is a 1 in 10 chance this is successful. 
     *
     * @param neighbour An animal of the same type in an adjacent location
     */
    public void infect(Object neighbour)
    {
        Random randomInfectedGenerator = new Random(); 
        int randInfected = randomInfectedGenerator.nextInt(10); 
        if (randInfected == 5){
            Animal animal = (Animal) neighbour;
            if(!animal.isInfected()) { 
                animal.setInfected();   
            }
        }
    }

    //ABSTRACT METHODS

    /**
     * This is what the animal does most of the time: it hunts. 
     * In the process, it might breed, die of hunger, or die of old age.
     * 
     * @param newanimals A list to return newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    /**
     * Checks neighbouring locations. 
     * If another animal of the opposite gender is found, calls giveBirth method
     * If another animal of the same gender is found, return false
     * If another animal/plant type is found, return false
     *
     */
    abstract public boolean meet();

    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    abstract public Location findFood();

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newanimals A list to return newly born animals.
     */
    abstract public void giveBirth(List<Animal> newAnimals);
    
    /**
     * Look for animals of the same type in adjacent locations. 
     * If they are found, try to infect them. 
     */
    abstract public void spreadDisease();

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    abstract public void incrementHunger();

    /**
     * Increase the age. This could result in the animal's death.
     */
    abstract public void incrementAge();

    //ABSTRACT ACCESSOR METHODS 

    /**
     * Returns the age at which the animal can start to breed.
     *
     * @return the age at which the animal can start to breed
     */
    abstract public int getBreedingAge();

    /**
     * Returns the age to which this animal can live.
     *
     * @return the age to which this animal can live
     */
    abstract public int getMaxAge();

    /**
     * Returns the likelihood of this animal breeding.
     *
     * @return the likelihood of this animal breeding
     */
    abstract public double getBreedingProbability();

    /**
     * Returns the maximum number of births.
     *
     * @return the maximum number of births
     */
    abstract public int getMaxLitterSize();

    /**
     * Returns the food value of the animal. 
     * In effect, this is the food value its predator gets when the animal is eaten.
     *
     * @return the food value of the animal
     */
    abstract public int getFoodValue();

    /**
     * Returns the highest food value of whatever organism(s) the animal eats.
     *
     * @return the highest food value of whatever organism(s) the animal eats
     */
    abstract public int getPreyFoodValue();

    /**
     * Returns the animal's age.
     *
     * @return the animal's age
     */
    abstract public int getAge();

    /**
     * Returns the animal's food level, which is increased by eating another organism. 
     *
     * @return the animal's food level
     */
    abstract public int getFoodLevel();

    /**
     * Returns the animal's sex.
     *
     * @return the animal's sex
     */
    abstract public String getSex();
}
