import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a tiger.
 * Tigers age, move, hunt, and die.
 *
 */
public class Tiger extends Animal
{
    // Characteristics shared by all tigers (class variables).

    // The age at which a tiger can start to breed. 
    private static final int BREEDING_AGE = 6;
    // The age to which a tiger can live.
    private static final int MAX_AGE = 155;
    // The likelihood of a tiger breeding.
    private static final double BREEDING_PROBABILITY = 0.55;

    private static final double MALE_CREATION_PROBABILITY = 0.5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // The food value of a single prey. In effect, this is the
    // number of steps a tiger can go before it has to eat again.
    private static final int FOOD_VALUE = 10;

    private static final double DISEASE_PROBABILITY = 0.01;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    private boolean isMale;
    private boolean hasDisease;

    // Individual characteristics (instance fields).
    // The tiger's age.
    private int age;
    // The tiger's food level, which is increased by eating preys.
    private int foodLevel;

    /**
     * Create a tiger. A tiger can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the tiger will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tiger(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOOD_VALUE);

        }
        else {
            age = 0;
            foodLevel = FOOD_VALUE;
        }
        isMale = rand.nextDouble() <= MALE_CREATION_PROBABILITY;
        hasDisease = rand.nextDouble() <= DISEASE_PROBABILITY;
    }

    /**
     * This is what the tiger does most of the time: it hunts for
     * preys. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newTigers A list to return newly born tigers.
     */
    public void act(List<Animal> newTigers)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newTigers);            
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
        else if(isAlive() & hasDisease)
        {
           spreadDisease();
        }
    }
    
    /**
     * If the tiger gets sick. It can spred the disease.
     */
    public void spreadDisease()
    {
        Field field = getField();
        List<Location> animalsNearby = field.getAnimalLocation(getLocation());
        for(Location where : animalsNearby) {
            Object animal = field.getObjectAt(where);
            if(animal instanceof Tiger){
                Tiger tiger = (Tiger) animal;
                if(tiger.isAlive())
                    tiger.setDisease();
            }
        }
    }

    /**
     * Tigers with a disease only have 10 more steps of life.
     */
    public void setDisease()
    {
        hasDisease = true;
        if(age <= MAX_AGE - 10)
            age = MAX_AGE -10;
        else setDead();
    }
    
    /**
     * Tigers strive during the night, being capable of hunting.
     */
    public void nightAct(List <Animal> newTigers)
    {
       incrementAge();
        incrementHunger();
        if(isAlive() && !isMale) {
            giveBirth(newTigers);            
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
     * Tigers behave normally when it rains.
     */
    public void rainAct(List <Animal> newTigers)
    {
        act(newTigers);
    }
    
    /**
     * Tigers can't move when its foggy.
     */
    public void fogAct(List <Animal> newTigers)
    {
        if(isAlive() && !isMale && !hasDisease) {
            giveBirth(newTigers);                 
        }
        else if(isAlive() & hasDisease)
        {
           spreadDisease();
        }
    }
    
    /**
     * Tigers can't breed when its stormy.
     */
    public void stormAct(List <Animal> newTigers)
    {
        
        if(isAlive() && !hasDisease) {
            // Try to move into a free location.
            Location newLocation = getField().freeAdjacentLocation(getLocation());
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
        else if(isAlive() && hasDisease)
            spreadDisease();
    }
    
    /**
     * Increase the age. This could result in the tiger's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this tiger more hungry. This could result in the tiger's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for preys adjacent to the current location.
     * Only the first live prey is eaten.
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
            if(animal instanceof Giraffe) {
                Giraffe giraffe = (Giraffe) animal;
                if(giraffe.isAlive()) { 
                    giraffe.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Gazelle) {
                Gazelle gazelle = (Gazelle) animal;
                if(gazelle.isAlive()) { 
                    gazelle.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            if(animal instanceof Zebra) {
                Zebra zebra = (Zebra) animal;
                if(zebra.isAlive()) { 
                    zebra.setDead();
                    foodLevel = FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Plant) {
                Plant plant = (Plant) animal;
                if(plant.isAlive()) { 
                    plant.setDead();
                    foodLevel = 0;
                    return where;

                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this tiger is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newTigers A list to return newly born tigers.
     */
    private void giveBirth(List<Animal> newTigers)
    {
        // New tigers are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Tiger young = new Tiger(false, field, loc);
            newTigers.add(young);
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
     * A tiger can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        if(age >= BREEDING_AGE)
        {
            Field field = getField();
            List<Location> matingPartners = field.getAnimalLocation(getLocation());
            for(Location where : matingPartners) {
                Object animal = field.getObjectAt(where);
                if(animal instanceof Tiger){
                    Tiger tiger = (Tiger) animal;
                    if(tiger.isMale() && tiger.getAge() >= BREEDING_AGE)
                        return true;
                }
                else return false;                                          
            }
        }
        return false;
    }

    private boolean isMale()
    {
        return isMale;
    } 

    private int getAge()
    {
        return age;
    }
}
