import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * A class representing a plant in the simulation.
 *
 * @version 2020.02.09
 */
public class Plant extends Organism
{
    private int plantCount;
    
    Random rand = Randomizer.getRandom();
    
    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant()
    {
        // super constructor call omitted
        plantCount = rand.nextInt(1000) + 1;    // Initialise plant's count to a random value in range.
    }
    
    /**
     * This is what the plant does most of the time.
     * The plants will either grow during the day or not grow (die) in harsh weather.
     * @param newAnimals A list to return newly born animals.
     * @param isDay True if it is day, False if not.
     * @param harshWeather True if weather is harsh, False if not
     */
    public void act(List<Actor> newPlants, boolean isDay, boolean harshWeather)
    {
        if (harshWeather) decCount(rand.nextInt(5));
        else if (isDay) incCountOne();
    }
    
    /**
     * Get the count of the plant.
     * @return count.
     */
    public int getCount()
    {
        return plantCount;
    }
    
    /**
     * Increment the plant's count by 10.
     */
    public void incCountOne()
    {
        plantCount = ((plantCount+=10) > 1000) ? 1000 : plantCount;   
    }
    
    /**
     * Increment the plant's count by an amount.
     * @param Integer amount to be incremented by.
     */
    public void incCount(int amount)
    {
        plantCount = ((plantCount+=amount)>1000) ? 1000 : plantCount;
    }
    
    /**
     * Decrement the plant's count by an amount.
     * @param Integer amount to be decremented by.
     */
    public void decCount(int amount)
    {
        plantCount = ((plantCount-=amount)>1000) ? 1000 : plantCount;
    }
    
    /**
     * Reduce the plant's count when eaten.
     */
    public void getEaten()
    {
        plantCount -= 50;
    }
    
    /**
     * @return the plant's food value for animals that consume it.
     */
    public int getFoodValue()
    {
        return 1;
    }
    
    /**
     * @return whether or not the plant is currently available (i.e. eatable)
     */
    public boolean isAvailable()
    {
        return plantCount>10;
    }
}
