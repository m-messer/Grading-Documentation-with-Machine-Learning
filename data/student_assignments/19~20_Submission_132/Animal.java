import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A class representing shared characteristics of Animals.
 * It is a sub class of super class Actor.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Actor
{
    private static final Random rand = Randomizer.getRandom();
    //The Animal's food level 
    protected int foodLevel;
    //Whether is Animal is male or not
    private boolean male;
    //Whether Animal is fertile or not
    private boolean isFertile;
    //The time before the Animal can breed again
    private int breedingCooldownCounter;
    /**
     * Create a new Animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        foodLevel = rand.nextInt(foodChain.getStartValue(this));
        male = rand.nextBoolean();
        isFertile = true;
    }
    
    /**
     * Get breeding age of the Animal from it's specofoc class
     */
    abstract public int getBreedingAge();

    /**
     * Get max litter size of the Animal from it's specofoc class
     */
    abstract public int getMaxLitterSize();

    /**
     * Get breeding probability for the Animal from it's specofoc class
     */
    abstract public double getBreedingProbability();

    /**
     * Get breeding cooldown period for the Animal from it's specofoc class
     */
    abstract public int getBreedingCooldown();

    /**
     * Create a new offspring of the Animal
     */
    abstract protected Actor generateOffspring(Field field, Location loc);
    
    /**
     * @return true If Animal can breed if certain conditions
     * i.e breeding age, fertility and availability of
     * mate are satisfied
     */
    public boolean canBreed()
    { 
        if (age >= this.getBreedingAge() && isFertile && checkForMate(getLocation())) 
            return true;
        else
            return false;
    }

    /**
     * If Animal is eaten they die
     */
    protected void eaten()
    {
        setDead();
    }

    /** 
     * @return true If Animal next to another is of same species
     * and opposite gender for them to mate
     */
    protected boolean checkForMate(Location location)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for(Location position : adjacent)
        {
            Object adjacentAnimal = field.getObjectAt(position);
            if(adjacentAnimal != null && adjacentAnimal instanceof Animal)
            {
                Animal animal = (Animal) adjacentAnimal;
                //Check both animals are of same species and opposite genders
                if (isCompatibleMate(this,animal))
                {
                    return true;
                }
            }
        }  
        return false;
    }  

    /**
     * Hunger increase when food level in Animal
     * decreses. Animal dies if it is below 0.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Animal gives birth 
     */
    protected void giveBirth()
    {
        // New Animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        //Get number of Animals that will be born
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            Actor young = this.generateOffspring(field,loc);
            newActors.add(young);
        }
        isFertile = false;
        breedingCooldownCounter = 0;
    }

    /**
     * Make the Animal roam and perform tasks
     * i.e giving birth, finding food when step 
     * is incremented.
     */
    protected void roam()
    {
        if(isAlive()) 
        {
            //If Animal can breed is true then it gives birth
            if(canBreed())
            {
                giveBirth();
            }

            if(foodLevel < 200){
            // Move towards a source of food if found.
            
            Location newLocation = findFood();
            if(newLocation == null)
            { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) 
            {
                setLocation(newLocation);
            }
            else 
            {
                // Overcrowding.
                setDead();
            }
            }
        }
    }

    /**
     * Increment time Animal is not fertile
     * once it reaches cooldown value for Animal
     * it again becomes fertile
     */
    protected void incrementBreedingCooldown()
    {
        if(isFertile == false)
        {    
            breedingCooldownCounter ++;
            if(breedingCooldownCounter >= this.getBreedingCooldown())
            {
                isFertile = true;
            }
        }
    }

    /**
     * Check if there is food next to Animal
     * and if Animal can eat it or not
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) 
        {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            if(object instanceof Actor)
            {
                Actor actor = (Actor) object;
                if(foodChain.canEat(this,actor))
                {
                    actor.eaten();
                    foodLevel += actor.getFoodValue();
                    //if prey is an Animal the predator takes its place
                    if(object instanceof Animal)
                    {
                        return where;
                    }
                    else if(object instanceof Plant)
                    {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * If Animal can breed and it's within breeding probability 
     * randomly choose litter size from max litter size of Animal
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= this.getBreedingProbability()) 
        {
            births = rand.nextInt(this.getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * @return true If Animal is male
     */
    public boolean isMale()
    {
        return male;
    }

    /**
     * @param animal1 Animal we're at
     * @param animal2 Animal next to it
     * @return true If both Animals are of opposite gender
     */
    public boolean isOppositeGender(Animal animal1,Animal animal2)
    {
        if(animal1.isMale() != animal2.isMale())
        {
            return true;
        }
        return false;
    }

    /**
     * @param animal1 Animal we're at
     * @param animal2 Animal next to it
     * @return true If both Animals are of same species
     */
    public boolean isSameSpecies(Animal animal1, Animal animal2)
    {
        if(animal1.getClass().equals(animal2.getClass()))
        {
            return true;
        }
        return false;
    }

    /**
     * @param animal1 Animal we're at
     * @param animal2 Animal next to it
     * @return true If both Animals are of same species and opposite genders
     */
    public boolean isCompatibleMate(Animal animal1, Animal animal2){
        if(isOppositeGender(animal1,animal2) && isSameSpecies(animal1,animal2))
        {
            return true;
        }
        return false;
    }

    /**
     * Bodily function like increasing age, hunger etc.
     * taking place in Animal when it moves a step
     */
    public void bodilyFunctions()
    {
        if(alive)
        {
            Field field = getField();
            //assert field != null;
            //spread disease if Animal has disease
            if(diseased && disease != null)
            {
                disease.diseaseFunctions(getLocation(), getField());
            }
            incrementAge();
            incrementHunger();
            incrementBreedingCooldown();
        }
    }

    /**
     * @return type of species  
     */
    public Animal getSpecies()
    {
        return this;
    }
}
