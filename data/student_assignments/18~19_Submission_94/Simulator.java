import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing water, corn, mouse, chicken, cat, snake, eagle.
 *
 * @version 2019.02.21
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 150;
    // The probability that a cat will be created in any given grid position.
    private static final double CAT_CREATION_PROBABILITY = 0.04;
    // The probability that a mouse will be created in any given grid position.
    private static final double MOUSE_CREATION_PROBABILITY = 0.02;
    // The probability that a chicken will be created in any given grid position.
    private static final double CHICKEN_CREATION_PROBABILITY = 0.05;
    // The probability that a snake will be created in any given grid position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.03; 
    // The probability that an eagle will be created in any given grid position.
    private static final double EAGLE_CREATION_PROBABILITY = 0.02;    
    // The probability that corn will be created in any given grid position.
    private static final double CORN_CREATION_PROBABILITY = 0.05 ;    
    // The probability that corn will be created in any given grid position in each step.
    private static final double CORN_CREATION_PROBABILITY_EACH_STEP = 0.025;
    // The probability that water will be created in any given grid position.
    private static final double WATER_CREATION_PROBABILITY = 0.05 ;    
    // The probability that water will be created in any given grid position in each step.
    private static final double WATER_CREATION_PROBABILITY_EACH_STEP = 0.025;
  
    // List of species in the field.
    private List<Species> species;
    
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // The weather of the field.
    private Weather weather;
    // The current weather of the field.
    private String currentWeather;
    // Indicates it is the day or the night.
    private boolean isDay = true;
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
        
        species = new ArrayList<>();
        weather = new Weather();
        field = new Field(depth, width);
        currentWeather = "sunny";

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Mouse.class, Color.ORANGE);
        view.setColor(Chicken.class, Color.RED);
        view.setColor(Cat.class, Color.PINK);
        view.setColor(Snake.class, Color.BLACK);
        view.setColor(Eagle.class, Color.YELLOW);
        view.setColor(Corn.class, Color.GREEN);
        view.setColor(Water.class, Color.BLUE);
        
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
            //delay(60);   // comment this to run more fast.
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each species.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn animals and plants.
        List<Corn> newCorns = new ArrayList<>();       
        List<Animal> newAnimals = new ArrayList<>();     
        List<Water> newWater = new ArrayList<>();  
        // Let all animals and plants act.
        if (step % 5 == 0)
        {
            isDay = !isDay;
        }
        if (step % 10 == 0)
        {
            currentWeather = weather.getRandomWeather();
        }
        
        for(Iterator<Species> it = species.iterator(); it.hasNext(); ) {
            Species species = it.next();
            Animal animal = null;
            
            if (species instanceof Animal) {
                animal = (Animal) species;

                if (!animal.isAlive()) {
                    it.remove();

                } 
                else {
                    animal.act(newAnimals, isDay, currentWeather);
                }

            }
            if (species instanceof Corn) {
                Corn corn = (Corn) species;
                if (!corn.isAlive()) {
                    it.remove();

                } 
                else {
                    corn.act(newCorns, currentWeather);
                }
            }
            if (species instanceof Water) {
                Water water = (Water) species;
                if (!water.isAlive()) {
                    it.remove();

                } 
                else {
                    water.act(newWater);
                }
            }
        }
               
        // Add the newly born animals and plants to the main lists.
        this.createCorn();
        this.createWater();
        species.addAll(newAnimals);
        species.addAll(newCorns);
        species.addAll(newWater);
        view.showStatus(step,field,currentWeather,isDay);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        species.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step,field,currentWeather,isDay);
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
                if(rand.nextDouble() <= CAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cat cat = new Cat(true, field, location,Randomizer.getRandomGender(),Randomizer.getRandomIsSick());
                    species.add(cat);
                }
                else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Mouse mouse = new Mouse(true, field, location,Randomizer.getRandomGender(),Randomizer.getRandomIsSick());
                    species.add(mouse);
                }
                
                else if(rand.nextDouble() <= CHICKEN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Chicken chicken = new Chicken(true, field, location,Randomizer.getRandomGender(),Randomizer.getRandomIsSick());
                    species.add(chicken);
                }
                
                else if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Snake snake = new Snake(true, field, location,Randomizer.getRandomGender(),Randomizer.getRandomIsSick());
                    species.add(snake);
                }
                else if(rand.nextDouble() <= EAGLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Eagle eagle = new Eagle(true, field, location,Randomizer.getRandomGender(),Randomizer.getRandomIsSick());
                    species.add(eagle);
                }
                else if(rand.nextDouble() <= CORN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Corn corn= new Corn( field, location);
                    species.add(corn);
                }
                else if(rand.nextDouble() <= WATER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Water water= new Water( field, location);
                    species.add(water);
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
     * Create corn randomly in the simulation.
     */
    private void createCorn() 
    {
        Random rand = Randomizer.getRandom();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Species species = null;
                if (rand.nextDouble() <= CORN_CREATION_PROBABILITY_EACH_STEP) {

                    species = new Corn(field, location);
                    this.species.add(species);
                }
            }
        }
    }
    
    /**
     * Create water randomly in the simulation.
     */
    private void createWater() 
    {
        Random rand = Randomizer.getRandom();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Species species = null;
                if (rand.nextDouble() <= WATER_CREATION_PROBABILITY_EACH_STEP) {

                    species = new Water(field, location);
                    this.species.add(species);
                }
            }
        }
    }
}
