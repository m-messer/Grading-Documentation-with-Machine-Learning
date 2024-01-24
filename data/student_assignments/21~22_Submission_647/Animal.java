import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal implements Actor
{
    private static final double DISEASE_INFECT_PROBABILITY = 0.001;
    
    private static final double DISEASE_SPREAD_PROBABILITY = 0.001;
    
    private static final double DISEASE_WORSEN_TIME = 20;
    
    private static final double DISEASE_DEAD_PROBABILITY = 0.5;
    
    private static final Random rand = Randomizer.getRandom();
    
    private boolean alive;
    
    private Field field;
    
    private Location location;

    private int foodLevel;
    
    private int age;
    
    private int infectedTime;

    private boolean female;

    private boolean infected;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        infectedTime = 0;
        if (rand.nextDouble() <= 0.5){
            female = true;
        } else {
            female = false;
        }
        infected = false;
        age = 0;
        foodLevel = rand.nextInt(getMaxFoodValue());
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void actDay(List<Actor> newActors);

    abstract public void actNight(List<Actor> newActors);

    abstract protected Animal generateNewAnimal(boolean randomAge, Field field, Location loc);    
    
    abstract protected int getMaxAge();

    abstract protected boolean canBreed();
    
    abstract protected double getBreedingProbability();
    
    abstract protected int getMaxLitterSize();
    
    abstract protected int getMaxFoodValue();
    
    public int breed()
    {
        int births = 0;
        if(canBreed() && getRandom().nextDouble() <= getBreedingProbability()) {
            births = getRandom().nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }
    
    protected int getAge()
    {
        return age;
    }

    protected int getFoodLevel()
    {
        return foodLevel;
    }
    
    protected int getInfectedTime()
    {
        return infectedTime;
    }
    
    protected void setAge(int age)
    {
        this.age = age;
    }
    
    protected void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
    }
    
    protected void setInfectedTime(int infectedTime)
    {
        this.infectedTime = infectedTime;
    }
    
    protected void setInfected()
    {
        infected = true;
    }
    
    protected void incrementAge()
    {
        age += 1;
        if(age > getMaxAge()) {
            setDead();
        }
    }
    
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    public boolean isFemale()
    {
        return female;
    }

    public boolean isInfected()
    {
        return infected;
    }
    
    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    protected Random getRandom()
    {
        return rand;
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

    protected void giveBirth(List<Actor> newActors)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocationsForAnimals(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Object obj = field.getObjectAt(loc);
            if(obj instanceof Grass){
                Grass grass = (Grass)obj;
                grass.setDead();
            }
            Animal young = generateNewAnimal(false, field, loc);
            newActors.add(young);
        }
    }
    
    protected void setDisease()
    {
        if(rand.nextDouble() <= DISEASE_INFECT_PROBABILITY){
            this.setInfected();
        }
    }
    
    protected void spreadDisease()
    {
        Iterator <Location> it = field.adjacentLocations(location).iterator();
        while (it.hasNext()){
            Object obj = field.getObjectAt(it.next());
            if (obj instanceof Animal && !(obj instanceof Grass) && rand.nextDouble()<=DISEASE_SPREAD_PROBABILITY){
                Animal animal = (Animal)obj;
                animal.setInfected();
            }
        }
    }
        
    protected void worsenDisease(){
        infectedTime++;
        if(infectedTime >= DISEASE_WORSEN_TIME && rand.nextDouble() <= DISEASE_DEAD_PROBABILITY){
            this.setDead();
        }
    }
}
