import java.awt.Color;

/**
 * A graphical view of the simulation grid. This interface defines all possible different
 * views.
 * 
 * Copy from the book- https://www.bluej.org/objects-first/ -Book Projects -chapter 12 -foxes-and-rabbits-graph
 * @version 2016.03.18
 */
public interface SimulatorView
{
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    void setColor(Class<?> animalClass, Color color);

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    boolean isViable(Field field);

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    void showStatus(DateTime dateTime, Field field);

    /**
     * Prepare for a new run.
     */
    void reset();
}
