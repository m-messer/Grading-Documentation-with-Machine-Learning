package GUI;

import java.awt.Color;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

import Environment.Habitat;
import Environment.Tile;
import Entities.Entity;
import Entities.Creature;
import Entities.CreatureState;

/**
 * This class collects and provides some statistical data on the state 
 * of a habitat. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the habitat.
 *
 * @version 2016.02.29
 */
public class HabitatStats
{
    // Counters for each type of entity (fox, rabbit, etc.) in the simulation.
    

    /**
     * Construct a HabitatStats object.
     */
    private HabitatStats() {}

    /**
     * Get details of what is in the habitat.
     * @return A string describing what is in the habitat.
     */
    public static String getPopulationDetails(Habitat habitat)
    {
        ArrayList<String> infoBuffer = new ArrayList<String>();
        
        HashMap<Class, Counter> counters = generateCounts(habitat);
        
        for(Class key : counters.keySet()) {
            Counter info = counters.get(key);
            String name = info.getName().replace("Entities.","");
            String count = "" + info.getCount();
            infoBuffer.add(name + ": " + count + ", ");
        }
        Collections.sort(infoBuffer);
        String detailsString = "";
        for(String info : infoBuffer) {
            detailsString += info;
        }
        return detailsString;
    }

    /**
     * Increment the count for one class of animal.
     * @param animalClass The class of animal to increment.
     */
    private static void incrementCount(HashMap<Class,Counter> counters, Class animalClass)
    {
        Counter count = counters.get(animalClass);
        if(count == null) {
            // We do not have a counter for this species yet.
            // Create one.
            count = new Counter(animalClass.getName());
            counters.put(animalClass, count);
        }
        count.increment();
    }
    
    /**
     * Generate counts of the number of foxes and rabbits.
     * These are not kept up to date as foxes and rabbits
     * are placed in the habitat, but only when a request
     * is made for the information.
     * @param habitat The habitat to generate the stats for.
     */
    private static HashMap<Class,Counter> generateCounts(Habitat habitat)
    {
        HashMap<Class, Counter> counters = new HashMap<>();
        
        for(Tile tile : habitat.getTiles()) {
            for(Entity entity : tile.getEntities()) {
                if(entity instanceof Creature) {
                    if(((Creature) entity).getState() == CreatureState.ALIVE) {
                        incrementCount(counters, entity.getClass());
                    }
                } else {
                    incrementCount(counters, entity.getClass());
                }
            }
        }
        return counters;
    }
}
