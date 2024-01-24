import java.util.Random;
import java.util.Iterator;
import java.util.List;

/**
 * Class that encompasses the characteristics shared by all organisms, both animal and plant.
 *
 * @version 2021.03.02 (3)
 */
public abstract class Organism
{
    // Flag that specifies whether this organism is alive
    protected boolean alive;
    
    // The field in which it lives
    protected Field field;
    
    // The organism's location
    protected Location location;

    // The age (in steps) of the organism
    protected int age;

    // A reference to the main simulator class
    protected Simulator simulator;

    protected static final Random rand = Randomizer.getRandom();
    
    //How many steps there are to go until this organism is not sick anymore.
    protected int diseaseLevel;

    /**
     * Creates a new object of type Organism
     * 
     * @param randomAge specifies whether this organism's starting age should be random, or 0
     * @param field The field currently occupied
     * @param location The location within the field
     * @param simulator Used to pass a reference to the main simulator class
     */
    public Organism(boolean randomAge, Field field, Location location, Simulator simulator){
        this.simulator = simulator;

        alive = true;
        
        diseaseLevel = 0;

        age = 0;
        if(randomAge) {
            age = rand.nextInt(getDefaultMaxAge());
        }

        this.field = field;
        setLocation(location);
    }
    
    /**
     * Returns the number of steps this organism might live for under normal conditions (of health).
     */
    abstract protected int getDefaultMaxAge();
    
    /**
     * Cures this organism of disease.
     */
    public void cureDisease(){
        diseaseLevel = 0;
    }
    
    /**
     * Makes this organism act: makes it do whatever it needs or wants to do.
     */
    public void act(List<Organism> newOrganisms){
        incrementAge();
    }
    
    /**
     * Specifies whether this organism is diseased.
     * @return true If this organism has a disease.
     */
    protected boolean isDiseased(){
        return (diseaseLevel>0);
    }
    
    // ****** added
    /**
     * Decreases this organism's disease level (amount of steps until cured), and cures it when appropriate.
     */
    protected void decreaseDiseaseLevel(){
        if (diseaseLevel>0)
            diseaseLevel--;
            
        if (diseaseLevel==0)
            cureDisease();
    }
    
    /**
     * Makes this organism sick.
     */
    abstract protected void getDisease();
    
    /**
     * Gets the maximum age this organism can live up to as of right now.
     */
    public abstract int getMaxAge();

    /**
     * Place the organism at the new location in the given field.
     * @param newLocation The organism's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the organism's field.
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Makes this organism older by one step.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Return the organism's location.
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Check whether the organism is alive or not.
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

}
