import java.util.List;
/**
 *A class representing the shared characteristics of plants.
 *
 * @version 2021.03.02
 */
public abstract class Plant implements Actor
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The plant position in the field.
    private Location location;

    private Simulator simulator;

    /**
     * Constructor for objects of class Plants
     */
    public Plant(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Place the plant at the new location in the given field.
     * @param newLocation The animal's new location.
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
     * This method implements the behaviour of a plant in the simulation.
     */
    public void act(List<Actor> newPlants)
    {
        incrementLifesSpan();
        if(simulator.getWeather() == "rain"){
            generatePlants(newPlants);
        }
    }     
    
    /**
     * This method generated new plants that will appear in the simualtor.
     */
    protected void generatePlants(List<Actor> newPlants)
    {
        Field field = getField();
       
        if(field!= null){
            List<Location> locations = getField().getFreeAdjacentLocations(getLocation());

            for(int i=0;locations.size()>0;i++)
            { 
                Location loc = locations.remove(0);
                Grass newGrass = new Grass(field, loc);
                newPlants.add(newGrass);
            }
        }
    }

    /**
     * Return the plant's location.
     * @return The plant's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * This method returns the field in which the plants's are created.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Check whether the plants are stiil alive.
     * @return true if the plant is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
     
    /**
     * This method sets the boolean value alive of each plant object to false and 
     * deletes the plant from the simulation.
     */
    public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * This method increments the "age" of the plant. Each plant can be displayed in the simulator for a certain 
     * amount of steps.This method is called every time the plant acts.
     */
    private void incrementLifesSpan()
    {
        int lifeSpan = getPlantAge();
        lifeSpan++;

        if(lifeSpan > getPlantMaxAge())
        {
            setDead();
        }
    }
    
    /**
     * This method returns the current "age" of a plant.
     * @return int age
     */
    abstract protected int getPlantAge();
    
    /**
     * This method returns the maximum 'age' that a plant can reach.
     * @return int MAX_AGE
    */
    abstract protected int getPlantMaxAge();
}
