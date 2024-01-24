import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing multiple animals and plants.
 *
 * @version 2021.3.3
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 140;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 120;

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
    // The weather of the simulation.
    private Weather weather;
    // The time of day in the simulation.
    private Time time;
    // To generate the initial population of the simulation.
    private PopulationGenerator populationGenerator;

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
        weather = new Weather();
        time = new Time();
        

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        populationGenerator = new PopulationGenerator(view);
        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (2000 steps).
     */
    public void runLongSimulation()
    {
        simulate(2000);
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
            //delay(50);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal and plant.
     */
    public void simulateOneStep()
    {
        step++;
        changeEnvironment();

        // Provide space for newborn animals and plants.
        List<Animal> newAnimals = new ArrayList<>(); 
        List<Plant>  newPlants = new ArrayList<>();

        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, time.getIsDay(), weather.getCurrent());
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        //Let all plants act
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(newPlants, time.getIsDay(), weather);
            if(! plant.isAlive()) {
                it.remove();
            }
        }

        // Add the newly reproduced animals and plants to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants);

        view.showStatus(time.getString(), weather.getCurrent(), step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        populationGenerator.populate(plants,animals,field);

        // Show the starting state in the view.
        view.showStatus(time.getString(), weather.getCurrent(), step, field);
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
     * Switch day and night time, and change the weather every 50 steps.
     */
    private void changeEnvironment()
    {
        if(step % 50 == 0) {
            time.changeTime();
        }

        if(step % 50 == 0) {
            weather.changeWeather();
        }
    }
}