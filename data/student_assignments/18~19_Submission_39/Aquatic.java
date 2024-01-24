import java.util.List;
/**
 * Write a description of class Aquatic here.
 *
 * @version (a version number or a date)
 */
public interface Aquatic
{
    /**
     * Makes the aquatic act.
     */
     public abstract void act(List<Aquatic> newAquatics);
    
    /**
     * Check whether the aquatic is alive or not.
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
     public abstract boolean isAlive();
}