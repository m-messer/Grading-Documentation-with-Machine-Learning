import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A predator-prey simulator, based on a rectangular field
 * containing many animals. 
 *
 * @version 2022.02.28 
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // List of organism in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // A population generator.
    private PopulationGenerator populator;
    //The probability that a disease will be created in an animal. 
    private static final double DISEASE_CREATION_PROBABILITY = 0.001;
   
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
       
        organisms = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        populator = new PopulationGenerator(view);
               
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
            delay(60);   // uncomment this to run more slowly
        }
    }
   
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each plant and animal.
     */
    public void simulateOneStep()
    {
        step++;
        field.playWeather();
        if (step % 2 == 0) {
            field.setCurrentTime(Time.NIGHT);
        }
        else {
            field.setCurrentTime(Time.DAY);
        }
        
        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();     
        //
        Random rand = new Random(); 
        // Let all organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            if (organism instanceof Animal && rand.nextDouble() <= DISEASE_CREATION_PROBABILITY ) {
                Animal animal = (Animal) organism;
                animal.infect(); 
            }
            organism.act(newOrganisms);
            if(! organism.isAlive()) {
                it.remove(); 
            }
        }
               
        // Add the newly born organisms to the main lists.
        organisms.addAll(newOrganisms);

        view.showStatus(step, field);
    }
       
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
       
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
   
    /**
     * Randomly populate the field with plants and animals.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        populator.populate(field, organisms);
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