import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2020.02.20 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a bat will be created in any given grid position.
    private static final double BAT_CREATION_PROBABILITY = 0.02;
    // The probability that a fly will be created in any given grid position.
    private static final double FLY_CREATION_PROBABILITY = 0.08;    
    // The probability that a cow will be created in any given grid position.
    private static final double COW_CREATION_PROBABILITY = 0.06; 
    // The probability that a frog will be created in any given grid position.
    private static final double FROG_CREATION_PROBABILITY = 0.04; 
    // The probability that a human will be created in any given grid position.
    private static final double HUMAN_CREATION_PROBABILITY = 0.05; 
    // The number of animal species
    private static final int SPECIES = 5;
    // The number of plant species
    private static final int PLANT_SPECIES = 2;
    // The current state of the field.$
    private List<Actor> actors;
    
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    private ClockDisplay clock;
    
    private Counter counter;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_WIDTH, DEFAULT_DEPTH);
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
        
        field = new Field(depth, width);
        
        clock = new ClockDisplay();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Human.class, Color.ORANGE);
        view.setColor(Fly.class, Color.BLUE);
        view.setColor(Frog.class, Color.CYAN);
        view.setColor(Bat.class, Color.BLACK);
        view.setColor(Cow.class, Color.YELLOW);
        view.setColor(Grass.class, Color.GREEN);        
        view.setColor(Flower.class, Color.PINK);
        
        // Display a clock
        view.setClock(clock);
        
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
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each actor.
     */
    public void simulateOneStep()
    {
        step++;
        clock.timeTick(step);
        clock.updateDisplay();
        field.changeWeather();
            
        // Provide space for newborn animals.
        List<Actor> newActors = new ArrayList<>();        
        // Let all actors act.
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            if(! actor.isActive()) {
                it.remove();
            }
            else{
                actor.act(newActors);
            }
        }
               
        // Add the newly born actors to the main lists.
        actors.addAll(newActors);
        view.showStatus(step, field , clock);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        // Show the starting state in the view.
        view.showStatus(step, field, clock);
    }
    
    /**
     * Randomly populate the field with actors.
     */
    private void populate()
    {
        actors = new ArrayList<Actor>();
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                //Class<? extends Animal> species = SPECIES.get(speciesIndex);
                Animal animal; //= new species(true, field, location, gender);
                if (rand.nextDouble() < 0.5){
                    //if random is between 0 and 0.5 then generate an animal
                    //else generate a new plant
                    int species = rand.nextInt(SPECIES);
                    
                    switch(species){
                        case 0:
                            animal = new Bat(true, field, location );
                            break;
                        case 1:
                            animal = new Fly(true, field, location);
                            break;
                        case 2:
                            animal = new Cow(true, field, location);
                            break;
                        case 3:
                            animal = new Frog(true, field, location);
                            break;
                        default:
                            animal = new Human(true, field, location);
                   }
                   actors.add(animal);
                }
                else{
                    //random in range 2
                    int plantSpecies= rand.nextInt(PLANT_SPECIES);
                    
                    Plant plant;
                    
                    switch(plantSpecies){
                        case 0:
                            plant = new Grass(true, location, field);
                            break;
                        default:
                            plant = new Flower(true, location, field); 
                   }
                   actors.add(plant);
                   // else leave the location empty.
              }
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
