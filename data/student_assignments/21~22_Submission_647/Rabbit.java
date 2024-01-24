import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Rabbit extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 100;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;

    private static final int GRASS_FOOD_VALUE = 15;

    private static final int MAX_FOOD_VALUE = 15;
    // A shared random number generator to control breeding.

    // Individual characteristics (instance fields).

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rabbit(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(getRandom().nextInt(MAX_AGE));
        }
    }

    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void actDay(List<Actor> newActors)
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
            if(!getField().isRainy()){
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

    public void actNight(List<Actor> newActors)
    {
        incrementAge();
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

    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Grass) {
                Grass grass = (Grass) animal;
                if(grass.isAlive()) { 
                    grass.setDead();
                    setFoodLevel(Integer.min(getFoodLevel() + GRASS_FOOD_VALUE, MAX_FOOD_VALUE));
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    public boolean canBreed()
    {
        return isFemale() && getAge() >= BREEDING_AGE && diffSex();
    }

    public Rabbit generateNewAnimal(boolean randomAge, Field field, Location loc)
    {
        return new Rabbit(false, field, loc);
    }

    private boolean diffSex(){
        List<Location> adjacent= getField().adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Object object = getField().getObjectAt(it.next());
            if(object instanceof Rabbit ){
                Rabbit rabbit = (Rabbit) object;   
                if(!rabbit.isFemale() && rabbit.getAge() >= BREEDING_AGE){
                    return true;
                }
            } 
        }
        return false;
    }
}
