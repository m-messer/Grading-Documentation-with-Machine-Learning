import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Monkey
 * Monkeys age 
 * 
 * @Author Adam Doidge and Gwyn Barrientos.
 * @version 2022.03.02
 */
public class Monkey extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    // The age at which a rabbit can start to breed.
    private static final int BREEDING_AGE = 5;
    // The likelihood of a rabbit breeding.
    private static final double BREEDING_PROBABILITY = 0.75;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a single marijuana plant.
    private static final int Marijuana_FOOD_VALUE = 100;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // Has the rabbit given birth in the previous turn
    private boolean previouslyBirthed = false;
    // The age to which a rabbit can live.
    private int maxAge = 100;
    // The rabbit's age.
    private int age;
    // How many steps the monkey can go without food.
    private int foodLevel;
    // List of diseases this animal has
    protected List<Disease> diseases;
    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Monkey(boolean randomAge, Field field, Location location)
    {

        super(field, location);
        age = 0;
        diseases = new ArrayList<>();
        
        if(randomAge) {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(Marijuana_FOOD_VALUE);
        }
        else { 
            age = 0;
            foodLevel = Marijuana_FOOD_VALUE;
        }
    }
    
    /**
     * returns the list of all the monkey's diseases.
     * @return list of monkey's diseases
     */
    public List<Disease> diseses()
    {
        return diseases;
        }
    
    /**
     * Representation of what happens to the monkey during the day and what it does, aswell as the diseases the monkey has.
     * Its hunger and age increases, it finds food and it breeds. 
     * @param newMonkey A list to return newly born monkeys.
     * @param diseases A list likely to return new disease 
     */
    public void actDay(List<Animal> newMonkey, List<Disease> diseases)
    {
        incrementAge();
        incrementHunger();
        
        if(isAlive()) {
            diseaseStuff(diseases);
            giveBirth(newMonkey);            
            // Try to move into a free location.
            Location newLocation = findFood();
            if(newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());;
            }
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
     * Representation of what the monkey does during night.(just ages) 
     * @param newMonkey A list to return newly born monkeys.
     * @param diseases A list likely to return new disease 
     */
    public void actNight(List<Animal> newMonkeys, List<Disease> diseases)
    {
        incrementAge();
    }
    
    /**
     * 
     * @param newDiseases List of new diseases
     * 
     */
    private void diseaseStuff(List<Disease> newDiseases) {
        Iterator<Disease> it = newDiseases.iterator();
        while(it.hasNext()) {
            Disease newDisease = it.next();
            if(newDisease.canBeInfected(this) && newDisease.infect()){
                System.out.print(newDisease);
                diseases.add(newDisease);
                maxAge *= newDisease.returnAgeDecrease();
            }
        }
    }
    
    /**
     * 
     * reduces monkeys food level by one and checks if monkey has died
     * 
     */
    private void incrementHunger() {
        foodLevel--;
        if(foodLevel <=0) {
            setDead();
        }
    }
    
    /**
     * looks any food in adjacent locations and then returns the location of the food.
     * @returns location where food is.
     */

    private Location findFood() 
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Marijuana) {
                Marijuana marijuana = (Marijuana) plant;
                if (marijuana.isAlive()) {
                    marijuana.setDead();
                    foodLevel = Marijuana_FOOD_VALUE;
                    return where;
                }
            }
           
        
        }
         return null;
    }
    
    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * looks for other monkeys of opposite gender in adjacent locations and then tries to breed with it.
     * @returns true if it can breed, otherwise false.
     */
    private boolean findMate()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Monkey) {
                Monkey monkey = (Monkey) animal;
                if(monkey.isFemale() != this.isFemale()) { 
                    if(monkey.canBreed() && this.canBreed()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRabbits A list to return newly born rabbits.
     */
    private void giveBirth(List<Animal> newMonkeys)
    {
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if(findMate()){
            int births = breed();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Monkey young = new Monkey(false, field, loc);
                newMonkeys.add(young);
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
        if(rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        if (!(previouslyBirthed)){
            previouslyBirthed = true;
            return age >= BREEDING_AGE;
        }
        else{
            previouslyBirthed = false;
            return false;
        }
    }
}

