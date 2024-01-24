import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing gazelles, cheetahs, lions, leopards, pandas, bamboos
 * and grass.
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
    // The probability that a cheetah will be created in any given grid position.
    private static final double CHEETAH_CREATION_PROBABILITY = 0.005;
    // The probability that a gazelle will be created in any given grid position.
    private static final double GAZELLE_CREATION_PROBABILITY = 0.31;
    // The probability that a panda will be created in any given grid position.
    private static final double PANDA_CREATION_PROBABILITY = 0.31;    
    // The probability that a leopard will be created in any given grid position.
    private static final double LEOPARD_CREATION_PROBABILITY = 0.005;  
    // The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.006;    
    // The probability that a bamboo will be created in any given grid position.
    private static final double BAMBOO_CREATION_PROBABILITY = 0.04;  
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.03;  

    // List of animals in the field.
    private List<Animal> animals;
    // List of animals in the field.
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // A variable to show duration of the day.
    private int DAY_LENGTH = 4;

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
        view.setColor(Gazelle.class, Color.MAGENTA);
        view.setColor(Cheetah.class, Color.ORANGE);
        view.setColor(Panda.class, Color.CYAN);
        view.setColor(Leopard.class, Color.BLUE);
        view.setColor(Lion.class, Color.RED);
        view.setColor(Bamboo.class, Color.YELLOW);
        view.setColor(Grass.class, Color.GREEN);
        
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (3000 steps).
     */
    public void runLongSimulation()
    {
        simulate(3000);
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
     * predator and prey.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();   
        List<Plant> newPlants = new ArrayList<>();  
        // Let all preys act.
        
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(newPlants);
            if(! plant.isAlive()) {
                it.remove();
            }
        }
        if(step%(2*DAY_LENGTH) <= DAY_LENGTH) {
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act1(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        } else{
               for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                Animal animal = it.next();
                animal.act2(newAnimals);
                if(! animal.isAlive()) {
                it.remove();
                }
        }
        }
               
        // Add the newly born predators and preys to the main lists.
        animals.addAll(newAnimals);
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
     * Randomly populate the field with cheetahs and gazelles.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                 if(rand.nextDouble() <= BAMBOO_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bamboo bamboo = new Bamboo(true, field, location);
                    plants.add(bamboo);
                }
                 else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    plants.add(grass);
                }
                else if(rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cheetah cheetah = new Cheetah(true, field, location);
                    animals.add(cheetah);
                }
                else if(rand.nextDouble() <= GAZELLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Gazelle gazelle = new Gazelle(true, field, location);
                    animals.add(gazelle);
                }
                else if(rand.nextDouble() <= PANDA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Panda panda = new Panda(true, field, location);
                    animals.add(panda);
                }
                else if(rand.nextDouble() <= LEOPARD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Leopard leopard = new Leopard(true, field, location);
                    animals.add(leopard);
                }
                 else if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    animals.add(lion);
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
