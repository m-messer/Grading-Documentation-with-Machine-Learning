import java.util.Random;

/**
 * This class represents the weather that can occur within each step.
 *
 * @version (1.0)
 */
public class Weather
{
    //represents the temperature in each step
    private double temperature = -30;
    // stores what season it is 
    private boolean summer = true;
    // a value representing the probability that global warming will destroy all arctic life
    private static final double GLOBALWARMING_PROBABILITY = 0.005;
    // The probability temperature will increase/decrease
    private static final double SET_TEMP_PROBABILITY = 0.5;
    //Shows whether it's snowing or not
    private static boolean snow = false;
    // The probability it's snowing if the temperature is below zero
    private static final double SNOWING_PROBABILITY = 0.35;
    //Holds the check that global warming has killed all the animals
    private static boolean globalWarming = false;
    // Maximum temperature in summer
    private static final double MAX_SUMMER = 20.5;
    // Minumum temperature in summer
    private static final double MIN_SUMMER = -10.5;
    // Maximum temperature in winter
    private static final double MAX_WINTER = 0.0;
    // Minimum temperature in winter
    private static final double MIN_WINTER = -50.0;
    
    // Holds a random generator to generate probabilities
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Constructor for objects of class Weather
     */
    public Weather()
    {
    }
    
    /**
     * @return whether it is snowing or not. 
     * It can only snow below zero degrees
     */
    public boolean isSnowing()
    {
        if (temperature < 0 && rand.nextDouble() <= SNOWING_PROBABILITY){
            snow = true;
        }
        else {
            snow = false;
        }
        return snow;
    }
    
    /**
     * Changes the season from summer to winter
     */
    public void setSummer()
    {
        summer = !summer;
    }
    
    /**
     * @return whether global warming has occured
     */
    public boolean globalWarming()
    {
       return globalWarming;
    }
    
    /**
     * Sets the temperature at each step by incrementing/decrementing the 
     * current temperature by one
     */
    public void generateTemp()
    {
        // checks whether global warming will occur and sets temp accordingly
        if(rand.nextDouble() <= GLOBALWARMING_PROBABILITY){
            temperature++; 
            if(temperature >= 30) {
               globalWarming = true;
            }
        }
        // otherwise, checks if it's summer (as temperature can reach higher)
        else if(summer){
            if(rand.nextDouble() <= SET_TEMP_PROBABILITY && temperature <= MAX_SUMMER) {
                temperature ++; 
            }
            else if (temperature >= MIN_SUMMER){
                temperature --;
            }
        }
        // otherwise it's winter
        else{
            if(rand.nextDouble() <= SET_TEMP_PROBABILITY && temperature <= MAX_WINTER) {
                temperature ++; 
            }
            else if (temperature >= MIN_WINTER ){
                temperature --;
            }
        }
    }
    
    /**
     * @return the current temperature for this step
     */
    public double getTemp()
    {
       return temperature;
    }
    
    /**
     * @return the current season for this step
     */
    public boolean getSummer()
    {
       return summer;
    }
}
