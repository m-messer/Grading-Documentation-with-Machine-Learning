import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing fish and sharks.
 *
 * @version 21.02.2020 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 150;
    
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 100;
    
    // The probability that a shark will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.02;
    
    // The probability that a fish will be created in any given grid position.
    private static final double FISH_CREATION_PROBABILITY = 0.1;    

    // The probability that a plankton will be created in any given grid position.
    private static final double PLANKTON_CREATION_PROBABILITY = 0.15;     

    // The probability that a whale will be created in any given grid position.
    private static final double WHALE_CREATION_PROBABILITY = 0.05; 

    // The probability that a shrimp will be created in any given grid position.
    private static final double SHRIMP_CREATION_PROBABILITY = 0.12;     

    // The probability that a shrimp will be created in any given grid position.
    private static final double SEAWEED_CREATION_PROBABILITY = 0.1;

    // List of animals in the field.
    private List<Animal> animals;

    // List of plants in the field.
    private List<Plant> plants;

    //A varieble which stops the game 
    private boolean stop;
    // The current state of the field.
    private Field field;

    // The current step of the simulation.
    private int step;

    // A graphical view of the simulation.
    private SimulatorView view;

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
        stop = false;
        animals = new ArrayList<>(); 
        plants = new ArrayList<>();
        field = new Field(depth, width);
        // Create a view of the state of each location in the field. 

        view = new SimulatorView(depth, width, this);
        view.setColor(Fish.class, new Color(255,165,0,255)); //set the colour of fish to orange
        view.setColor(Shark.class, new Color(0,0,255,255));  //set the colour of shark to blue
        view.setColor(Shrimp.class, new Color(255,0,0,255));  //set the colour of shrimp to red
        view.setColor(Whale.class, new Color(255,192,203,255)); //set the colour of whale to pink
        view.setColor(Plankton.class, new Color(255,255,0,255)); //set the colour of plankton to yellow 
        view.setColor(SeaWeed.class, new Color(0,255,0,255)); //set the colour of sea weed to green

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (2000 steps).
     */
    public void runLongSimulation()
    {
        simulate(2000);     // max step is 2000
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
            // delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * shark and fish.
     */
    public void simulateOneStep()
    {
        // its a if statment which stops the game 
        if(!stop){   
            step++;
            // Provide space for newborn animals.
            List<Animal> newAnimals = new ArrayList<>(); 

            // Provide space for newborn plants.
            List<Plant> newPlants = new ArrayList<>();

            // Let all animals act.
            for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                Animal animal = it.next();
                animal.act(newAnimals);
                if(! animal.isAlive()) {
                    animal.setDead();

                }
            }
            // Let all plants act.
            for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
                Plant plant = it.next();
                plant.act(newPlants);
                if(! plant.isAlive()) {
                    it.remove();
                }
            }

            // Add the newly born animals and plants to the main lists.
            animals.addAll(newAnimals);
            plants.addAll(newPlants);

            //returns the step and field to the SimulatorView Class
            view.showStatus(step, field);
        }
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
        //returns the step and field to the SimulatorView Class
        view.showStatus(step, field);

    }

    /**
     * Randomly populate the field with all animals and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                //populates the field with sharks
                if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location);
                    animals.add(shark);
                }
                //populates the field with fish
                else if(rand.nextDouble() <= FISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fish fish = new Fish(true, field, location);
                    animals.add(fish);
                }
                //populates the field with palnktons
                else if(rand.nextDouble() <= PLANKTON_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Plankton plankton = new Plankton(true, field, location);
                    animals.add(plankton);
                }
                //populates the field with whales
                else if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Whale whale = new Whale(true, field, location);
                    animals.add(whale);
                }
                //populates the field with shrimps
                else if(rand.nextDouble() <= SHRIMP_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Shrimp shrimp = new Shrimp(true, field, location);
                    animals.add(shrimp);
                }
                //populates the field with seaweed
                else if(rand.nextDouble() <= SEAWEED_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    SeaWeed seaWeed = new SeaWeed(true, field, location);
                    plants.add(seaWeed);
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
     * By calling this method, the simulation stops
     */
    public void stop()
    {
        stop = true;
    }
}
