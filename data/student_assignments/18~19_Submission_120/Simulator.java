import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing mice and cats.
 *
 * @version 2016.03.18
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a cat will be created in any given grid position.
    private static final double CAT_CREATION_PROBABILITY = 0.02;
    // The probability that a dog will be created in any given grid position.
    private static final double DOG_CREATION_PROBABILITY = 0.02;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.02;
    // The probability that a mouse will be created in any given grid position.
    private static final double MOUSE_CREATION_PROBABILITY = 0.08;    
    // The probability that a bird will be created in any given grid position.
    private static final double BIRD_CREATION_PROBABILITY = 0.08;    
    // The probability that a worm will be created in any given grid position.
    private static final double WORM_CREATION_PROBABILITY = 0.08;    
    // The probability that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.04;    
    // Night i.e. The number of steps after which the organisms don't move.
    private static final int NIGHT = 1000;

    // List of organisms in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The number of steps to run for.
    private int numSteps;
    // A graphical view of the simulation.
    private List<SimulatorView> views;
    
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

        views = new ArrayList<>();
        
        SimulatorView view = new GridView(depth, width);
        view.setColor(Bird.class, Color.RED);
        view.setColor(Mouse.class, Color.ORANGE);
        view.setColor(Worm.class, Color.YELLOW);
        view.setColor(Cat.class, Color.BLUE);
        view.setColor(Fox.class, Color.PINK);
        view.setColor(Dog.class, Color.MAGENTA);
        view.setColor(Plant.class, Color.GREEN);
        views.add(view);
        
        view = new GraphView(500, 150, 500);
        view.setColor(Bird.class, Color.RED);
        view.setColor(Mouse.class, Color.ORANGE);
        view.setColor(Worm.class, Color.YELLOW);
        view.setColor(Cat.class, Color.BLUE);
        view.setColor(Fox.class, Color.PINK);
        view.setColor(Dog.class, Color.MAGENTA);
        view.setColor(Plant.class, Color.GREEN);
        views.add(view);

        // Setup a valid starting point.
        reset();
    }
    
    /**
     * This method opens the system's default internet browser
     * The Image shows the food chain of all the organisms in this simulation.
     */
    public void viewFoodChain() throws Exception
    {
        URI uri = new URI("https://3.bp.blogspot.com/-EyU79i28mN8/XG8tuEA3nLI/AAAAAAAAUAg/yB93WMe2KYYYIcHC2-T7gZhgFNSu2wlOgCLcBGAs/s1600/Picture1.png");
        java.awt.Desktop.getDesktop().browse(uri); 
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * e.g. (2000 steps).
     */
    public void runLongSimulation()
    {
        try {
            viewFoodChain(); // launch informative image at the start of the simulation
        }
        catch (Exception e) {
            System.out.println("INVALID URL");
        }
        simulate(2000);
        try {
            viewVideo(); // launch funny video at the end of the simulation
        }
        catch (Exception e) {
            System.out.println("INVALID URL");
        }
    }
    
    /**
     * Keeps track of the time of the day. fter half of the given number of steps
     * the simulation changes from day to night.
     * @param numSteps The number of steps to run for.
     */
    public boolean timeOfDay()
    {
        boolean night; // check if it is night or not
        // if the current step is greater than half of the total
        // then it is night. otherwise it is day.
        if (step >= NIGHT) {
            night = true;
        }
        else {
            night = false;
        }
        return night;
    }
    
    /**
     * Return the time of day in the simulation.
     * @return timeOfDay The current time of day.
     */
    public String getTimeOfDay()
    {
        // Get strings to represent what time of day it is
        String timeOfDay = "";
        if (timeOfDay() == true) {
            timeOfDay = "Night";
        }
        else {
            timeOfDay = "Day";
        }
        return timeOfDay;
    }

    /**
     * Return the number of steps in the simulation.
     * @return The number of steps to run for.
     */
    public int getNumSteps()
    {
        return numSteps;
    }
    
    /**
     * Return the current step of the simulation.
     * @return step The current step.
     */
    public int getCurrentStep()
    {
        return step;
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
            delay(10);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * cat and mouse.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();        
        // Let all mice act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            // animals only move when it is day time
            if (timeOfDay() == true) {
                organism.act(newOrganisms);
                if(! organism.isAlive()) {
                    it.remove();
                }
            }
        }
               
        // Add the newly born cats and mice to the main lists.
        organisms.addAll(newOrganisms);

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
     * Randomly populate the field with cats and mice.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= CAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cat cat = new Cat(true, field, location, true);
                    organisms.add(cat);
                }
                else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Mouse mouse = new Mouse(true, field, location, true);
                    organisms.add(mouse);
                }
                else if(rand.nextDouble() <= BIRD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bird bird = new Bird(true, field, location, true);
                    organisms.add(bird);
                }
                else if(rand.nextDouble() <= DOG_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Dog dog = new Dog(true, field, location, true);
                    organisms.add(dog);
                }
                else if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location, true);
                    organisms.add(fox);
                }
                else if(rand.nextDouble() <= WORM_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Worm worm = new Worm(true, field, location, true);
                    organisms.add(worm);
                }
                else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(true, field, location, true);
                    organisms.add(plant);
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
     * This method opens the system's default internet browser
     * The Youtube page shows a funny video about cats and dogs.
     */
    public void viewVideo() throws Exception
    {
        URI uri = new URI("https://www.youtube.com/watch?v=EtH9Yllzjcc");
        java.awt.Desktop.getDesktop().browse(uri); 
    }
}
