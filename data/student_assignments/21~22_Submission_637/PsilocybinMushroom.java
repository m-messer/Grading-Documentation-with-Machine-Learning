import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;

/**
 * A simple model of a PsilocybinMushroom plant. 
 * PsilocybinMushrooms age and spread as they get older and they die once they reach a certain age. 
 * PsilocybinMushrooms only grow and spread at the night and during the day are not active. 
 *
 * @version 2022.03.02
 */
public class PsilocybinMushroom extends Plant
{
    private static final int MAX_AGE = 100;   
    private static final int NEW_PsilocybinMushroom = 2;
    private static final Random rand = Randomizer.getRandom();
    private int age;

    /**
     * Create a PsilocybinMushroom. A PsilocybinMushroom can be created as a new born (age zero) or with a random age.
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public PsilocybinMushroom(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt();
        } 
        else {
            age = 0;
        }
    }
    /**
     * Increments the age of PsilocybinMushroom and if the age of the mushroom is larger than the max age it uses setDead method.
     * @return age
     */
      private int incrementAge()
    {
        age++;
         if(age > MAX_AGE) {
        setDead();
        }
        return age;
    }
    
    
    /**
     * Simulates the mushrooms activity at day, during day it does not doing anything other than age. 
     * @param newPsilocybinMushroom 
     */
    
    public void actDay(List<Plant> newPsilocybinMushroom) { 
        incrementAge();
        }
        
    /**
     * Simulates the mushrooms activity at night, during night it increments age and spreads in the area around it.
     * @param newPsilocybinMushrooms
     */    
    public void actNight(List<Plant> newPsilocybinMushrooms) {
         incrementAge();
         if(isAlive()) {
            spread(newPsilocybinMushrooms); 
         }
      } 
    
      /**
       * Simulates the mushrooms growth/expansion, only if age is less than 0 and the age is a multiple of 2. If requirements 
       * are met the mushroom spreads to the adjacent locations. 
       * @param newPsilocybinMushrooms
       */
     public void spread(List<Plant> newPsilocybinMushrooms) 
         {
        // New PsilocybinMushroom spread into adjacent locations.
        // Get a list of adjacent locations with space for PsilocybinMushroom.
        if(age > 0 && age % NEW_PsilocybinMushroom == 0) {
            Field field = getField();
            List<Location> locations = field.adjacentLocations(getLocation());
            //boolean spread = false;
            int seeds = 1;
            for(Location location : locations) {
               ArrayList<Plant> currentPlants = location.ReturnPlants();
               while(seeds == 1) {
                  if(currentPlants.size() < 10) {
                     PsilocybinMushroom newPlant = new PsilocybinMushroom(false, field, location);
                     newPsilocybinMushrooms.add(newPlant);
                     seeds--;
                    }
                }
            }
        }
    }
    }
   
