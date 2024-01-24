import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.HashMap;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing several species. Includes weather, time and disease
 *
 * @version 3
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 140;
    // The probability that a species will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.03;
    private static final double HARE_CREATION_PROBABILITY = 0.06;  
    private static final double DEER_CREATION_PROBABILITY = 0.06;  
    private static final double COYOTE_CREATION_PROBABILITY = 0.03;  
    private static final double JAGUAR_CREATION_PROBABILITY = 0.03; 
    private static final double BERRIES_CREATION_PROBABILITY = 0.02;  

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    private int weather;
    private HashMap<Integer, String> weathers;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);
        
        // Create a view of the state of each location in the field.
        Color brown = new Color(210, 105, 30); // Color brown
        
        view = new SimulatorView(depth, width);
        view.setColor(Hare.class, Color.LIGHT_GRAY);
        view.setColor(Wolf.class, Color.CYAN);
        view.setColor(Deer.class, brown);
        view.setColor(Jaguar.class, Color.BLUE);
        view.setColor(Coyote.class, Color.YELLOW);
        view.setColor(Berries.class, Color.RED);
        
        weathers = new HashMap<>();
        weathers.put(0, "Clear"); //0 - clear nothing happens
        weathers.put(1,"Rain"); //1 - rain plants grow
        weathers.put(2,"Snow"); //2 - snow plants may die
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (400 steps).
     */
    public void runLongSimulation()
    {
        simulate(400);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(45);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * species
     */
    public void simulateOneStep()
    {
        step++;
    

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();  
        List<Plant> newPlants = new ArrayList<>();  
    
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, isDay());
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.grow(newPlants, isDay(), weather());
            if(! plant.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born wolfes and Hares to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants);

        view.showStatus(step, field, weather(), isDay());
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field, weather(), isDay());
    }
    
    /**
     * Randomly populate the field with wolfes and Hares.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= BERRIES_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    List <Location> locations = field.getFreeAdjacentLocations(location); 
                    
                    for(Location loc : locations){
                        Berries berries = new Berries(field, loc);
                        plants.add(berries);
                    }
                    
                    Berries berries = new Berries(field, location);
                    plants.add(berries);
                }
                else if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, field, location);
                    animals.add(wolf);
                }
                else if(rand.nextDouble() <= HARE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hare hare = new Hare(true, field, location);
                    animals.add(hare);
                }
                else if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location);
                    animals.add(deer);
                }
                else if(rand.nextDouble() <= COYOTE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Coyote coyote = new Coyote(true, field, location);
                    animals.add(coyote);
                }
                else if(rand.nextDouble() <= JAGUAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Jaguar jaguar = new Jaguar(true, field, location);
                    animals.add(jaguar);
                }
                
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
    
    /**
     * Returns whether its day or night
     */
    private boolean isDay()
    {
        int time = step % 24;
        
        if (time > 5 && time < 18) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns a weather condition
     */
    private String weather()
    {
        if ( step % 24 == 0) {
            Random r = new Random();
            weather = r.nextInt(3);
        } 
        
        return weathers.get(weather);
    }
}
