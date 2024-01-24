import java.awt.Color;

/**
 * A graphical view of the simulation grid. This interface defines all possible different
 * views.
 *
 * @version 2016.03.18
 */
public interface SimulatorView
{
    /**
     * Define a colour to be used for a given species name.
     *
     * @param species The species to use this colour.
     * @param color The colour to be used for the given species.
     */
    void setColor(String species, Color color);

    void setEmptyColor(Color emptyColor);

    /**
     * Determine whether the simulation should continue to run.
     *
     * @return true If there is more than one species alive.
     */
    boolean isViable(Field field);

    /**
     * Show the current status of the field.
     *
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    void showStatus(int step, int day, String time, Field field);
    
    /**
     * Prepare for a new run.
     */
    void reset();
}