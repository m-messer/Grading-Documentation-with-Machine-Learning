import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Cheetah.
 * Cheetahs age, move, hunt gazelles, and die.
 *
 * @version 2019.02.22 
 */
public class Cheetah extends Animal
{
    // Characteristics shared by all Cheetahs (class variables).
    
    // The age at which a cheetah can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a cheetah can live.
    private static final int MAX_AGE = 37;
    // The likelihood of a cheetah breeding.
    private static final double BREEDING_PROBABILITY = 0.04503909289638412;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single gazelle. In effect, this is the
    // number of steps a cheetah can go before it has to eat again.
    private static final int GAZELLE_FOOD_VALUE = 18;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The cheetah's age.
    private int age;
    // The cheetah's food level, which is increased by eating gazelles.
    private int foodLevel;
    // The cheetah's disease level, which is increased by infection.
    private int diseaseLevel;

    /**
     * Create a cheetah. A cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the cheetah will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cheetah(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        diseaseLevel = 2;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GAZELLE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = GAZELLE_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the cheetah does most of the time during the day: it hunts for
     * gazelles. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newCheetahs A list to return newly born Cheetahs.
     */
    public void act1(List<Animal> newCheetahs)
    {
        incrementAge();
        incrementHunger();
        haveDisease();
        if(isAlive()) {
            giveBirth(newCheetahs);            
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
     * This is what the cheetah does at night - it sleeps.
     * Hence, its age and hunger increases - which could result in death.
     * @param newCheetahs A list to return newly born Cheetahs.
     */
    public void act2(List<Animal> newCheetahs)
    {
        incrementAge();
        incrementHunger();
        haveDisease();
    }

    /**
     * Increase the age. This could result in the cheetah's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this cheetah more hungry. This could result in the cheetah's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * This method checks if the cheetah has a disease.
     */
    private void haveDisease()
    {
      if(isDiseased()){
        incrementDisease();
      }
    }
    
    /**
     * This method is to increment the disease of the cheetah.
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
     * This method kills the animal if they have increased the food level over the limit.
     */
    private void overEat()
    {
        
        if(foodLevel >= 50) {
            setDead();
        }
    }
    
    /**
     * Look for gazelles adjacent to the current location.
     * Only the first live gazelle is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Gazelle) {
                Gazelle gazelle = (Gazelle) animal;
                int food = gazelle.foodLevel;
                if(gazelle.isAlive()) { 
                    gazelle.setDead();
                    foodLevel += food;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this cheetah is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newCheetahs A list to return newly born cheetahs.
     */
    private void giveBirth(List<Animal> newCheetahs)
    {
        // New cheetahs are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Cheetah young = new Cheetah(false, field, loc);
            newCheetahs.add(young);
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
     * A cheetah can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}