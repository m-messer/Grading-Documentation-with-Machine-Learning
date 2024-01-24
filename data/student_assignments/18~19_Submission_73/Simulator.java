import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals and plants.
 *
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The number of steps before the day changes to night and vice versa.
    private static final int NIGHT_LENGTH = 3;
    // An unmodifiable map containing the creation probabilities of each entity.
    private static final Map<Class, Double> creationProbabilities;
    static {
        Map<Class, Double> map = new HashMap<>();
        map.put(Hare.class, 0.20);
        map.put(Bobcat.class, 0.03);
        map.put(Deer.class, 0.05);
        map.put(Squirrel.class, 0.20);
        map.put(Bear.class, 0.02);
        map.put(Plant.class, 0.5);
        creationProbabilities = Collections.unmodifiableMap(map);
    }

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
    //Whether it is raining or not
    boolean isRaining;
    // Whether it is day or night.
    private boolean night;
    
    private final Random rand = Randomizer.getRandom();

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
        view = new SimulatorView(depth, width);
        view.setColor(Hare.class, Color.ORANGE);
        view.setColor(Bobcat.class, Color.BLUE);
        view.setColor(Deer.class, Color.YELLOW);
        view.setColor(Bear.class, Color.RED);
        view.setColor(Squirrel.class, Color.MAGENTA);
        view.setColor(Plant.class, Color.GREEN);
        // Setup a valid starting point.
        reset();
        
        isRaining = false;
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
            delay(60);    // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * plant and animal.
     */
    public void simulateOneStep()
    {
        step++;
        // Change the time of day every 10 steps.
        if (step % NIGHT_LENGTH == 0) {
            night = !night;             
        }

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            if ((night && animal.isNocturnal()) || (!night && !animal.isNocturnal())) {
                animal.act(newAnimals);
            }
            else {
                animal.sleep();
            }
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        // randomly generates if its raining or not.
        isRaining = rand.nextBoolean();
        // Allows plants to grow only when raining.
        if (isRaining) {
            for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
                Plant plant = it.next();
                plant.grow();
            }
        }

        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field, isRaining);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        night = false;
        animals.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field, isRaining);
    }

    /**
     * Randomly populate the field with animals.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= creationProbabilities.get(Bobcat.class)) {
                    Location location = new Location(row, col);
                    Bobcat bobcat = new Bobcat(true, field, location);
                    animals.add(bobcat);
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Hare.class)) {
                    Location location = new Location(row, col);
                    Hare hare = new Hare(true, field, location);
                    animals.add(hare);
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Deer.class)) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location);
                    animals.add(deer);
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Bear.class)) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    animals.add(bear);
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Squirrel.class)) {
                    Location location = new Location(row, col);
                    Squirrel squirrel = new Squirrel(true, field, location);
                    animals.add(squirrel);
                }

                if(rand.nextDouble() <= creationProbabilities.get(Plant.class)) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(field, location);
                    plants.add(plant);
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
