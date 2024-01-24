import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a 
 * rectangular forest field containing
 * Animals: Tiger, weasel, chicken, bear and deer.
 * Plants: Grass and berry.
 * Hunter
 * 
 * @version 2020.02.22
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.

    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;      
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;        
    // The array of weathers spanning across the day.
    private static final String[] WEATHER_ARRAY = {"sunny", "windy", "rainy", "cloudy", "foggy", "cold"};

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
    // List of hunters in the field.
    private List<Hunter> hunters;

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // A generator for the population.
    private PopulationGenerator populationGenerator;

    // A boolean variable to keep track of the time of the day.
    private boolean isDayTime;
    // A string showing the current weather.
    private String currentWeather;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * 
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
        hunters = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        populationGenerator = new PopulationGenerator(view);

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
     * 
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(60);   
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal, plant, hunter, daytime and weather.
     */
    public void simulateOneStep()
    {
        step++;
        updateDayTime();
        updateCurrentWeather();

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>(); 

        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, isDayTime, currentWeather);
            if(! animal.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);

        // Provide space for newly produced plants.
        List<Plant> newPlants = new ArrayList<>();
        // Let all plants act.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(newPlants, isDayTime, currentWeather);
            if(! plant.isAlive()) {
                it.remove();
            }
        }

        // Add the newly produced plants to the main lists.
        plants.addAll(newPlants);

        // Let all hunters act.
        for(Iterator<Hunter> it = hunters.iterator(); it.hasNext(); ) {
            Hunter hunter = it.next();
            hunter.act(currentWeather, step);            
        }

        view.showStatus(step, field, currentWeather);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();    // reset the animals on the field.
        plants.clear();     // reset the plants on the field.
        hunters.clear();    // reset the hunters on the field.
        updateDayTime();    // reset the daytime.
        updateCurrentWeather();    // reset the weather.

        populationGenerator.populate(field, animals, plants, hunters);

        // Show the starting state in the view.
        view.showStatus(step, field, currentWeather);
    }

    /**
     * Update the time of the day. 
     * Updates according to the number of steps encountered.
     * Day and night is updated every 25 steps.
     */
    private void updateDayTime()
    {
        isDayTime = ((step / 25) % 2 == 1);
    }

    /**
     * Updates the current weather to a random weather.
     * It's being updated every 25 days.
     */
    private void updateCurrentWeather()
    {
        if(step % 25 == 0) {
            // Weather changes randomly during the day and night.
            currentWeather = WEATHER_ARRAY[(int)(Math.random()*WEATHER_ARRAY.length)];
        }
    }

    /**
     * Pause for a given time.
     * 
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
