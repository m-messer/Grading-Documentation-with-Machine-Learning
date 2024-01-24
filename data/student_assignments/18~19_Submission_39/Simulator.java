import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing piranhas, farlowellas, cockatoos, seahorses, mollys and fisherman.
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
    // The probability that a piranha will be created in any given grid position.
    private static final double PIRANHA_CREATION_PROBABILITY = 0.08;
    // The probability that a molly will be created in any given grid position.
    private static final double MOLLY_CREATION_PROBABILITY = 0.08;    
    // The probability that a farlowella will be created in any given grid position.
    private static final double FARLOWELLA_CREATION_PROBABILITY =0.06;
    // The probability that a cockatoo will be created in any given grid position.
    private static final double COCKATOO_CREATION_PROBABILITY =0.04;
    // The probability that a moss will be created in any given grid position.
    private static final double MOSS_CREATION_PROBABILITY =0.22;
    // The probability that a seahorse will be created in any given grid position.
    private static final double SEAHORSE_CREATION_PROBABILITY = 0.03;
    // The probability that a fisherman will be created in any given grid position.
     private static final double FISHERMAN_CREATION_PROBABILITY= 0.02;
    // List of aquatics in the field.
    private List<Aquatic> aquatics;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // 
    private Integer day;
    //
    private boolean dayTime;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
        dayTime = true;
        day = 10;
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
        
        aquatics = new ArrayList<>();
        field = new Field(depth, width);
        
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Molly.class, Color.CYAN);
        view.setColor(Piranha.class, Color.BLUE);
        view.setColor(Farlowella.class, Color.PINK);
        view.setColor(Cockatoo.class, Color.YELLOW);
        view.setColor(Seahorse.class, Color.RED);
        view.setColor(Moss.class, Color.GREEN);
        view.setColor(Fisherman.class, Color.BLACK);
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
         //delay(90);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * piranha, farlowella, cockatoo, seahorse, molly and fisherman.
     */
    public void simulateOneStep()
    {
        step++;
        field.bermudaTriangle(view);
        delay(100);
     
        // Provide space for newborn animals.
        List<Aquatic> newAquatic = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<Aquatic> it = aquatics.iterator(); it.hasNext(); ) 
        {
         Aquatic aquatic = it.next();
         if (dayTime() && animalCheck(aquatic)) 
         {
          continue;
         }
         aquatic.act (newAquatic);
          if(! aquatic.isAlive()) 
          {
           it.remove();
         }
        }     
        // Add the newly born foxes and rabbits to the main lists.
        aquatics.addAll(newAquatic);
        field.bermudaTriangle(view);
        view.showStatus(step, field);
    } 
      
    /**
     * Indicates the day and night time periods.
     */
    private boolean dayTime()
    {
        if ((step % day) == 0){
            dayTime = !dayTime; //switch between day and night
        }
        return dayTime;
    }
    
    /**
     * Checks whether the aquatic is animal or not.
     */
    private boolean animalCheck (Aquatic aquatic)
    {
     return aquatic instanceof Molly || aquatic instanceof Piranha || aquatic instanceof Cockatoo || aquatic instanceof Farlowella;
    }  
    
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        aquatics.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with piranhas, farlowellas, cockatoos, seahorses, mollys and fisherman.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) 
        {
            for(int col = 0; col < field.getWidth(); col++) 
            {
                field.bermudaTriangle(view);
                if(rand.nextDouble() <= PIRANHA_CREATION_PROBABILITY) 
            {
                Location location = new Location(row, col);
                Piranha piranha = new Piranha(true, field, location);
                aquatics.add(piranha);
            }
                else if(rand.nextDouble() <= MOLLY_CREATION_PROBABILITY) 
            {
                Location location = new Location(row, col);
                Molly molly = new Molly(true, field, location);
                aquatics.add(molly);
            }
                else if(rand.nextDouble() <= FARLOWELLA_CREATION_PROBABILITY) 
            {
                Location location = new Location(row, col);
                Farlowella farlowella = new Farlowella(true, field, location);
                aquatics.add(farlowella);
            }   
                else if(rand.nextDouble() <= COCKATOO_CREATION_PROBABILITY) 
            {
                Location location = new Location(row, col);
                Cockatoo cockatoo = new Cockatoo(true, field, location);
                aquatics.add(cockatoo);
            }
                else if(rand.nextDouble() <= MOSS_CREATION_PROBABILITY) 
            {
                Location location = new Location(row, col);
                Moss moss = new Moss(true, field, location);
                aquatics.add(moss);
            }
                else if(rand.nextDouble() <= SEAHORSE_CREATION_PROBABILITY) 
            {
                Location location = new Location(row, col);
                Seahorse seahorse = new Seahorse(true, field, location);
                aquatics.add(seahorse);
            }
               else if(rand.nextDouble() <= FISHERMAN_CREATION_PROBABILITY) 
            {
                Location location = new Location(row, col);
                Fisherman fisherman = new Fisherman( field, location);
                aquatics.add(fisherman);
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
