import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2021.02.28
 */
public abstract class Animal extends Organism {
    // The animal's age.
    private int age;
    // The animal's food level.
    private int foodLevel;
    // The animal's gender.
    private char gender;
    // The animal's chance of randomly getting infected.
    private double infectionSpawn = 0.1;
    // The animal's chance that it becomes infected when neighbouring an infected animal.
    private double infectionSpread = 0.5;
    // Whether or not the animal is infected.
    private boolean isInfected;

    private static final Random randomGenerator = Randomizer.getRandom();

    /**
     * Create a new animal at location in field.
     * The animal will be created as either male or female with a 50% chance of each.
     * The animal may be infected with a disease when it spawns based on the
     * infectionSpawn field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field, location);
        boolean randomGender = randomGenerator.nextBoolean();
        gender = randomGender ? 'm' : 'f';

        double infection = randomGenerator.nextDouble();
        isInfected = infection <= infectionSpawn ? true : false;

        setFoodLevel();
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals);

    /**
     * @return The animal's maximum age before dying.
     */
    abstract public int getMaxAge();

    /**
     * @return The food value that the animal's food level is set to when it eats.
     */
    abstract public int getFoodValue();

    /**
     * @return The class that the animal eats.
     */
    abstract public Class getClassToEat();

    /**
     * Creates a new animal and places it in the field.
     * 
     * @param field The field to place the new animal in.
     * @param loc The location to place the new animal in.
     * 
     * @return A newly created animal.
     */
    abstract public Animal createNewAnimal(Field field, Location loc);

    /**
     * @return The breeding probability of the animal.
     */
    abstract public double getBreedingProbability();

    /**
     * @return The breeding age (miniumum age to breed) of the animal.
     */
    abstract public int getBreedingAge();

    /**
     * @return The maximum number of children the animal can create when breeding.
     */
    abstract public int getMaxLitterSize();

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
     * Sets the age of the animal.
     * 
     * @param newAge The new age of the animal.
     */
    protected void setAge(int newAge)
    {
        age = newAge;
    }

    /**
     * @return age The age of the animal.
     */
    protected int getAge()
    {
        return age;
    }

    /**
     * Increases how hungry the animal is by decreasing their food level.
     * The animal can die from starvation.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Sets the food level.
     */
    protected void setFoodLevel()
    {
        foodLevel = getFoodValue();
    }

    /**
     * @return foodLevel The food level of the animal.
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * @return gender The gender of the animal.
     */
    protected char getGender() {
        return gender;
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first live piece of food is eaten.
     * 
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        // Holds the class that the animal eats.
        Class toEat = getClassToEat();

        // If the class that the animal eats is null, 
        // then it is assumed that the animal constantly eats and cannot die of starvation.
        if (toEat == null) {
            setFoodLevel();
            return null;
        }

        // Iterates through the neighbouring locations and checks if there is a food source for them to eat.
        // If there is then, the food level is set to its maximum and the location of the food source is returned.
        // Otherwise, null is returned.
        while(it.hasNext()) {
            Location where = it.next();
            Object food = field.getObjectAt(where);

            if(toEat.isInstance(food)) {
                if (food instanceof Organism) {
                    Organism castedOrganism = (Organism) food;

                    if(castedOrganism.isAlive()) { 
                        castedOrganism.setDead();
                        setFoodLevel();
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Causes the animal to move into a different cell.
     * Either towards a food source or a neighbouring empty location.
     * The animal can also die from overcrowding if there are no empty neighbouring cells.
     */
    protected void move() {
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

    /**
     * @return A string that shows that there is an animal object in a location.
     */
    public String toString(){
        if (this.isAlive()) {
            return "Animal at location: " + getLocation().toString();
        }

        return "Animal is dead and has no location";
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newAnimals A list to return newly born animals.
     */
    protected void giveBirth(List<Animal> newAnimals)
    {
        // New animals are born into adjacent locations.
        Field field = getField();
        // Get a list of adjacent free locations.
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();

        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);            
            Animal young = createNewAnimal(field, loc);
            newAnimals.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * 
     * @return births The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && randomGenerator.nextDouble() <= getBreedingProbability()) {
            births = randomGenerator.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * An animal can breed if it has reached the breeding age and found a mate.
     * 
     * @return true If this animal satisfies the conditions for breeding.
     */
    private boolean canBreed()
    {
        return getAge() >= getBreedingAge() && foundMate();
    }

    /**
     * Tries to find a mate in a neighbouring cell. 
     * The mate must be from the same species as the current animal
     * and be male (since only female animals can give birth).
     * 
     * @return true If the animal has found a suitable mate.
     */
    private boolean foundMate() {
        Field field = getField();
        List<Location> neighbours = field.adjacentLocations(getLocation());

        for(Location location: neighbours) {
            Object currentObject = field.getObjectAt(location);
            Class thisClass = getClass();

            // Checks if the neighbouring object is of the same animal type.
            if (thisClass.isInstance(currentObject)) {
                Animal currentAnimal = (Animal) currentObject;
                if (currentAnimal.getGender() == 'm') {
                    return true;
                }
            }
        }

        return false;
    }    

    /**
     * By default, the animal will act the same at night time.
     * 
     * @param newAnimals List to store new animals created.
     */
    public void nightBehaviour(List<Animal> newAnimals) {
        act(newAnimals);
    }

    /**
     * Checks if the animal is neighbouring another animal that is infected.
     * If they are, then the animal also becomes infected.
     */
    protected void becomeInfected()
    {
        if(isAlive()){
            Field field = getField();
            List<Location> neighbours = field.adjacentLocations(getLocation());

            for(Location location: neighbours) {
                Object currentObject = field.getObjectAt(location);
                if(currentObject != null){
                    if (currentObject instanceof Animal) {
                        Animal neighbourAnimal = (Animal) currentObject;
                        if(neighbourAnimal.getIsInfected()) {
                            setInfected();
                        }
                    }
                }   
            }
        } 
    }

    /**
     * The animal becomes infected based on the probability of the infection spreading.
     * (This only happens if there is a neighbouring infected animal).
     */
    private void setInfected()
    {
        double infection = randomGenerator.nextDouble();
        isInfected = infection <= infectionSpread ? true : false;
    }

    /**
     * @return isInfected Whether or not an animal is infected or not.
     */
    private boolean getIsInfected()
    {
        return isInfected;
    }

    /**
     * If the animal is infected, then they age twice as fast.
     */
    protected void checkInfection()
    {
        if(isAlive()){
            if (isInfected){
                incrementAge();
            }   
        }
    }
}
