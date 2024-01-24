import java.util.*;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2020.02.23
 */
public abstract class Animal implements Actor
{
    //This is all the animals and plants value that gets 
    //added to the animals hunger level that eat them
    private static final int BUSH_FOOD_VALUE = 10;
    private static final int ACACIA_FOOD_VALUE = 10;
    private static final int ZEBRA_FOOD_VALUE = 15;
    private static final int BABOON_FOOD_VALUE = 12;
    private static final int GAZELLE_FOOD_VALUE = 10;

    //Whether the animal is alive or not.
    private boolean alive;
    //The animal's field.
    private Field field;
    //The animal's position in the field.
    private Location location;
    //A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //Check whether the animal is female
    private boolean isFemale;
    //The animals age
    private int age;
    //Checks if the animal is infected
    private boolean isInfected;
    //Checks the current food level fo the animal
    private int foodLevel;
    //Checks animals health in case of infection
    private int healthLevel;

    /**
     * Create a new animal at location in field.
     * 
     * @param randomStats if True starts with random age, hunger.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomStats, Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        this.isFemale = rand.nextBoolean();
        if(randomStats) {
            age = rand.nextInt(getMaxAge());
            foodLevel = rand.nextInt(20) + 1;
        }
        else {
            age = 0;
            this.foodLevel = 15;
        }
        isInfected = false;

    }

    /**
     * Checks if the animal is currently infected
     * 
     * @return true if the animal is infected
     */
    public boolean isInfected() {
        return isInfected;
    }

    /**
     * Makes an animal infected
     */
    public void setInfection() {
        isInfected = true;
        healthLevel = 5;
    }

    /**
     * Makes an animal Female (true)
     * or Male (false)
     * 
     * @param isFemale if True animal becomes Female
     */
    public void setGendre(boolean isFemale)
    {
        this.isFemale = isFemale;
    }

    /**
     * Checks if the animal is a Female
     * 
     * @return true if the animal is female
     */
    public boolean getGendre()
    {
        return isFemale;
    }

    /**
     * Gets the specific age where animals can start breeding
     */
    abstract public int getBreedingAge();

    /**
     * Gets the specific probability of the animal to breed
     */
    abstract public double getBreedingProbability();

    /**
     * Gets the max age an animal can be alive
     */
    abstract public int getMaxAge();

    /**
     * Gets the maximum amount of babies an animal can have
     */
    abstract public int getMaxLitterSize();

    /**
     * Check whether the animal is alive or not.
     * 
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Decreases the food level of the animal
     * making the animal hungry until it dies of hunger
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Decreases the health level of the animal 
     * once it gets infected, after a few steps,
     * the animal dies
     */
    protected void incrementInfection()
    {
        healthLevel--;
        if(healthLevel <= 0) {
            setDead();
        }
    }

    /**
     * This is what the Actors do most of the time: it hunts or reproduces.
     * In the process, it might breed, die of hunger,
     * or die of old age.
     * 
     * @param newActors A list to return newly born Actors.
     */
    public void act(List<Actor> newActors)
    {
        act();
        if(isAlive()) 
        {
            //gives probability of birth
            giveBirth(newActors);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Makes the Animal look for food
     */
    abstract protected Location findFood();

    /**
     * Gets the food level of the animal
     * 
     * @return current food level
     */
    public int getFoodLevel() {
        return foodLevel;
    }

    /**
     * sets the food level to a specific value
     * 
     * @param foodLevel sets food value
     */
    protected void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return int The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && (rand.nextDouble() <= getBreedingProbability())) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            if(this instanceof Gazelle) {
                Gazelle young = new Gazelle(false, field, loc);
                newAnimals.add(young);
            }
            else if(this instanceof Zebra) {
                Zebra young = new Zebra(false, field, loc);
                newAnimals.add(young);
            }
            else if(this instanceof Baboon) {
                Baboon young = new Baboon(false, field, loc);
                newAnimals.add(young);
            }
            else if(this instanceof Giraffe) {
                Giraffe young = new Giraffe(false, field, loc);
                newAnimals.add(young);
            }
            else if(this instanceof Lion) {
                Lion young = new Lion(false, field, loc);
                newAnimals.add(young);
            }
            else if(this instanceof Leopard) {
                Leopard young = new Leopard(false, field, loc);
                newAnimals.add(young);
            }
        }
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Makes the animals live throughout the simulation
     * depending on the steps, each time increasing their
     * age, hunger, or death if sick, old, or hungry
     * 
     * @Override method
     */
    public void act() {
        incrementAge();
        incrementHunger();
        if(isInfected()) {
            incrementInfection();
        }
        if(isInfected && isAlive()) {
            doSpreadInfection();
        }
    }

    /**
     * Spreads the infection of an animal to their neighbouring species
     */
    protected void doSpreadInfection() {
        // Get a list of adjacent locations.
        Field field = getField();
        List<Location> adjacents = field.adjacentLocations(getLocation());
        for (int b = 0; b < adjacents.size() && adjacents.size() > 0; b++) {
            //Locates neigbours of the same species
            //in adjacent locations and infects them
            Location loc = adjacents.remove(b);
            if(field.getObjectAt(loc) != null) {
                Object object = field.getObjectAt(loc);
                if (object instanceof Animal) {
                    Animal animal = (Animal) object;
                    if( ((animal instanceof Gazelle) && (this instanceof Gazelle)) ||
                    ((animal instanceof Zebra) && (this instanceof Zebra)) ||
                    ((animal instanceof Baboon) && (this instanceof Baboon)) ||
                    ((animal instanceof Giraffe) && (this instanceof Giraffe)) ||
                    ((animal instanceof Lion) && (this instanceof Lion)) ||
                    ((animal instanceof Leopard) && (this instanceof Leopard)) ) {
                        animal.setInfection();
                    }
                }
            }
        }
    }

    /**
     * Checks if the animal is infected and able to spread to 
     * its neighbours
     * 
     * @return boolean True if animal can infect others
     */
    protected boolean canSpreadInfection()
    {
        return isInfected;
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Set specific age for the animal
     * @param age preferred age of the animal
     */
    protected void setAge(int age)
    {
        this.age = age;
    }

    /**
     * Get current age of the animal
     * @return current age of animal
     */
    protected int getAge() 
    {
        return age;
    }

    
    /**
     * An animal can breed if it has reached the breeding age
     * return True if the animal is old enough to breed
     */
    protected boolean canBreed()
    {
        List<Location> adjacentLocations = field.adjacentLocations(getLocation());
        for(Location loc : adjacentLocations) {
            if(field.getObjectAt(loc) != null) {
                Object object = field.getObjectAt(loc);
                if (object instanceof Animal) {
                    Animal animal = (Animal) object;
                    if( ((animal instanceof Gazelle) && (this instanceof Gazelle)) ||
                    ((animal instanceof Zebra) && (this instanceof Zebra)) ||
                    ((animal instanceof Baboon) && (this instanceof Baboon)) ||
                    ((animal instanceof Giraffe) && (this instanceof Giraffe)) ||
                    ((animal instanceof Lion) && (this instanceof Lion)) ||
                    ((animal instanceof Leopard) && (this instanceof Leopard)) ) {
                        if ((getAge() >= getBreedingAge() && animal.getAge() >= animal.getBreedingAge())
                        && ((this.isFemale && !animal.getGendre()) ||
                            (!this.isFemale && animal.getGendre()))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets the food value of the bush
     * @return food value of bush
     */
    public static int getBushFoodValue() {
        return BUSH_FOOD_VALUE;
    }

    /**
     * Gets the food value of the acacia
     * @return food value of acacia
     */
    public static int getAcaciaFoodValue() {
        return ACACIA_FOOD_VALUE;
    }

    /**
     * Gets the food value of the zebra
     * @return food value of zebra
     */
    public static int getZebraFoodValue() {
        return ZEBRA_FOOD_VALUE;
    }

    /**
     * Gets the food value of the baboon
     * @return food value of baboon
     */
    public static int getBaboonFoodValue() {
        return BABOON_FOOD_VALUE;
    }

    /**
     * Gets the food value of the gazelle
     * @return food value of gazelle
     */
    public static int getGazelleFoodValue() {
        return GAZELLE_FOOD_VALUE;
    }
}