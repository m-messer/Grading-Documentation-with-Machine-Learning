import java.util.List;
/**
 * A simple model of Female
 *
 * @version 2022.03.01 (15)
 */
public class Female extends Gender
{
    // The maximum number of births.
    private int MAX_LITTER_SIZE;
    private double BREEDING_PROBABILITY;
    // The likelihood of an animal breeding.
    /**
     * Create a female
     */
    public Female(int breeding_age,int breeding_end,int MAX_LITTER_SIZE,double BREEDING_PROBABILITY)
    {
        super(breeding_age,breeding_end);
        this.MAX_LITTER_SIZE=MAX_LITTER_SIZE;
        this.BREEDING_PROBABILITY= BREEDING_PROBABILITY;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */

    private void giveBirth(Animal animal,List<LivingThing> newAnimals) 
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = animal.getField();
        DateTime dateTime = animal.getDateTime();
        List<Location> free = field.getFreeAdjacentLocations(animal.getLocation());

        int births = breed(animal);
        //all animals breed in the same way
        for(int b = 0; b < births && free.size() > 0&&free.size()-b>=births; b++) {
            Location loc = free.remove(0);
            Class[] paraType= new Class[4];
            paraType[0]=boolean.class;
            paraType[1]=Field.class;
            paraType[2]=Location.class;
            paraType[3]=DateTime.class;
            //only know the actural type of the animal at runtime
            //create a new object by the constructor
            Animal newAnimal = animal.getClass().getDeclaredConstructor(paraType).newInstance(false,field,loc,dateTime);
            newAnimals.add(newAnimal);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed(Animal animal)
    {
        int births = 0;
        if(canBreed(animal) && isOppositeSexNearBy(animal)&&rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;

        }
        return births;
    }

    /**
     * Female give births.
     * @return null as no movement is created.
     */
    public Location behave(Animal animal,List<LivingThing> newAnimals) 
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        giveBirth(animal, newAnimals);
        //no movement
        return null;
    }

}
