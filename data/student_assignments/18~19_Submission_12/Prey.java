import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * Abstract class Prey - Contains the unique methods of the prey.
 *
 */
public abstract class Prey extends Animal
{
    // The amount which increses the foodlevel of the animal when it eats a Plant specie.
    protected int PLANT_FOOD_LEVEL;
    // If the prey has a disease.
    protected boolean isInfected;
    // Countdown to prey's death if infected.
    protected int countdown = 20;
    
    /**
     * Create a new prey at location in field.
     * 
     * Also the prey has a 2% chance of being infected
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Prey(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);
        if ( rand.nextDouble() < 0.02) {
            isInfected = true;
        }
        else {
            isInfected = false;
        }
    }
    
    /**
     * The prey looks through the adjacent cells and then sees if any of them are plants
     * if they are it will eat the plant and increase the food value level by the PLANT_FOOD_LEVEL.
     */    
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object base = field.getObjectAt(where);
            if(base instanceof Plants) {
                Plants plants = (Plants) base;
                if(plants.isAlive() && plants.canBreed()) { 
                    plants.setDead();
                    foodLevel += PLANT_FOOD_LEVEL;
                    if (foodLevel > PLANT_FOOD_LEVEL) {
                        foodLevel = PLANT_FOOD_LEVEL;
                    }
                    return where;
                }
            }
        } 
        return null;
    }
    
    abstract Base returnMyType(boolean randomAge, Field field, Location location);
    
    /**
     * This is what the pary does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * At night it sleeps so it dosnt moves but it does still get hungry.
     * @param newPrey A list to return newly born prey.
     */
    public void act(List<Base> newPrey, Time time)
    {
        incrementHunger();
        if(!time.isNight()){
            incrementAge();
            
        if(isAlive()) {
            giveBirth(newPrey);            
            // Try to move into a free location.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
              newLocation = getField().freeAdjacentLocation(getLocation());
            }

            if(newLocation != null) {
                setLocation(newLocation);
                if (isInfected) {
                    spreadInfection();
                    countdown--;
                    if (countdown == 0) {
                        setDead();
                    }
                }
            }
            else {
                // Overcrowding.
                setDead();
            }
                
          }
        }
    }
    
    /**
     *  This spreads the infection to any nearby prey. There is a 5%
     *  chance of catching this infection.
     */
    protected void spreadInfection()
    {
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location loc : adjacent) {
            Object obj = field.getObjectAt(loc);
            if (obj != null  && obj instanceof Prey) {  // only carries out when there is an animal
                Prey prey = (Prey) obj;
                if (rand.nextDouble() < 0.05) {
                    prey.isInfected = true;
                }
            }
        }
    }
}
