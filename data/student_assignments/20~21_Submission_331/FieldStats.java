import java.util.HashMap;

/**
 * This class collects and provides some statistical data on the state 
 * of a field. It is flexible: it will create and maintain a counter 
 * for any class of object that is found within the field.
 *
 * @version 2021.03.01
 */
public class FieldStats
{
    // Counters for each type of entity (panda, gorilla, etc.) in the simulation.
    private final HashMap<String, Counter> counters;
    // Whether the counters are currently up to date.
    private boolean countsValid;

    /**
     * Construct a FieldStats object.
     */
    public FieldStats()
    {
        // Set up a collection for counters for each type of actor that could be encountered.
        counters = new HashMap<>();
        countsValid = false;
    }

    /**
     * Get formatted details of what actors are in the field and how many of them there are.
     *
     * @return A string describing what is in the field.
     */
    public String getPopulationDetails(Field field)
    {
        StringBuilder buffer = new StringBuilder();
        if(!countsValid) {
            generateCounts(field);
        }
        for(String species : counters.keySet()) {
            Counter info = counters.get(species);
            buffer.append(info.getSpecies());
            buffer.append(": ");
            buffer.append(info.getCount());
            buffer.append(' ');
        }
        return buffer.toString();
    }
    
    /**
     * Get the number of individuals in the population of a given species name.
     *
     * @param field The field currently occupied.
     * @param species The species to find the population of.
     * @return An int with the number for this species.
     */
    public int getPopulationCount(Field field, String species)
    {
        if(!countsValid) {
            generateCounts(field);
        }

        Counter counter = counters.get(species);
        return counter.getCount();
    }
    
    /**
     * Invalidate the current set of statistics, so reset all counts to zero.
     */
    public void reset()
    {
        countsValid = false;
        for(String species : counters.keySet()) {
            Counter count = counters.get(species);
            count.reset();
        }
    }

    /**
     * Increment the count for one species name.
     */
    public void incrementCount(String species)
    {
        Counter count = counters.get(species);
        if(count == null) {
            // We do not have a counter for this species yet.
            // Create one.
            count = new Counter(species);
            counters.put(species, count);
        }
        count.increment();
    }

    /**
     * Indicate that an animal count has been completed, so counters are up to date.
     */
    public void countFinished()
    {
        countsValid = true;
    }

    /**
     * Determine whether the simulation is still viable, so if it should continue to run.
     *
     * @param field The field currently occupied.
     * @return true if there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        // How many counts are non-zero.
        int nonZero = 0;
        if(!countsValid) {
            generateCounts(field);
        }
        for(String species : counters.keySet()) {
            Counter info = counters.get(species);
            if(info.getCount() > 0) {
                nonZero++;
            }
        }
        return nonZero > 1;
    }
    
    /**
     * Generate counts of the number of actors. These are not kept up to date as actors
     * are placed in the field, but only when a request is made for the information.
     *
     * @param field The field to generate the stats for.
     */
    private void generateCounts(Field field)
    {
        reset();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Actor actor = (Actor) field.getObjectAt(row, col);
                if(actor != null) {
                    incrementCount(actor.getSpecies());
                }
            }
        }
        countsValid = true;
    }
}
