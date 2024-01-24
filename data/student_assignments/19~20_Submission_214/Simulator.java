import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing plants and animals.
 *
 * @version 2020.2.22
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a snak will be created in any given grid position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.07;
    // The probability that a vole will be created in any given grid position.
    private static final double VOLE_CREATION_PROBABILITY = 0.15;
    // The probability that a rice will be created in any given grid position.
    private static final double RICE_CREATION_PROBABILITY = 0.40;
    // The probability that a locust will be created in any given grid position.
    private static final double LOCUST_CREATION_PROBABILITY = 0.12;
    // The probability that a frog will be created in any given grid position.
    private static final double FROG_CREATION_PROBABILITY = 0.09;
    // The probability that a weasel will be created in any given grid position.
    private static final double WEASEL_CREATION_PROBABILITY = 0.05;

    // List of animals in the field.
    private List<Animal> animals;
    //List of plants in the field.
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // The current weather of the simulation.
    private Weather weather;
    
    private Time time;
    
    
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
        view.setColor(Vole.class, Color.ORANGE);
        view.setColor(Snake.class, Color.BLUE);
        view.setColor(Locust.class, Color.RED);
        view.setColor(Rice.class,Color.GREEN);
        view.setColor(Frog.class,Color.YELLOW);
        view.setColor(Weasel.class,Color.MAGENTA);
        
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
        boolean clock;
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
           if(step%20 < 10){
             clock = true;
             simulateOneStep(clock);
            }
           else{
             clock = false;
             simulateOneStep(clock);
           }
            
           if(step%5 == 0){
                weather.nextWeather();
           }
        
           delay(100);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * actor.
     */
    public void simulateOneStep(boolean clock)
    {
        step++;
        if(clock == true){
            time.day(animals);
            view.changeToDay();
        }else if (clock == false){
            time.night(animals);
            view.changeToNight();
        }
        //Judging plant action based on weather
        weather.Rain(plants,view);
        
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
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clearAll();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Snake snake = new Snake(true, field, location);
                    animals.add(snake);
                }
                else if(rand.nextDouble() <= VOLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Vole vole = new Vole(true, field, location);
                    animals.add(vole);
                }
                else if(rand.nextDouble() <= RICE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rice rice = new Rice(true, field, location);
                    plants.add(rice);
                }
                else if(rand.nextDouble() <= LOCUST_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Locust locust = new Locust(true, field, location);
                    animals.add(locust);
                }
                else if(rand.nextDouble() <= FROG_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Frog frog = new Frog(true, field, location);
                    animals.add(frog);
                }
                else if(rand.nextDouble() <= WEASEL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Weasel weasel = new Weasel(true, field, location);
                    animals.add(weasel);
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
