import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 *
 * @version 2022.03.02
 */
public class Marijuana extends Plant
{
    private static final int MAX_AGE = 100;  
    private static final int NEW_MARIJUANA = 2;
    private static final Random rand = Randomizer.getRandom();
    private int age;

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Marijuana(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt();
        } 
        else {
            age = 0;
        }
    }
    
      private int incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
        setDead();
        }
        return age;
    }
    
    public void actDay(List<Plant> newMarijuanas) {
         incrementAge();
         if(isAlive()) {
            spread(newMarijuanas); 
         }
      } 
    public void actNight(List<Plant> newMarijuanas) { 
        incrementAge();
        }
        
     public void spread(List<Plant> newMarijuanas) 
         {
        // New Marijuana spread into adjacent locations.
        // Get a list of adjacent locations with space for Marijuana.
        if(age > 0 && age % NEW_MARIJUANA == 0) {
            Field field = getField();
            List<Location> locations = field.adjacentLocations(getLocation());
            //boolean spread = false;
            int seeds = 1;
            for(Location location : locations) {
               ArrayList<Plant> currentPlants = location.ReturnPlants();
               while(seeds == 1) {
                  if(currentPlants.size() < 10) {
                     Marijuana newPlant = new Marijuana(false, field, location);
                     newMarijuanas.add(newPlant);
                     seeds--;
                    }
                }
            }
        }
    }
    }
   
