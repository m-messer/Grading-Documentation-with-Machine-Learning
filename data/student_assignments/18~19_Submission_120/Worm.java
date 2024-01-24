import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a worm.
 * Worms age, move, eat plants, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Worm extends Organism
{
    // Characteristics shared by all worms (class variables).
    
    // The age at which a worm can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a worm can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a worm breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single plant. In effect, this is the
    // number of steps a worm can go before it has to eat again.
    private static final int PLANT_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The max food value of a single worm.
    private static final int MAX_FOOD_VALUE = 20; 
    
    // Individual characteristics (instance fields).
    // The worm's age.
    private int age;
    // The worm's gender.
    private int gender;
    // The worm's food level, which is increased by eating plants.
    private int foodLevel;

    /**
     * Create a worm. A worm can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the worm will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomGender If true, the worm will have random gender. 0 for females and 1 for males.
     */
    public Worm(boolean randomAge, Field field, Location location, boolean randomGender)
    {
        super(field, location);
        if (randomGender) {
            setGender(rand.nextInt(2)); //assign each worm a gender
        }
        else {
            gender = 0; //default gender female
        }
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = PLANT_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the worm does most of the time: it hunts for
     * plants. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newWorms A list to return newly born worms.
     */
    public void act(List<Organism> newWorms)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newWorms);            
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
     * Make this worm more hungry. This could result in the worm's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for plants adjacent to the current location.
     * Only the first live plant is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location plantLocation = null;
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if(organism instanceof Plant) {
                Plant plant = (Plant) organism;
                if(plant.isAlive()) {
                    plant.setDead();
                    foodLevel = foodLevel + PLANT_FOOD_VALUE;
                    if(foodLevel > MAX_FOOD_VALUE) {
                        foodLevel = MAX_FOOD_VALUE;
                    } 
                    plantLocation = where;
                }
            }  
        }
        return plantLocation; 
    }
    
    /**
    * Create a new organism. An organism may be created with age
    * zero (a new born) or with a random age.
    *
    * @param randomAge If true, the organism will have a random age.
    * @param field The field currently occupied.
    * @param location The location within the field.
    * @param randomGender If true, the worm will have random gender. 0 for females and 1 for males.
    */
    protected Organism createOrganism(boolean randomAge, Field field, Location location, boolean randomGender)
    {
        return new Worm(randomAge, field, location, randomGender);
    } 
    
    /**
     * Return the age to which a worm can live.
     * @return The worm's max age.
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
    
    /**
     * Return the age at which a worm can breed.
     * @return The worm's breeding age.
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
    
    /**
     * Return the likelihood of a worm breeding.
     * @return The worm's breeding probability.
     */
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    /**
     * Return the maximum number of births for a worm.
     * @return The worm's maximum number of births.
     */
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }
    
    /**
     * An organism can breed if it has reached the breeding age and
     * when a male and female individual are in a neighbouring cell.
     * @return true if the organism can breed, false otherwise.
     */
    protected boolean canWormBreed()
    {
        super.canBreed();
        boolean oppositeGender = false;
        Field cell = getField();
        List<Location> neighbouringCells = cell.adjacentLocations(getLocation());
        for (int i = 0; i < neighbouringCells.size(); i++) {
            // iterate through all locations in the list
            // to see if there are any organisms in the neighbouring
            // cells. If there are organisms then check to see if the
            // gender of the organisms are the opposite to the gender of 
            // the organism in the central cell.
            Object organism = cell.getObjectAt(neighbouringCells.get(i));
            if (organism != null) {
                Worm worm = new Worm(false, cell, neighbouringCells.get(i),true);
                if (organism.equals(worm)) {
                    int wormGender = worm.getGender();       // to be able to get it's gender
                    if ((wormGender == 0) && (wormGender == 1)) {
                        oppositeGender = true;
                    }
                    else if ((wormGender == 1) && (wormGender == 0)) {
                        oppositeGender = true;
                    }
                }
            }
        }
        // check if the organism is of breeding age and there is an organism 
        // in the neighbouring cell that is of the opposite gender.
        if ((super.canBreed() == true) && (oppositeGender == true)) {
            return true;
        }
        return false;
    }
}
