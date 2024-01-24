import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * the unique meothods of the preditor class.
 *
 */
public abstract class Predator extends Animal
{
    // instance variables - replace the example below with your own

    protected int PREY_FOOD_LEVEL;
    // The amount which increses the foodlevel of the animal when it eats a Prey specie.
    protected int PLANT_FOOD_LEVEL;
    // The amount which increses the foodlevel of the animal when it eats a Plant specie.
    private boolean isFoggy;
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Predator(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        
        isFoggy = false;
    }

    /**
     * Look for prey and plants adjacent to the current location.
     * Only the first prey/plant is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Prey) {
                Prey prey = (Prey) animal;
                // catching prey is affected by the fog
                double catchPrey;
                if (isFoggy) {
                    catchPrey = rand.nextDouble();
                }
                else {
                    catchPrey = 1;
                }
                // 50% chance of catching prey if its foggy 
                if(prey.isAlive() && catchPrey > 0.5 && prey.canBreed()) { 
                    prey.setDead();
                    foodLevel += PREY_FOOD_LEVEL;
                    if (foodLevel > PREY_FOOD_LEVEL) { 
                        foodLevel = PREY_FOOD_LEVEL;
                    }
                    return where;
                }
                
            }
            if(animal instanceof Plants) {
                Plants plants = (Plants) animal;
                if(plants.isAlive() && canBreed()) { 
                    plants.setDead();
                    foodLevel += PLANT_FOOD_LEVEL;
                    if (foodLevel > PREY_FOOD_LEVEL) {
                        foodLevel = PREY_FOOD_LEVEL;
                    }
                    return where;
                }
            }
        }
        return null;
    }

    abstract Base returnMyType(boolean randomAge, Field field, Location location);

    /**
     * This is what the predator does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Base> newPredators, Time time)
    {
        incrementHunger();
        changeWeather();
        incrementAge();   
        if(isAlive()) {
                giveBirth(newPredators);            
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
         *  Decides the weather. If it is currently not foggy, there is a 20% 
         *  chance of it becoming foggy. If it is already foggy, there is a 50% 
         *  chance of it either staying foggy or returning to normal.
         */
    private void changeWeather()
        {
           double weatherChance = rand.nextDouble();
            if (!isFoggy) {
                if (weatherChance < 0.2) {
                    setWeather();
                }
            }
            else {
                if (weatherChance < 0.5) {
                    setWeather();
                }
            }

        }

    /**
         * Changes the weather between foggy and not foggy
         */
        public void setWeather()
        {
            isFoggy = !isFoggy;
        }

}

