import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Fox extends Animal
{
    // Characteristics shared by all foxes (class variables).

    // The age at which a fox can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a fox can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a fox breeding.
    private static final double BREEDING_PROBABILITY = 1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 6;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 20;
    // A shared random number generator to control breeding.
    private static final int MAX_FOOD_VALUE = 24;

    // Individual characteristics (instance fields).
    // The fox's food level, which is increased by eating rabbits.

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fox(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
        }
    }

    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void actDay(List<Actor> newActors)
    {
        incrementAge();
    }

    public void actNight(List<Actor> newActors)
    {
        incrementAge();
        incrementHunger();
        
        if(isAlive()) {
            if(!isInfected()){
                setDisease();
            }
            if(isInfected()){
                spreadDisease();
                worsenDisease();
            }
        }
        
        if(isAlive()){
                if(!getField().isFoggy()){
                giveBirth(newActors);
                Location newLocation = findFood();            
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocationForAnimls(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    setDead();
                }
            }
        }
    }
    
    public int getMaxFoodValue()
    {
        return MAX_FOOD_VALUE;
    }
    
    public int getMaxAge(){
        return MAX_AGE;
    }

    public double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }
    
    public int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
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
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isInfected()){
                    this.setInfected();
                }
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    setFoodLevel(Integer.min(getFoodLevel() + RABBIT_FOOD_VALUE, MAX_FOOD_VALUE));
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * A fox can breed if it has reached the breeding age.
     */
    public boolean canBreed()
    {
        return isFemale() && getAge() >= BREEDING_AGE && diffSex();
    }

    public Fox generateNewAnimal(boolean randomAge, Field field, Location loc)
    {
        return new Fox(false, field, loc);
    }

    private boolean diffSex(){
        List<Location> adjacent= getField().adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Object object = getField().getObjectAt(it.next());
            if(object instanceof Fox ){
                Fox fox = (Fox) object;   
                if(!fox.isFemale() && fox.getAge() >= BREEDING_AGE){
                    return true;
                }
            } 
        }
        return false;
    }
}