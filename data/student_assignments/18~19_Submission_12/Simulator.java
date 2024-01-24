import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.1;
    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.2;    
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.2; 
    private static final double EAGLE_CREATION_PROBABILITY = 0.1;
    private static final double LION_CREATION_PROBABILITY = 0.15;
    private static final double OAKTREE_CREATION_PROBABILITY = 0.5;
    
    // List of animals in the field.
    private List<Base> bases;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    //the current time of the simultator.
    private Time time;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Sets the time mode of the simulator.
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        this.time = new Time(24);
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        bases = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.LIGHT_GRAY);
        view.setColor(Fox.class, Color.RED);
        view.setColor(Squirrel.class, Color.DARK_GRAY);
        view.setColor(Eagle.class, Color.BLACK);
        view.setColor(Lion.class, Color.ORANGE);
        view.setColor(OakTree.class, Color.GREEN);
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
             delay(110);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of all the species.
     */
    public void simulateOneStep()
    {
        step++;
        // For ever step increses the hour by one.
        time.setTime(step);
        // Provide space for newborn animals.
        List<Base> newBases = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<Base> it = bases.iterator(); it.hasNext(); ) {
            Base base = it.next();
            base.act(newBases, time);
            if(! base.isAlive()) {
                it.remove();
            }
        }
               
        // Add the species to the main lists.
        bases.addAll(newBases);
        
        view.showStatus(step, field, time);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        time.setTime(step);
        bases.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field, time);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    bases.add(fox);
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    bases.add(rabbit);
                }
                else if(rand.nextDouble() <= SQUIRREL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Squirrel squirrel = new Squirrel(true, field, location);
                    bases.add(squirrel);
                }
                else if(rand.nextDouble() <= EAGLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Eagle eagle = new Eagle(true, field, location);
                    bases.add(eagle);
                }
                else if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    bases.add(lion);
                }
                else if(rand.nextDouble() <= OAKTREE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    OakTree oakTree = new OakTree(true, field, location);
                    bases.add(oakTree);
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
