import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A predator-prey simulator, based on a rectangular field
 * currently containing: tuna, plankton, sharks, whales and shrimp.
 * As well as seaweed for the prey to eat.
 *
 * @version 2021.02.28
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    
    // The probabilities that each organism will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.02;
    private static final double PLANKTON_CREATION_PROBABILITY = 0.07; 
    private static final double SHRIMP_CREATION_PROBABILITY = 0.07;
    private static final double WHALE_CREATION_PROBABILITY = 0.02;   
    private static final double TUNA_CREATION_PROBABILITY = 0.02;
    private static final double SEAWEED_CREATION_PROBABILITY = 0.1;

    //The number of steps that a day and night last
    private static final int DAY_NIGHT_CYCLE = 5;

    // List of animals in the field.
    private List<Animal> animals;
    // List of the plants in the field.
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // The current weather conditions of the simulation.
    private Weather currentWeather;
    // Whether or not it is daytime in the simulation.
    private boolean daytime;

    /**
     * Main method to make running a long simulation easier.
     */
    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.runLongSimulation();
    }

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
        field = new Field(depth, width);
        daytime = true;

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        
        // Outline the color of each organism in the simulation.
        view.setColor(Plankton.class, Color.GREEN);
        view.setColor(Shark.class, Color.BLUE);
        view.setColor(Tuna.class, Color.GRAY);
        view.setColor(Shrimp.class, Color.PINK);
        view.setColor(Whale.class, Color.CYAN);
        view.setColor(Seaweed.class, Color.BLACK);
        
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
            //delay(200);   // uncomment this to run more slowly
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

        Random random = Randomizer.getRandom();
        int randomNum = random.nextInt(Weather.values().length);
        // Choose a random weather condition.
        currentWeather = Weather.values()[randomNum];

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();

            if (isDaytime()) {
                animal.act(newAnimals);
            } else {
                animal.nightBehaviour(newAnimals);
            }

            if(!animal.isAlive()) {
                it.remove();
            }
        }
        
        // Provide space for new plants.
        List<Plant> newPlants = new ArrayList<>();
        // Let all plants grow (act).
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();

            if(!plant.isAlive()) {
                it.remove();
            }

            if (!currentWeather.equals(Weather.LOW_TIDE)) {
                plant.act(newPlants);
            }
        }

        // Add the newly born animals to the main list.
        animals.addAll(newAnimals);
        // Add the newly grown plants to the main list.
        plants.addAll(newPlants);

        view.showStatus(step, field); 
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with animals and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location);
                    animals.add(shark);
                }
                else if(rand.nextDouble() <= PLANKTON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plankton plankton = new Plankton(true, field, location);
                    animals.add(plankton);
                }else if(rand.nextDouble() <= TUNA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Tuna tuna = new Tuna(true, field, location);
                    animals.add(tuna);
                }else if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Whale whale = new Whale(true, field, location);
                    animals.add(whale);
                }else if(rand.nextDouble() <= SHRIMP_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shrimp shrimp = new Shrimp(true, field, location);
                    animals.add(shrimp);
                }else if(rand.nextDouble() <= SEAWEED_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seaweed seaweed = new Seaweed(field, location);
                    plants.add(seaweed);
                }

                // else leave the location empty.
            }
        }
    }

    /**
     * Pause for a given time.
     * 
     * @param millisec The time to pause for, in milliseconds
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
     * Returns whether it is daytime or not.
     * Note that the first day will last 1 less step than the others.
     * 
     * @return daytime Whether it is daytime or not.
     */
    public boolean isDaytime() {
        if (step % DAY_NIGHT_CYCLE == 0) {
            daytime = !daytime;
        }
        return daytime;
    }
}
