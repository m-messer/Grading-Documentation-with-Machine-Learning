import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @version 2020.02.22
 */
public abstract class Animal extends Actor
{
    // The gender of the animal.
    private boolean gender;
    // The health of the animal.
    private int health;
    // To check if the animal is infected.
    private boolean isInfected;

    private Random rand;

    /**
     * Create a new animal at location in field.
     * 
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field, location);
        rand = new Random();
        setRandomGender();
        health= 10; // default health level, will be changed later
        isInfected = false; // animals are initially not infected 
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newAnimals    A list to receive newly born animals.
     * @param isDayTime     A boolean track the time of day i.e. day time/ night time.
     * @param weather       A String to keep track of the weather.
     */
    abstract public void act(List<Animal> newAnimals, boolean isDayTime, String weather);

    /**
     * Generate a number representing the number of births,
     * if the animal can breed.
     * 
     * @return int  the number of births (may be zero).
     */
    public int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * An animal can breed if it has reached its breeding age.
     * 
     * @return boolean  true if the animal can breed.
     */
    public boolean canBreed()
    {
        return getAge() >= getBreedingAge();
    }

    /**
     * Return the breeding age of this animal.
     * 
     * @return int  The breeding age of this animal.
     */
    abstract protected int getBreedingAge();

    /**
     * Return the breeding probability of this animal.
     * 
     * @return double   The breeding probability of this animal.
     */
    abstract protected double getBreedingProbability();

    /**
     * Return the max litter size of this animal.
     * 
     * @return int  The max litter size of this animal.
     */
    abstract protected int getMaxLitterSize();

    /**
     * Return the age of this animal.
     * 
     * @return int  The age of this animal.
     */
    abstract protected int getAge();

    /**
     * Generate and set a random gender for the animal.
     */
    protected void setRandomGender()
    {
        gender = rand.nextBoolean();
    }

    /**
     * Return the animal's gender.
     * 
     * @return boolean  The animal's gender, true if male and false if female.
     */
    public boolean getGender()
    {
        return gender;
    }

    /**
     * Set the animal as having infection.
     */
    protected void setInfected()
    {
        isInfected = true;
    }

    /**
     * Returns whether the animal is infected or not.
     * 
     * @return The infection state of the animal.
     */
    protected boolean isInfected()
    {
        return isInfected;
    }

    /**
     * Set the health of the animal.
     * 
     * @param healthLevel   int for the health level of the animal.
     */
    protected void setHealth(int healthLevel)
    {
        health = healthLevel;
    }

    /**
     * To check whether the animal is in good health.
     * If the health level is less than or equal to 0 it's going to die
     */
    abstract protected void checkHealth();

    /**
     * Update the health level of the animal.
     * If an infected animal or plant is eaten then the 
     * health level of the animal is decreased by 1. 
     */
    protected void decreaseHealth()
    {
        setHealth(getHealth()-1);
    }

    /**
     * Return the health level of the animal.
     * 
     * @return health Returns the health level of the animal.
     */
    protected int getHealth()
    {
        return health;
    }
}
