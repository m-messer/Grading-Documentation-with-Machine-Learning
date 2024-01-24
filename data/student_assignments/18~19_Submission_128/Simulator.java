import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.HashMap ; 
/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing owls, lizards, snakes, worms, roosters, hens and chards(a plant).
 *
 * @version 2019.02.22
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a owl will be created in any given grid position.
    private static final double OWL_CREATION_PROBABILITY = 0.08;
    // The probability that a worm will be created in any given grid position.
    private static final double WORM_CREATION_PROBABILITY = 0.40;    
    // The probability of a lizard being created
    private static final double LIZARD_CREATION_PROBABILITY = 0.06;
    //The probability of a snake being created
    private static final double SNAKE_CREATION_PROBABILITY = 0.06;
    // the probability of hen being created
    private static final double HEN_CREATION_PROBABILITY = 0.05; 
    //the probability of a rooster being created
    private static final double ROOSTER_CREATION_PROBABILITY = 0.10; 
    //the probability of creation of plant
    private static final double CHARD_CREATION_PROBABILITY = 0.10;
    // Random number generator
    protected static final Random rand = Randomizer.getRandom();
    
    //stores the current weather
    private static String weather; 
    
    // List of organisms in the field.
    private List<Organism> organisms;
    // Hash map to store the weather and the probability of it
    private HashMap<String, Double> weatherConditions; 
    // A graphical view of the simulation.
    private List<SimulatorView> views;
    
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    //Records the time of the day
    private int timer;
    

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * Create the weather conditions.
     * Determines the colours of the organisms on the GUI and graph.
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
        weatherConditions = new HashMap<>(); 
        field = new Field(depth, width);
        views = new ArrayList<>();

        //The initial weather
        weather = "Foggy" ; 
        
        SimulatorView view = new GridView(depth, width);
        view.setColor(Worm.class, Color.ORANGE);
        view.setColor(Owl.class, Color.BLUE);
        view.setColor(Lizard.class, Color.CYAN);
        view.setColor(Snake.class, Color.YELLOW); 
        view.setColor(Hen.class, Color.MAGENTA); 
        view.setColor(Rooster.class, Color.GRAY); 
        view.setColor(Chard.class, Color.GREEN); 
        views.add(view);

        //for testing the TextView class (can be uncommented)
        view = new TextView();
        views.add(view);

        //This sets the colours for the organisms in the graph view
        view = new GraphView(500, 150, 500);
        view.setColor(Worm.class, Color.ORANGE);
        view.setColor(Owl.class, Color.BLUE);
        view.setColor(Lizard.class, Color.CYAN);
        view.setColor(Snake.class, Color.YELLOW); 
        view.setColor(Hen.class, Color.MAGENTA); 
        view.setColor(Rooster.class, Color.GRAY); 
        view.setColor(Chard.class, Color.GREEN);
        views.add(view);

        //adding the weather values to the hashmap
        weatherConditions.put("Sunny", 0.50); 
        weatherConditions.put("Rainy", 0.30); 
        weatherConditions.put("Foggy", 0.15); 
        weatherConditions.put("Snowy", 0.05); 
        
        
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
        for(int step = 1; step <= numSteps && views.get(0).isViable(field); step++) {
            simulateOneStep();
            //delay(150);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each organism. 
     */
    public void simulateOneStep()
    {
        step++;

        //Padding the step numbers with 0 (step numbers less than 10)
        String formattedStep = String.format("%02d", step);  
        //first digit of the step number
        int firstDigit = Integer.parseInt(formattedStep.substring(0, 1));   

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();        
        // List<Chard> newPlants = new ArrayList<>(); 
        if(rand.nextDouble()>=weatherConditions.get("Sunny")){    //0.50 probability for sunny
            weather = "Sunny" ; 
            Chard.changeGrowthRate(10); 
        }
        else if(rand.nextDouble()>=weatherConditions.get("Rainy")){   //0.20 probability for rain 
            weather = "Rainy" ; 
            Chard.changeGrowthRate(2);
        }  
        else if(rand.nextDouble()>=weatherConditions.get("Foggy")){   //0.15 probability for foggy 
            weather = "Foggy" ; 
            Chard.changeGrowthRate(1);
        }
        //0.15 probability for snowy 
        else if(rand.nextDouble()>weatherConditions.get("Snowy") || rand.nextDouble()<=weatherConditions.get("Snowy")) { 
            weather = "Snowy" ; 
            Chard.changeGrowthRate(0); 
        }
        
        //This is a boolean for the time of the day
        boolean morning = false;  
        
        //This checks if the time of the day is morning or not
        //A day is 150 steps, 0-75 is morning and 75-150 is night.
        if(timer <= 75 )
        {
            morning = true; 
        }
        else if(timer>75 && timer<=150)
        {
            morning = false; 
        }
        else
        {
            timer = 0 ; //resets the timer if the day is over
        }

        // Let all organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext();) 
        {
            Organism organism = it.next();
            //if it's morning and organism is an owl then put them to sleep
            if(organism.getClass() == Owl.class && morning)
            {
                //Let the owls sleep
                organism.sleep();
            }
            else {
                organism.act(newOrganisms);
            }

            if(!organism.isAlive()) 
            {
                it.remove(); 
            }

        }

        // Add the newly born organisms to the main lists.
        organisms.addAll(newOrganisms);
        timer++; 
        updateViews();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        for (SimulatorView view : views) {
            view.reset();
        }

        populate();
        updateViews();
    }

    /**
     * Update all existing views.
     */
    private void updateViews()
    {
        for (SimulatorView view : views) {
            view.showStatus(step, field);
        }
    }

    /**
     * Randomly populate the field with owls, lizards
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= OWL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Owl owl = new Owl(true, field, location);
                    organisms.add(owl);
                }
                else if(rand.nextDouble() <= WORM_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Worm worm = new Worm(true, field, location);
                    organisms.add(worm);
                }
                else if(rand.nextDouble() <= LIZARD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lizard lizard = new Lizard(true, field, location);
                    organisms.add(lizard);
                }
                else if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Snake snake = new Snake(true, field, location);
                    organisms.add(snake);
                }
                else if(rand.nextDouble() <= HEN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hen hen = new Hen(true, field, location);
                    organisms.add(hen);
                }
                else if(rand.nextDouble() <= ROOSTER_CREATION_PROBABILITY){
                    Location location = new Location(row, col) ; 
                    Rooster rooster = new Rooster(true, field, location);
                    organisms.add(rooster); 
                }
                else if(rand.nextDouble() <= CHARD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Chard chard = new Chard(true, field, location);
                    organisms.add(chard);
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
     * This is static method as it's being accessed by a method in GridView class
     * @return the value stored in weather
     */
    public static String getWeather(){
        return weather ; 
    }
}
