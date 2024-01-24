import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal
{

    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // The animal's gender.
    private boolean isFemale;
    // Whether the animal is nocturnal or not.
    private boolean isNocturnal;
    // Whether the animal is infected or not.
    private boolean hasDisease;

    private final Random rand = Randomizer.getRandom();
    //The age at which an animal can start breeding.
    private int BREEDING_AGE;
    // The age to which a animal can live.
    private int MAX_AGE;
    // The likelihood of a animal breeding.
    private double BREEDING_PROBABILITY;
    // The maximum number of births.
    private int MAX_LITTER_SIZE;
    // The maximum number of steps an animal can take before dying of hunger.
    private int MAX_FOOD_LEVEL;
    // Disease probability, the smaller number the higher probability (1/n)
    private static final int DISEASE_PROBABILITY = 35;

    // Individual characteristics (instance fields).
    private int foodLevel;

    private int age;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param random if animal should have random features
     * @param isNocturnal if animal is nocturnal
     * @param breeding_age The age at which an animal can start breeding.
     * @param max_age The age to which a animal can live.
     * @param breeding_probability The likelihood of a animal breeding.
     * @param max_litter_size The maximum number of births.
     * @param max_food_level The maximum number of steps an animal can take before dying of hunger.
     * 
     */
    public Animal(  Field field, 
    Location location,
    boolean random,
    boolean isNocturnal,
    int breeding_age,
    int max_age,
    double breeding_probability,
    int max_litter_size,
    int max_food_level
    )
    {
        alive = true; 
        isFemale = rand.nextBoolean();
        hasDisease = rand.nextBoolean();
        this.isNocturnal = isNocturnal;
        this.field = field;
        setLocation(location);

        BREEDING_AGE = breeding_age;
        MAX_AGE = max_age;
        BREEDING_PROBABILITY = breeding_probability;
        MAX_LITTER_SIZE = max_litter_size;
        MAX_FOOD_LEVEL = max_food_level;

        age = 0;
        if(random) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MAX_FOOD_LEVEL);

            if(rand.nextInt(DISEASE_PROBABILITY) == 0)
                getDisease();
            else
                hasDisease = false;
        }
        else{
            age = 0;
            foodLevel = MAX_FOOD_LEVEL;
            hasDisease = false;
        }
    }

    /**
     * Increase the age.
     * This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Decrements the foodlevel of the animal.
     * If the animal has the disease, foodlevel
     * decrements by 2.
     * This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(hasDisease) {
            foodLevel--;
        }
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for animals adjacent to the current location.
     * Only the first live animal is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            where = eat(where, field);

            if(where != null)
                return where;
        }
        return null;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDeers A list to return newly born animals.
     */
    private void giveBirth(List<Animal> newAnimals)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Animal young = getYoung(field, loc);

            if (hasDisease) {
                young.getDisease();
            }

            newAnimals.add(young);
        }
    }

    /**
     * A animal can breed if it has reached the breeding age.
     * @return true if the animal can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE && isFemale();
    }

    /**
     * Checks whether an animal has a partner of the opposite gender in an nearby location.
     * @return the partner if the above is the case.
     */
    private Animal hasPartner() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());

        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where).getKey();
            if(animal != null && this.getClass() == animal.getClass() && !((Animal) animal).isFemale()) {
                return (Animal) animal;
            }
        }

        return null;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        Animal partner = hasPartner();
        if(canBreed() && partner != null && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;

            if(hasDisease && !partner.hasDisease()){
                partner.getDisease();
            }
            if(partner.hasDisease() && !hasDisease()) {
                getDisease();
            }
        }
        return births;
    }

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clearAnimal(location);
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
            field.clearAnimal(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * This is what the animal does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newAnimals A list to return newly born animals.
     */
    public void act(List<Animal> newAnimals)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newAnimals);            
            // Try to move into a free location.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }

            // during every step there is some probability of getting infected, e.g. through a wound
            if(rand.nextInt(DISEASE_PROBABILITY) == 0 && !hasDisease)
                getDisease();
            else
                hasDisease = false;
        }
    }

    /**
     * Imitates sleeping. Animal gets older and more 
     * hungry but doesn't move or breed.
     */
    public void sleep() {
        incrementAge();
        incrementHunger();
    }

    /**
     * Increase the animal's foodlevel by the amount specified.
     * If the food level is above the max, keep it at the maximum.
     * @param foodValue value of eaten food
     */
    protected void increaseFoodLevel(int foodValue)
    {   
        foodLevel += foodValue;

        if (foodLevel > MAX_FOOD_LEVEL) {
            foodLevel = MAX_FOOD_LEVEL;
        }
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
     * Return if the animal is female.
     * @return if the animal is female.
     */
    public boolean isFemale() {
        return isFemale;
    }

    /**
     * Return if the animal is nocturnal.
     * @return if the animal is nocturnal.
     */
    protected boolean isNocturnal() {
        return isNocturnal;
    }

    /**
     * Give the animal a disease,
     * and decrease lifespan by 20%.
     */

    private void getDisease(){
        hasDisease = true;

        MAX_AGE *= 0.8;
    }

    /**
     * Return if the animal is infected.
     * @return if the animal is infected.
     */
    public boolean hasDisease(){
        return hasDisease;
    }

    /**
     * Return the animal's capacity for food.
     * @return The animal's capacity for food.
     */
    public int getFoodCapacity() {
        return MAX_FOOD_LEVEL - foodLevel;
    }

    /**
     * Returns a new young
     * @param field
     * @param loc
     * @return new instance of young animal
     */
    abstract protected Animal getYoung(Field field, Location loc);

    /**
     * If there is a correct food in the specified location, 
     * the food gets eaten by the capacity for food the animal has available 
     * and its location is returned. In other case null is returned
     * @param where where the potential food is
     * @param field the field on which everything happens
     * @return location where food has been eaten
     */
    abstract protected Location eat(Location where, Field field);

}

