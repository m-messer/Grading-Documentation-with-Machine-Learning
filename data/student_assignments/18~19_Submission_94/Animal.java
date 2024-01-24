import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2019.02.21
 */
public abstract class Animal extends Species 
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The probability of the sick animals die.
    private static final double mortalityRate = 0.5;
    // The gender of an animal. (Male, Female)
    private String gender;
    // Wheather the animal is sick or not.
    private boolean isSick;
    // The age of animal.
    private int age;
    // The animal's food level, which is increased by eating the others.
    private int foodLevel;
    // The animal's water level, which is increased by drinking the water.
    private int waterLevel;
    /**
     * Create a new animal at location in field.
     * 
     * @param randomAge If true, the animal will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param gender The gender of an animal. (Male, Female)
     * @param isSick If true, the animal is sick.
     */
    public Animal(boolean randomAge, Field field, Location location, String gender,
                  boolean isSick) 
    {
        super(field, location);
        this.gender = gender;
        this.isSick = isSick;
        if (randomAge) {
            age = rand.nextInt(10);//make sure all animals' age should smaller than the number we set(10).
        } else {
            age = 0;
        }
        foodLevel = 15;
        waterLevel = 15;
    }
    
    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newAnimal A list to return newly born animals.
     */
    abstract public void giveBirth(List<Animal> newAnimal);
   
    /**
     * Check wheather the parameter is food or not.
     * 
     * @return wheather the parameter is food or not.
     * @param object the object the animal is going to eat.
     */
    abstract public boolean isFood(Object object);

    /**
     * Check wheather the adjacent animal can be a partner(mate).
     * 
     * @param animal The adjacent object.
     * @return wheather or not the adjacent eagle can be a partner.
     */
    abstract public boolean isMate(Object animal);
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> animalList, boolean isDay, String weather);
    
    /**
     * Check wheather the parameter is water or not.
     * 
     * @return wheather the parameter is water or not.
     * @param object the object the animal is going to drink.
     */
    protected boolean isDrink(Object object) 
    {
        if (object instanceof Water) {
            return true;
        }

        return false;
    }
    
    /**
     * Increase the age of the animal.
     * If the animal aged over the maximum, it dies.
     * 
     * @param maxAge Within the maximum age, it increased.
     */
    protected void incrementAge(int maxAge) 
    {
        age++;
        if (age > maxAge) {
          setDead();
        }
    }
    
    /**
     * Increase the hunger of the animal.
     * If the hunger level gets less than 0, it dies.
     */
     protected void incrementHunger() 
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Increase the thirsty level of the animal.
     * If the thirsty level gets less than 0, it dies.
     */
    protected void incrementThirsty() 
    {
        waterLevel--;
        if (waterLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Access method of variable: gender
     * 
     * @return The string of gender. (Male, Female)
     */
    protected String getGender()
    {
        return gender;
    }
    
    /**
     * An animal can breed if it has reached the breeding age.
     * 
     * @param breedingAge The age that the animal is able to breed.
     * @return true if the rabbit can breed, false otherwise.
     */
    protected boolean canBreed(int breedingAge)
    {
        return age >= breedingAge;
    }
    
    /**
     * Make the animal sick.
     * 
     * @param sick Wheather the animal is sick or not.
     */
    public void setSick(boolean sick) 
    {
        isSick = sick;
    }
    
    /**
     * Indicate wheather the animal is sick or not.
     */
    public boolean isSick() 
    {
        return isSick;
    }
    
    /**
     * If the animal is sick, it might dies up to the mortality rate.
     */
    protected void diseaseCauseDeath()
    {
        if(isSick){
            if(rand.nextDouble() <= mortalityRate){
                setDead();
            }
        }
    }
    
    /**
     * Look for foods adjacent to the current location.
     * Only the first live foods is eaten.
     *
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood() 
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if (isFood(object)) {
                Species species = (Species) object;
                if (species.isAlive()) {
                    foodLevel=15;
                    species.setDead();
                    return where;
                }
            }
        }while (it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if (isDrink(object)) {
                Water water = (Water) object;
                if (water.isAlive()) {
                    waterLevel=15;
                    water.setDead();
                    return where;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Look for mate adjacent to the current location.
     *
     * @return true mate was found, or false if it wasn't.
     */
    protected boolean findMate() 
    {
        boolean haveMate = false;
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (isMate(animal)) {
                haveMate = true;
                break;
            }
        }
        return haveMate;
    }

    /**
     * The disease spread when the animals meet in the adjacent location.
     */
    protected void SpreadDisease() 
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        while (it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if (object instanceof  Animal)
            {
                Animal animal = (Animal) object;
                if(isSick()){
                    animal.setSick(Randomizer.getRandomIsSick());
                } else if (animal.isSick()){
                    setSick(Randomizer.getRandomIsSick());
                }

            }
        }
    }
}
