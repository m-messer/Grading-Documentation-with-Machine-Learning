import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and Lions.
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
    // The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.01;
    // The probability that a zebra will be created in any given grid position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.25;    
    // The probability that a hyena will be created in any given grid position.
    private static final double HYENA_CREATION_PROBABILITY = 0.02; 
    // The probability that a giraffe will be created in any given grid position.
    private static final double GIRAFFE_CREATION_PROBABILITY = 0.2; 
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.35; 
    // The probability that a tree will be created in any given grid position.
    private static final double TREE_CREATION_PROBABILITY = 0.3; 

    // List of Actors in the field.
    private List<Actor> Actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    //The current time of day and weather
    private EnviromentalFactors enviromentalFactors;
    
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
        if(width <= 0 || depth <= 0) 
        {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        Actors = new ArrayList<>();
        field = new Field(depth, width);
        enviromentalFactors = new EnviromentalFactors();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Zebra.class, Color.BLACK);
        view.setColor(Lion.class, Color.ORANGE);
        view.setColor(Hyena.class, Color.BLUE);
        view.setColor(Giraffe.class, Color.RED);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Tree.class, Color.CYAN);
        
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
        for(int step = 1; step <= numSteps && view.isViable(field); step++)
        {
            simulateOneStep();
            //delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * Lion and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        boolean isNight = enviromentalFactors.getIsNight();
        boolean isRaining = enviromentalFactors.getIsRaining();
        // Provide space for newborn Actors.
        List<Actor> newActors = new ArrayList<>();  
        // Let all rabbits act.
        for(Iterator<Actor> it = Actors.iterator(); it.hasNext(); )
        {
            Actor Actor = it.next();
            if(! Actor.isAlive()) 
            {
                it.remove();
            }
            //check if it is day or nigth for different behaviour      
            if(!isNight)
            {
                Actor.actDay(isRaining);
            }
            else
            {
                Actor.actNight(isRaining);
            }
            newActors.addAll(Actor.getNewActors());
        }
               
        // Add the newly born actors to the main lists.
        Actors.addAll(newActors);
        //increment the hour 
        enviromentalFactors.nextHour();
        //to show information on status bar 
        view.showStatus(step,enviromentalFactors.getTime(), enviromentalFactors.getDay(), field, isNight, isRaining);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        Actors.clear();
        populate();
        boolean isNight = enviromentalFactors.getIsNight();
        boolean isRaining = enviromentalFactors.getIsRaining();
        // Show the starting state in the view.
        view.showStatus(step,enviromentalFactors.getTime(), enviromentalFactors.getDay(), field, isNight, isRaining);
    }
    
    /**
     * Randomly populate the field with actors.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) 
        {
            for(int col = 0; col < field.getWidth(); col++) 
            {
                if(rand.nextDouble() <= LION_CREATION_PROBABILITY)
                {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    Actors.add(lion);
                }
                else if(rand.nextDouble() <= HYENA_CREATION_PROBABILITY)
                {
                    Location location = new Location(row, col);
                    Hyena hyena = new Hyena(true, field, location);
                    Actors.add(hyena);
                }
                else if(rand.nextDouble() <= GIRAFFE_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Giraffe giraffe = new Giraffe(true, field, location);
                    Actors.add(giraffe);
                }
                else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, field, location);
                    Actors.add(zebra);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) 
                {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    Actors.add(grass);
                }
                else if(rand.nextDouble() <= TREE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Tree tree = new Tree(true, field, location);
                    Actors.add(tree);
                }
                else{} //leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try 
        {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) 
        {
            // wake up
        }
    }
}
