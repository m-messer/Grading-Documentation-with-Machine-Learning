import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing gazelles and Liones.
 *
 * @version 2022.02.26 (3)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a Lion will be created in any given grid position.
    private static final double Lion_CREATION_PROBABILITY = 0.01;
    // The probability that a gazelle will be created in any given grid position.
    private static final double GAZELLE_CREATION_PROBABILITY = 0.07;   
    // The probability that a hyena will be created in any given grid position.
    private static final double HYENA_CREATION_PROBABILITY = 0.01;
    // The probability that a zebra will be created in any given grid position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.05;
    // The probability that a grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.3;
    // The probability that a wildebeast will be created in any given grid position.
    private static final double WILDEBEAST_CREATION_PROBABILITY = 0.07;

    // List of animals in the field.
    private List<Species> species;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    //The current weather of the simulator
    private Weather weather;
    //Simulation of a clock
    private Clock clock;
    
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
        
        species = new ArrayList<>();
        field = new Field(depth, width);
        weather = new Weather();
        clock = new Clock();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, weather);
        view.setColor(Gazelle.class, Color.GRAY);
        view.setColor(Lion.class, Color.YELLOW);
        view.setColor(Hyena.class, Color.RED);
        view.setColor(Wildebeast.class, Color.BLUE);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Zebra.class, Color.CYAN);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
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
            //delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * instance of species
     */
    public void simulateOneStep()
    {
        clock.tick(step);
        step++;

        // Provide space for newborn species.
        List<Species> newSpecies = new ArrayList<>();        
        // Let all species act.
        for(Iterator<Species> it = species.iterator(); it.hasNext(); ) {
            Species species = it.next();
            species.act(newSpecies, clock);
            if(! species.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born species to the main lists.
        species.addAll(newSpecies);
        weather.act(species);
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        species.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
        
        // Auto run simulation
        runLongSimulation();
    }
    
    /**
     * Randomly populate the field with animals
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= Lion_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion Lion = new Lion(true, field, location);
                    species.add(Lion);
                }
                else if(rand.nextDouble() <= GAZELLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Gazelle gazelle = new Gazelle(true, field, location);
                    species.add(gazelle);
                }
                else if(rand.nextDouble() <= HYENA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hyena hyena = new Hyena(true, field, location);
                    species.add(hyena);
                }
                else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, field, location);
                    species.add(zebra);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    species.add(grass);
                }
                else if(rand.nextDouble() <= WILDEBEAST_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Wildebeast wildebeast = new Wildebeast(true, field, location);
                    species.add(wildebeast);
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
}
