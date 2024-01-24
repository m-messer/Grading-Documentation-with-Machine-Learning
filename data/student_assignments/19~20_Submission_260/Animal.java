import java.util.*;
import java.awt.Color;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 2020.02
 */
public abstract class Animal extends Organism
{
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;
    // the Color value of the animal (can be healthy, carrier, or infected)
    private Color color;

    // The age of the animal
    private int age;
    // The animal's food level, which is increased by eating prey.
    private int foodLevel;
    // Determines the animal's gender (True = female, False = Male)
    private boolean isFemale;
    // The probability that an animal is born female.
    private static final double SEX_RATIO = 0.5;

    // Stores the sleeping value of each animal (negative = awake, positive = number of sleeping steps)
    private int sleep = -1;
    // The disease of the animal (0, 1, or 2)
    private int diseaseState;

    // A shared random number generator
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new animal at location in field..
     * 
     * @param randomAge True if the animal is to be created with a random age, or false if not.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     */
    public Animal(boolean randomAge, Field field, Location location, int diseaseState)
    {
        super();
        this.field = field;
        setLocation(location);
        if(randomAge) {
            setAge(rand.nextInt(getMaxAge()));
            setFoodLevel(rand.nextInt(getMaxFullness()));
            setDiseaseState(rand.nextInt(3));
        }
        else {
            setAge(0);
            setFoodLevel(getMaxFullness());
            setDiseaseState(diseaseState);
        }
        isFemale = rand.nextDouble() >= SEX_RATIO;
    }

    /**
     * This is what the animal does most of the time: it looks for
     * prey. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newAnimals A list to return newly born animals.
     * @param isDay True if it is day, False if not.
     * @param harshWeather True if weather is harsh, False if not.
     */
    public void act(List<Actor> newAnimals, boolean isDay, boolean drought)
    {
        boolean isAwake = isAwake(isDay);
        incrementAge();
        if (drought && rand.nextDouble()<0.005) setDead(); // small chance to die by dehydration. This extends to aquatic animals too.
        if (isAwake) {
            decrementFoodLevel();
            if(isAlive()) {
                giveBirth(newAnimals);            
                // Move towards a source of food if found.
                Location newLocation = findFood();
                
                newLocation = move(newLocation);
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
        //otherwise, do nothing (the animal is currently asleep)
    }

    /**
     * Attempt to move, or do nothing if the location has already been supplied by findFood()
     * 
     * @param newLocation The location returned from findFood(), which could be null.
     * @return newLocation The new location, which could be null.
     */
    protected Location move(Location newLocation)
    {
        if(newLocation == null) { 
            // No food found - try to move to a free location.
            List<Location> newLocations = getField().getFreeAdjacentLocations(getLocation());
            Iterator<Location> it = newLocations.iterator();
            boolean searching = true;
            while(it.hasNext() && searching){
                Location loc = it.next();
                if(isWalkable(loc)){
                    newLocation = loc;
                    searching = false;
                }
            }
        }
        return newLocation;
    }

    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey animal is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        // Check for animals that have a null location 
        if(getLocation() == null){
            return null;
        }

        Field field = getField();
        List<Location> adjacent = getAdjacentLocations();
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object food = field.getOrganismAt(where);
            if(isWalkable(where) && food != null && isPrey(food) && isHungry()) {
                Organism prey = (Organism) food;
                if(prey.isAvailable()) { 
                    prey.getEaten();
                    setFoodLevel(prey.getFoodValue());
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        //Check the animal is female and therefore can give birth.
        if(isFemale){
            // A mate must be located in an adjacent location.
            // Get a list of adjacent locations.
            Field field = getField();
            List<Location> adjacent = getAdjacentLocations();

            Iterator<Location> it = adjacent.iterator();
            boolean lookingForMate = true;
            while(it.hasNext() && lookingForMate) {
                Location where = it.next();
                Object object = field.getAnimalAt(where);
                Animal animal = (Animal) object;
                if(isCompatible(animal) && !animal.getIsFemale() && animal.isAlive()) {
                    // New animals are born into adjacent locations.
                    // Get a list of adjacent free locations.
                    List<Location> free = field.getFreeAdjacentLocations(getLocation());
                    int births = breed();
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location loc = free.remove(0);
                        int diseaseState = diseaseTransmission(animal);
                        Actor young = newAnimal(false, field, loc, diseaseState);
                        newAnimals.add(young);
                    }
                    lookingForMate = false; //Terminate search once a mate is found.
                }
            }      
        }
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
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
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
     * Set animal's location to null.
     */
    protected void setLocationNull()
    {
        this.location = null;
    }

    /**
     * Set animal's field to null.
     */
    protected void setFieldNull()
    {
        this.field = null;
    }

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return isActive();
    }

    /**
     * Check whether the animal can be eaten.
     * @return true if the animal can be eaten
     */
    protected boolean isAvailable()
    {
        return isActive();
    }

    /**
     * The default action for an animal once it gets eaten is to be set to dead
     */
    public void getEaten()
    {
        setDead();
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        setInactive();
        if(getLocation() != null) {
            getField().clear(getLocation());
            setLocationNull();
            setFieldNull();
        }
    }

    /**
     * Determine the animal's current color based off it's health
     * @return bright color if healthy, medium dark if carrier, dark if infected
     */
    protected Color getColor()
    {
        return getColors()[diseaseState];
    }

    /**
     * Get the animal's disease state
     * @return int diseaseState.
     */
    protected int getDiseaseState()
    {
        return diseaseState;
    }

    /**
     * Set the animal's disease state
     * @param int diseaseState.
     */
    protected void setDiseaseState(int diseaseState)
    {
        this.diseaseState = diseaseState;
    }

    /**
     * Determine the disease state of the offspring generated by giveBirth().
     * @param The animal being mated with.
     * @return int diseaseState.
     */
    protected int diseaseTransmission(Animal animal)
    {
        int diseaseState = animal.getDiseaseState() + this.getDiseaseState();
        diseaseState = diseaseState > 2 ? 2 : diseaseState;
        return diseaseState;
    }

    /**
     * Return the animal's food level.
     * @return The animal's food level.
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void decrementFoodLevel()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Increase the animal's age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Set the animal's age
     * @param The animal's age
     */
    protected void setAge(int age)
    {
        this.age = age;
    }

    /**
     * Get the animal's age.
     * @return the animal's age
     */
    protected int getAge()
    {
        return age;
    }

    /**
     * Update the animal's food value. If it's over its max fullness, set to max level.
     * @param preyFoodValue The prey's food value.
     */
    protected void setFoodLevel(int preyFoodValue)
    {
        int newLevel = getFoodValue() + preyFoodValue;
        foodLevel = ( newLevel > getMaxFullness()) ? getMaxFullness() : newLevel;
    }

    /**
     * Get the animal's gender.
     * @return true if the animal is Female
     */
    protected boolean getIsFemale()
    {
        return isFemale;
    }

    /**
     * An animal can breed if it has reached the breeding age, has a foodlevel above the threshold, and happens to succesfully impregnate.
     * @return true if the animal can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return (getAge() >= getBreedingAge() && getFoodLevel() >= (getBreedingFullness() * getMaxFullness()) && rand.nextDouble() <= getBreedingProbability());
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Check if the animal is to fall asleep, and 
     * @param isDay - true if it is day, false if it is night
     * @return True if the animal is awake, false if it is asleep.
     */
    public boolean isAwake(boolean isDay)
    {
        boolean isAwake = --sleep < 0;
        if(!isDay && isAwake) {
            sleep = (rand.nextInt(10) - 2); // set sleep field to random integer, Max. sleep 8 steps. Will stay awake if integer is negative.
        }

        return isAwake;
    }

    /**
     * @param animal Animal to be checked.
     * @return True if the animal is a compatible mate (same species).
     */
    protected boolean isCompatible(Actor animal)
    {
        if(animal instanceof Animal){
            Animal mate = (Animal) animal;

            return this.getClass() == mate.getClass() && this.getDiseaseState() != 2 && mate.getDiseaseState() != 2;
        }
        else{
            return false;
        }
    }

    /**
     * First, the getWalkableTiles() will obtain the set of the specific animal's walkable tiles, and then
     * it will check if the location contains a tile within that set.
     * 
     * @param Location location.
     * @return True if the animal can walk on given location, otherwise false.
     */
    protected boolean isWalkable(Location location)
    {
        return getWalkableTiles().contains(getField().getTile(location).getClass());
    }

    /**
     * @return True if the animal is hungry (Food Level is less than 80% of Maximum Fullness), or False otherwise.
     */
    protected boolean isHungry()
    {
        return foodLevel < 0.8 * getMaxFullness();
    }

    /**
     * Call the animal's constructor and return the newly created animal object.
     * @param randomAge True if the animal is to be created with a random age, or false if not.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param diseaseState An integer representing the animal's current disease state.
     * @return the new animal created.
     */
    protected Actor newAnimal(boolean randomAge, Field field, Location location, int diseaseState)
    {
        Actor newAnimal = null;
        try { 
            newAnimal =this.getClass().getConstructor(boolean.class, Field.class, Location.class, int.class).newInstance(randomAge, field, location, diseaseState);
        }
        catch(ReflectiveOperationException e) {
            System.out.println(e);
        }
        return newAnimal;
    }

    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey animal is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected abstract List<Location> getAdjacentLocations();

    /**
     * @return The animal's walkable tiles (i.e. where it can walk) as a set
     */
    protected abstract Set<Class> getWalkableTiles();

    /**
     * @return the animal's max litter size (max amount of babies they can have)
     */
    protected abstract int getMaxLitterSize();

    /**
     * @return the animal's breeding probability (Given the animal's satisfy breeding conditions, what's the chance they'll breed)
     */
    protected abstract double getBreedingProbability();

    /**
     * @return the animal's breeding age (i.e. the minimum age for it to start breeding).
     */
    protected abstract int getBreedingAge();

    /**
     * Get the max age of the animal.
     */
    protected abstract int getMaxAge();

    /**
     * @return The animal's prey animals
     */
    protected abstract Set<Class> getPrey();

    /**
     * @param food An animal that is potentially eaten by this animal.
     * @return true, if the animal parameter is a prey species of this animal.
     */
    protected boolean isPrey(Object food) 
    {
        return getPrey().contains(food.getClass());
    }

    /**
     * @return The animal's max fullness value
     */
    protected abstract int getMaxFullness();

    /**
     * @return The animal's breeding fullness (percentage of max fullness they need to breed)
     */
    protected abstract double getBreedingFullness();

    /**
     * @return the animal's various health colors as array
     */
    protected abstract Color[] getColors();
}
