import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing dinosaurs and plant life.
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

    // The probability that a raptor will be created in any given grid position.
    private static final double RAPTOR_CREATION_PROBABILITY = 0.12;
    // The probability that a T-Rex will be created in any given grid position.
    private static final double TREX_CREATION_PROBABILITY = 0.08;

    // The probability that an albertadromeus will be created in any given grid position.
    private static final double ALBERTADROMEUS_CREATION_PROBABILITY = 0.16;
    // The probability that a triceratops will be created in any given grid position.
    private static final double TRICERATOPS_CREATION_PROBABILITY = 0.12;

    // The probability that a cycad will be created in any given grid position.
    private static final double CYCAD_CREATION_PROBABILITY = 0.18;

    // List of organisms in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int day;
    // The state of the day in the simulation (when true, this signifies the afternoon 12:00 and false represents the night 00:00
    private boolean stateDay = true;
    // A graphical view of the simulation.
    private SimulatorView view;

    //Colours for simulation GUI
        /*
            This code was adapted from an example taken from
            https://teaching.csse.uwa.edu.au/units/CITS1001/colorinfo.html
         */
    public static final Color LIGHT_RED = new Color(255, 51, 51);
    public static final Color DARK_RED = new Color(204, 0, 0);
    public static final Color VERY_DARK_RED = new Color(153, 0, 0);

    public static final Color VERY_LIGHT_BLUE = new Color(51, 204, 255);
    public static final Color LIGHT_BLUE = new Color(51, 153, 255);
    public static final Color BLUE = new Color(0, 0, 255);

    public static final Color GREEN = new Color(0, 204, 0);
    public static final Color DARK_GREEN = new Color(0, 153, 0);


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

        view.setColor(Raptor.class, LIGHT_RED);
        view.setColor(TRex.class, VERY_DARK_RED);

        view.setColor(Albertadromeus.class, VERY_LIGHT_BLUE);
        view.setColor(Triceratops.class, BLUE);

        view.setColor(Cycad.class, DARK_GREEN);

        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a year,
     * (365 days).
     */
    public void runOneYear()
    {
        simulate(365);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numDays The number of steps to run for.
     */
    public void simulate(int numDays)
    {
        for(int day = 1; day <= numDays*2 && view.isViable(field); day++) {
            simulate12Hours();
            delay(30);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * dinosaur and plant.
     */
    public void simulate12Hours()
    {
        //below is if statements for the afternoon and night cycle in the day
        if (stateDay){
            stateDay = false;
            day++;
        }
        else{
            stateDay = true;
        }     
        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();
        // Let all organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms);
            if(! organism.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born dinosaurs and plants to the main lists.
        organisms.addAll(newOrganisms);

        view.showStatus(day, time(), field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        day = -1;
        organisms.clear();
        populate();
        simulate12Hours();
        simulate12Hours();
        // Show the starting state in the view.
        view.showStatus(day, time(), field);
    }
    
    /**
     * Randomly populate the field with dinosaurs and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= CYCAD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cycad cycad = new Cycad(true, field, location);
                    organisms.add(cycad);
                }
                else if(rand.nextDouble() <= TRICERATOPS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Triceratops triceratops = new Triceratops(true, field, location);
                    organisms.add(triceratops);
                }
                else if(rand.nextDouble() <= ALBERTADROMEUS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Albertadromeus albertadromeus = new Albertadromeus(true, field, location);
                    organisms.add(albertadromeus);
                }
                else if(rand.nextDouble() <= TREX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    TRex trex = new TRex(true, field, location);
                    organisms.add(trex);
                }
                
                else if(rand.nextDouble() <= RAPTOR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Raptor raptor = new Raptor(true, field, location);
                    organisms.add(raptor);
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
    
    private String time()
    {
    if (stateDay){
        return "   12:00 / Day";
    }
    else
    {
        return "   00:00 / Night";
    }
    }
}

