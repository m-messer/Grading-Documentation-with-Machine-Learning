import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A simple model of a vole.
 * Voles age, move, breed, sick, eat rice and die.
 *
 * @version 2020.2.22
 */
public class Vole extends Animal
{
    // Characteristics shared by all voles (class variables).

    // The age at which a vole can start to breed.
    private static final int BREEDING_AGE = 3;
    // The age to which a vole can live.
    private static final int MAX_AGE = 60;
    // The likelihood of a vole breeding.
    private static final double BREEDING_PROBABILITY = 0.95;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The food value of a single rice. In effect, this is the
    // number of steps a vole can go before it has to eat again.
    private static final int RICE_FOOD_VALUE = 12;
    //The probability of vole get sick.
    private static final double ILLNESS_PROBABILITY = 0.02;
    //The probability of vole die of sick.
    private static final double ILLNESS_DEATH_PROBABILITY = 0.65;
    //The probability of vole cure from sick.
    private static final double ILLNESS_CURE_PROBABILITY = 0.60;
    
    
    // Individual characteristics (instance fields).
    
    // The vole's age.
    private int age;
    private int foodLevel;
    //Determine whether voles can get sick
    private boolean canSick = true;

    /**
     * Create a new vole. A vole may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the vole will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Vole(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RICE_FOOD_VALUE);
        }else {
            age = 0;
            foodLevel = RICE_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the vole does most of the time - it runs 
     * around. Sometimes it will breed, sick or die of old age.
     * @param newVoles A list to return newly born voles.
     */
    public void act(List<Animal> newVoles)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            
            if(getSex()){
               giveBirth(newVoles); 
             }
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocationAnimal(getLocation());
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
         if(isAlive()){
           if(canSick && rand.nextDouble() <= ILLNESS_PROBABILITY){
                getSick();
                antiBody();
            }
          if(getAnimalState()){
                sick();
            }
        }
    }

    /**
     * Increase the age.
     * This could result in the vole's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this vole more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for grass adjacent to the current location.
     * Only the first live grass is eaten.
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
            if(plant instanceof Rice) {
                Rice rice = (Rice) plant;
                if(rice.isAlive()) { 
                    rice.setDead();
                    foodLevel = RICE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this vole is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newVoles A list to return newly born voles.
     */
    private void giveBirth(List<Animal> newVoles)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Vole) { 
               Vole vole1 = (Vole) animal;
               if(vole1.getSex() != this.getSex() ){
                 List<Location> free = field.getFreeAdjacentLocationsAnimal(getLocation());
                 int births = breed();
                 for(int b = 0; b < births && free.size() > 0; b++) {
                  Location loc = free.remove(0);
                  Vole young = new Vole(false, field, loc);
                  newVoles.add(young);
                 }
               }
            }
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
     * A vole can breed if it has reached the breeding age.
     * @return true if the vole can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * The sick preformence of vole. it will spread of disease and
     * determine if voles will die from disease or heal themselves
     */
    private void sick()
    {
       spread();
       if(rand.nextDouble() <= ILLNESS_DEATH_PROBABILITY ) {
            setDead();
        }else if(rand.nextDouble() <= ILLNESS_CURE_PROBABILITY){
            getCure();
        }
    }
    
    /**
     * Transmit disease to surrounding voles.
     */
    private void spread()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Vole) { 
               Vole vole = (Vole) animal;
               if(vole.getCanSick()){
                  vole.getSick();
                  vole.antiBody();
                 }
            }
        }
    }
        
     /**
     * A vole can sick if it don't have antibodies
     * @return true if the vole can sick, false otherwise.
     */
    public boolean getCanSick()
    {
        return canSick;
    }
    /**
     * Creat an antibody, if the vole sick once, it will never sick twice.
     */
    public void antiBody()
    {
        canSick = false;
    }
}
