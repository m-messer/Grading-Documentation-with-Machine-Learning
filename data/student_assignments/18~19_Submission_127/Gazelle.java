import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a gazelle.
 * Gazelles age, move, breed, eat grass and die.
 *
 * @version 2019.02.22 
 */
public class Gazelle extends Animal
{
    // Characteristics shared by all gazelles (class variables).

    // The age at which a gazelle can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a gazelle can live.
    private static final int MAX_AGE = 90;
    // The likelihood of a gazelle breeding.
    private static final double BREEDING_PROBABILITY = 0.116;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The food value of a single grass. In effect, this is the
    // number of steps a gazelle can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 27;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The gazelle's age.
    private int age;
    // The gazelle's food level, which is increased by eating grass.
    public int foodLevel;
    // The gazelle's disease level, which is increased by infection.
    private int diseaseLevel;
    

    /**
     * Create a new gazelle. A gazelle may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the gazelle will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Gazelle(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        diseaseLevel = 2;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = GRASS_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the gazelle does most of the time during the day- it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newGazelles A list to return newly born gazelles.
     */
    public void act1(List<Animal> newGazelles)
    {
        incrementAge();
        incrementHunger();
        haveDisease();
        if(isAlive()) {
            giveBirth(newGazelles);            
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
     * This is what the gazelle does at night - it sleeps
     * Hence, its age and hunger increases - which could result in death.
     * @param newGazelles A list to return newly born Gazelles.
     */
    public void act2(List<Animal> newGazelles)
    {
        incrementAge();
        incrementHunger();
        haveDisease();
    }

    /**
     * Increase the age.
     * This could result in the gazelle's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this gazelle more hungry. This could result in the gazelle's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * This method checks if the gazelle has a disease.
     */
    private void haveDisease()
    {
      if(isDiseased()){
        incrementDisease();
      }
    }
    
    /**
     * This method is to increment the disease of the gazelle.
     */
    private void incrementDisease()
    {
        diseaseLevel--;
        if(diseaseLevel <= 0) {
            setDead();
            infectOthers();
        }
    }
    
    /**
     * Look for animals adjacent to the current location.
     * Only the first live animal is infected.
     */
    private void infectOthers()
    {
        Field field = getField();
        System.out.println(field);
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Animal) {
                Animal animals = (Animal) animal;
                if(animals.isAlive()) { 
                    animals.getDisease();
                    //return where;
                }
            }
        }
        //return null;
    }
    
    /**
     * Look for grass adjacent to the current location.
     * Only the first grown grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                int grassAge = grass.grassAge;
                if(grass.isAlive()) { 
                    if(grassAge >= 3){
                        foodLevel += 2;
                        grassAge -= 2;
                }
                else {
                    foodLevel += grassAge;
                    grassAge = 0;
                }
              }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this gazelle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newGazelles A list to return newly born gazelles.
     */
    private void giveBirth(List<Animal> newGazelles)
    {
        // New gazelles are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Gazelle young = new Gazelle(false, field, loc);
            newGazelles.add(young);
        }
    }
     
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A gazelle can breed if it has reached the breeding age.
     * @return true if the gazelle can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}