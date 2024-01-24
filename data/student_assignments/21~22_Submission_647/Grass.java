import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 *  A model of grass.
 */
public class Grass extends Animal
{
    
    private static final int BREEDING_AGE = 0;
    private static final int MAX_AGE = 4000;
    private static final int MAX_FOOD_VALUE = 1;
    private static final double BREEDING_PROBABILITY = 0.3;
    private static final int MAX_LITTER_SIZE = 8;
    private static final Random rand = Randomizer.getRandom();

    /**
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
        }
    }

    /**
     */
    public int breed()
    {
        int births = 0;
        if(rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    public boolean canBreed()
    {
        return getAge() > BREEDING_AGE;
    }
    
    public Grass generateNewAnimal(boolean randomAge, Field field, Location loc)
    {
        return new Grass(false, field, loc);
    }

    public void actDay(List<Actor> newActors)
    {
        incrementAge();
        if(isAlive()) {
            if(getField().isRainy()){
                giveBirth(newActors);
            }
        }
    }

    public void actNight(List<Actor> newActors)
    {
        incrementAge();
        if(isAlive()) {
            if(getField().isRainy()){
                giveBirth(newActors);
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
    
    protected void giveBirth(List<Actor> newActors)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocationsForGrass(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Object obj = field.getObjectAt(loc);
            Animal young = generateNewAnimal(false, field, loc);
            newActors.add(young);
        }
    }
}
