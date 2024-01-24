import java.awt.Color;

/**
 * A graphical view of the simulation grid. This interface defines all possible different
 * views.
 *
 * @version 22.02.19
 */
public class TextView implements SimulatorView
{
    private FieldStats stats;

    /**
     * Constructor of objects of class TextView
     */
    public TextView()
    {
        stats = new FieldStats();
    }

    /**
     * Define a color to be used for a given class of organism.
     * @param organismClass The organism's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> organismClass, Color color)
    {
        //nothing...
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        stats.reset();
        String details = stats.getPopulationDetails(field);
        
        //This can be uncommented to see the stat values
        //System.out.println("Step: " + formattedStep + " " + details);
    }
    
    /**
     * Prepare for a new run.
     */
    public void reset()
    {
        stats.reset();
    }
}