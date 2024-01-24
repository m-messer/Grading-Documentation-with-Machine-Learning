import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals, contains behaviour values and methods for various actions.
 *
 * @version 2021.03.01 (3)
 */
public abstract class Animal extends Organism
{
    private String sex; // This animal's sex. only stores "male" or "female. Opted for String for readability

    // The animal's food level, increased by eating its prey or plant food.
    protected int foodLevel;

    // Stores the number of steps left until this animal may mate again.
    private int matingCooldown;

    /**
     * Create a new animal at location in field.
     * 
     * @param randomAge Specify if the animal's age should be random (true) or start from 0 (false).
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param simulator Used to pass a reference to the main simulator class
     */
    public Animal(boolean randomAge, Field field, Location location, Simulator simulator)
    {
        super(randomAge, field, location, simulator);

        matingCooldown = 0;

        if ( rand.nextInt(10)+1 < 6 )sex = "male";
        else sex = "female";
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newOrganisms A list to receive newly born animals.
     */
    public void act(List<Organism> newOrganisms){
        super.act(newOrganisms);

        updateStats();

        if(isAlive()) {
            if(isDiseased()){
                spreadDisease();
            }

            tryToMate(newOrganisms);

            moveOrEat();
        }
    }

    private void updateStats(){

        decreaseMatingCooldown();
        incrementHunger();
        decreaseDiseaseLevel();
    }

    /**
     * Checks if this animal can mate ( adequate time of day, age, neighboring mate, etc ) and makes it mate if possible
     */
    private void tryToMate(List<Organism> newOrganisms){
        if (
        (matesDuringDay() && simulator.getTimeOfDay()) // simulator.getTimeOfDay() returns true if it is currently day, false if it is night
        ||
        (!matesDuringDay() && !simulator.getTimeOfDay())
        )
        {
            if (canMate())
            {
                Object possibleMate = findMate();
                if (possibleMate!=null)
                {
                    updateMatingCooldown();
                    updateMateCooldown(possibleMate);
                    giveBirth(newOrganisms); 
                }   
            }
        }
    }

    /**
     * Checks if this animal moves and eats during the current time of day, makes it do so if it is adequate.
     */
    private void moveOrEat(){
        if (
        (movesOrEatsDuringDay() && simulator.getTimeOfDay()) // simulator.getTimeOfDay() returns true if it is currently day, false if it is night
        ||
        (!movesOrEatsDuringDay() && !simulator.getTimeOfDay())
        ){

            Location newLocation = null;

            newLocation = findFood(); // next location is set as the location of food if food is found
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
     * Gets the maximum age this animal will live up to if healthy.
     * @return int Maximum age for this animal's species.
     */
    abstract protected int getDefaultMaxAge();   

    /**
     * Searches for any animals in the adjacent cells, and depending on a random value and a given probability, spreads disease to each of them (separately).
     */
    protected void spreadDisease(){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Animal possibleDiseased = (Animal) field.getObjectAt(where);
            if(possibleDiseased != null){
                var diseaseRand = rand.nextDouble();
                if (diseaseRand < getDiseaseSpreadingProbability())
                {
                    possibleDiseased.getDisease();
                }
            }
        }

    }

    /**
     * Cures this animal of disease.
     */
    public void cureDisease(){
        diseaseLevel = 0;
        initializeDefaultStats();
    }

    /**
     * Sets this animal's behaviour defining stats to their default values.
     */
    public abstract void initializeDefaultStats();
    
    /**
     * Returns this animal's probability to spread disease.
     * @return double Number representing the probability to spread disease.
     */
    abstract protected double getDiseaseSpreadingProbability();
    
    /**
     * Specifies whether this animal mates during the day.
     * @return true if this animal mates during the day, false otherwise
     */
    abstract protected boolean matesDuringDay();
    
    /**
     * Specifies whether this animal moves or eats during the day.
     * @return true if this animal moves or eats during the day, false otherwise
     */
    abstract protected boolean movesOrEatsDuringDay();
    
    /**
     * Specifies whether this animal can mate at the moment.
     * @return true if this animal can mate, false otherwise
     */
    protected boolean canMate(){
        return (matingCooldown==0 && age >= getBreedingAge());
    }

    /**
     * Returns a number representing the number of steps this animal's species must wait between matings.
     * @return int No. of steps needed to pass to mate again.
     */
    abstract public int getMatingCooldown();

    /**
     * Sets the mating cooldown to the animal's species' specific value. Makes the animal wait before being able to mate again.
     */
    private void updateMatingCooldown(){
        matingCooldown = getMatingCooldown();
    }

    /**
     * Counts one step until the animal may mate again.
     */
    private void decreaseMatingCooldown(){
        if (matingCooldown>0)matingCooldown--;
    }

    /**
     * Updates the provided mate's (passed through the parameter) mating cooldown.
     * @param mate This animal's mate
     */
    private void updateMateCooldown(Object mate){
        Animal mateAnimalHolder = (Animal) mate;
        mateAnimalHolder.updateMatingCooldown();
    }

    /**
     * Looks for a suitable mate for this animal in the adjacent cells.
     * @return Object A suitable mate that was found, or null if there is none.
     */
    private Object findMate(){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object possibleMate = field.getObjectAt(where);
            if(checkMate(possibleMate)) 
                return possibleMate;

        }
        return null;
    }

    /**
     * Checks if the passed object represents a suitable mate for this animal. 
     * @return true if the object represents a suitable mate.
     */
    abstract public boolean checkMate(Object object);

    /**
     * Returns a string representing the gender of this animal.
     * @return "male" or "female"
     */
    public String getGender(){
        return sex;
    }

    /**
     * Return the value of the MAX_AGE constant;
     * @return Maximum age
     */
    public abstract int getMaxAge();

    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newOrganisms A list to return newly born animals.
     */
    protected void giveBirth(List<Organism> newOrganisms)
    {
        // New animals of same species are born into adjacent locations.
        
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation()); // Get a list of adjacent free locations.
        int births = breed(); // Number of offspring to create. Number can be 0 ( breeding is unsuccesful ) and no offspring will apear
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);

            Animal young = null;

            young = getNewYoung( false, field, loc, simulator); // This method is implemented individually in each subclass
            // so that it returns a new object of the same type as the caller, so, a new animal of the same species.

            newOrganisms.add(young);
        }
    }

    /**
     * Creates and Returns a new object of the type same type as this animal
     * to simulate the birth of an offspring.
     * @return New object of the same type.
     */
    public abstract Animal getNewYoung( boolean randomAge, Field field, Location loc, Simulator simulator);

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Accessor method for getting the current breeding probability for this animal.
     * @return Breeding probability for species represented by object
     */
    public abstract double getBreedingProbability();

    /**
     * Accessor method for getting the current maximum litter size for this animal.
     * @return Maximum litter size for species represented by object
     */
    public abstract int getMaxLitterSize();

    /**
     * Accessor method for getting breeding age constant specific to the type
     * of species
     * @return Breeding age for species represented by object
     */
    public abstract int getBreedingAge();

    /**
     * Searches neighbouring cells for the correct food type, and makes the animal eat
     * @return Location The location of the food found, or null if there is no food around.
     */
    protected Location findFood()
    {
        Field field;
        if (isHerbivore())field = simulator.getPlantField();
        else field = getField();

        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object possibleFood = field.getObjectAt(where);
            if(checkFoodType(possibleFood)) {    
                Organism food = (Organism) possibleFood;
                if(food.isAlive()) { 
                    food.setDead();
                    foodLevel = getFoodValue();
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Specifies whether this animal is an herbivore
     * @return true If this animal is an herbivore.
     */
    protected abstract boolean isHerbivore();

    /**
     * Checks if the passed object is an instance of the class representing the prey
     * of our animal object
     * @return True if the object is an instance of the class that represents the prey/food
     * @param object animal object you wish to check
     */
    protected abstract boolean checkFoodType(Object object);

    /**
     * Accessor method for getting the Food Value constant specific to the 
     * type of species
     * @return Food value constant for species represented by object prey
     */
    protected abstract int getFoodValue();

}
