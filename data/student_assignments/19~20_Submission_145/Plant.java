import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 * 
 * @version 2020.02.14
 */
public abstract class Plant extends Actor
{ 
    // To check if the plant is infected.
    private boolean isInfected;
    
    private Random rand;
  
    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
        rand = new Random();
    }

    /**
     * Make this plant act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newPlants     A list to receive newly born plants.
     */
    abstract public void act(List<Plant> newPlants, boolean isDayTime, String currentWeather);

    /**
     * Set the plant as infected or not infected.
     * 
     * @param infected  A boolean that shows if plant is infected.
     */
    protected void setIsInfected(boolean infected)
    {
        isInfected = infected;
    }

    /**
     * Returns whether the plant is infected or not.
     * 
     * @return boolean  The infection state of the plant.
     */
    public boolean isInfected()
    { 
        return isInfected;        
    }  

    /**
     * Checks if the weather is suitable for a plant 
     * to carry out weather specific beahaviour.
     * 
     * @param currentWeather    A string for the current weather in the simulation.
     * @return boolean          Whether the weather is suitable or not.
     */
    public boolean checkSuitableWeather(String currentWeather)
    {
        if( currentWeather.equals(getSuitableWeather())) {
            return true;
        }             
        return false;
    } 

    /**
     * Returns the suitable weather for the plant.
     * 
     * @return String   The suitable weather for the plant.
     */
    abstract public String getSuitableWeather();
}
