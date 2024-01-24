import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 *  A class representing shared characteristics of animals.
 *  Animals may behave differently according to time of day and weather. They
 *  can propagate if they meet the opposite gender. Animals compete for food
 *  if they share the same food source. They may die from old age, 
 *  overcrowding, lack of energy or being eaten if it is a prey.
 *
 * @version 2021.3.3
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // True if the animal is female, false if it is male.
    private boolean isFemale;
    //The animal's age.
    private int age;
    //The animals food level, which is increased by eating.
    private int energyLevel;
    //Whether the animal has been infected by a disease.
    private boolean isInfected;
    
    // A random number generator to generate an energy level and a gender.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a new animal at location in field.
     * 
     * @param randomAge A boolean to determine if the animal should
     * spawn with a random age or as a newborn.
     * @param isFemale A boolean indicating if the animal is female.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, boolean isFemale, Field field, Location location)
    {
        alive = true;
        this.isFemale = isFemale;
        this.field = field;
        setLocation(location);
        isInfected = false;

        if(randomAge) {
            age = rand.nextInt(getMaxAge());
            energyLevel = rand.nextInt(getHungry());
        }
        else {
            age = 0;
            energyLevel = getHungry();
        }
    }

    /**
     * Make this animal act - that is: it will age, lose enerygy, 
     * change between a sleeping and an awake state, reproduce, 
     * find food, and move. If it cannot find any food and 
     * it cannot move, then it dies due to overcrowding.
     * @param newAnimals A list to receive newly born animals.
     * @param isDay The time of day.
     * @param weather The current weather of the simulation.
     */
    public void act(List<Animal> newAnimals, boolean isDay, String weather)
    {
        incrementAge();
        changeEnergyLevel(-1);

        if(alive) {
            if(!isDay && getCanSleep()) {
                sleep();
                return;
            }

            // Propagate if the animal is a female of breeding age.
            if(isFemale && age >= getBreedingAge()) {
                propagate(newAnimals);  
            }

            // Move towards a source of food if found.
            Location newLocation = findFood(weather);
            if(!alive) {
                return;
            }

            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = field.freeAdjacentLocation(location);
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
     * Return true if the animal is alive, false otherwise.
     * @return true if the animal is alive, false otherwise.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Increase the age of the animal. This could result in it's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Make an animal more hungry. This could result it's death.
     */
    protected void changeEnergyLevel(int change)
    {
        energyLevel += change;
        if(energyLevel <= 0) {
            setDead();
        }
    }

    /**
     * Increase the energy level when the animal is asleep.
     */
    protected void sleep()
    {
        energyLevel++;
    }

    /**
     * Return true if the animal sleeps at nighttime.
     * @return true if the animal sleeps at night, false otherwise.
     */
    abstract protected boolean getCanSleep();

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
     * Return true if the animal is female, false if it is male.
     * @return true if it is female, false if it is male.
     */
    protected boolean getIsFemale()
    {
        return isFemale;
    }

    /**
     * Selects a random gender for each instance of an animal.
     * @return true if the gender is female, otherwise, gender is male.
     */
    protected boolean chooseGender()
    {
        int randomGender = rand.nextInt(2);
        if (randomGender == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check the surrounding locations for animals. Then have a 
     * chance to give birth to random number offspring in free
     * locations surrounding it, if there are any.
     * @param newAnimals A list to store add the new offspring.
     */
    protected void propagate(List<Animal> newAnimals)
    {
        Field field = getField();
        List<Location> surrounding = field.surroundingLocations(getLocation(), 2);
        Iterator<Location> it = surrounding.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            giveBirth(newAnimals, animal);
        }
    }

    /**
     * Check whether the given animal is male and of the same species. 
     * If so, then give birth to a random number of offsprings
     * in the free locations surrounding it, if there are any.
     * @param newAnimals A list to add all the new offspring.
     * @param animal The animal that could be male and of the same type.
     */
    abstract protected void giveBirth(List<Animal> newAnimals, Object animal);

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Return the animal's breeding age.
     * @return animal's breeding age.
     */
    abstract protected int getBreedingAge();

    /**
     * Return the animal's breeding probability.
     * @return animal's breeding probability.
     */
    abstract protected double getBreedingProbability();

    /**
     * Return the animal's maximum litter size.
     * @return animal's maximum litter size.
     */
    abstract protected int getMaxLitterSize();

    /**
     * If the animal's energy level is lower than its hungry level,
     * check its adjacent locations for food to eat.
     * @param weather The weather of the simulation.
     * @return The location of the animal or plant to eat.
     */
    protected Location findFood(String weather)
    {
        if(energyLevel <= getHungry()) {
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object food = field.getObjectAt(where);
                Location l = checkFood(food, where, weather);
                if(l != null){
                    return l;
                }
            }
        } 
        return null;
    }

    /**
     * If the weather is not foggy and one of the animals or 
     * plant around it is of its food source, then eat it and
     * return the location of the animal or plant that was eaten.
     * @param object The animal or plant that might be eaten.
     * @param where The location of the animal or plant to be eaten.
     * @param weather The weather of the simulation.
     * @return The location of the animal or plant that was eaten.
     */
    abstract protected Location checkFood(Object object, Location where, String weather);

    /**
     * Return the food value of the animal.
     * @return The food value of the animal.
     */
    abstract protected int getFoodValue();

    /**
     * Return the energy level at which the animal will start 
     * to find food.
     * @return the energy level when the animal will start to find food.
     */
    abstract protected int getHungry();

    /**
     * Return the maximum age that the animal can live up to.
     * @return The animal's maximum age. 
     */
    abstract protected int getMaxAge();

    /**
     * Set the animal's status to infected, decrease its lifespan 
     * and current energy level. If its age exceeds its maximum 
     * age or it has no energy left, then it dies.
     */
    protected void infected()
    {
        isInfected = true;
        age += 10;
        changeEnergyLevel(-10);
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Return true if the animal is infected, otherwise return false.
     * @return true if the animal is diseased, otherwise return false.
     */
    protected boolean getIsInfected()
    {
        return isInfected;
    }
}