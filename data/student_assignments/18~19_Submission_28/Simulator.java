import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing various animals, plants, land and water tiles, and weather elements.
 *
 * @version 22/02/2019
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a wolf will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.1;
    // The probability that a squirrel will be created in any given grid position.
    private static final double SQUIRREL_CREATION_PROBABILITY = 0.2;    
    // The probability that a sheep will be created in any given grid position.
    private static final double SHEEP_CREATION_PROBABILITY = 0.18;  
    // The probability that an eagle will be created in any given grid position.
    private static final double EAGLE_CREATION_PROBABILITY = 0.07;
    // The probability that an kingfisher will be created in any given grid position.
    private static final double KINGFISHER_CREATION_PROBABILITY = 0.1;
    // The probability that a salmon will be created in any given grid position.
    private static final double SALMON_CREATION_PROBABILITY = 0.3;   
    // The probability that a cloud will be created at any given step.
    private static final double CLOUD_CREATION_PROBABILITY = 0.01;
    // The probability that a plant is created at every step.
    private static final double PLANT_CREATION_PROBABILITY = 0.02;

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
    // List of clouds in the field.
    private List<Weather> clouds;
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
        
        // Initialize arraylists for animals, plants, and clouds.
        animals = new ArrayList<>();
        plants = new ArrayList<>();
        clouds = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        
        // Set colours for each entity.
        view.setColor(Squirrel.class, Color.ORANGE);
        view.setColor(Wolf.class, Color.BLACK);
        view.setColor(Sheep.class, Color.WHITE);
        view.setColor(Eagle.class, Color.GRAY);
        view.setColor(Kingfisher.class, Color.YELLOW);
        view.setColor(Salmon.class, Color.MAGENTA);
        
        view.setColor(Plant.class, Color.PINK);
        
        view.setColor(Fog.class, new Color(255, 255, 153, 150));
        view.setColor(Rain.class, new Color(0, 0, 153, 100));
        view.setColor(Snow.class, new Color(255, 255, 255, 150));
        
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
            //delay(100);   
            // uncomment this to run more slowly
        }
    }
    
    // Method that generates plants throughout the field. Is affected by rain, making the birth chance slightly higher.
    private void generatePlants() 
    {   
        Random rand = new Random();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(field.getObjectAt(row, col) == null && rand.nextDouble() <= PLANT_CREATION_PROBABILITY*0.05+0.005*field.getWeather(row, col, 1)) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(field, location);
                    plants.add(plant);
                }
            }
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal or other.
     */
    public void simulateOneStep()
    {
        step++;
        generatePlants();
        Time.setTime(step);
        // Random chance for clouds to form.
        Random rand = new Random();
        
        // Random chance to create a cloud.
        if (CLOUD_CREATION_PROBABILITY >= rand.nextDouble())
        {
            int chance = rand.nextInt(3);
            if (chance == 0)
            clouds.add(new Fog(rand.nextInt(field.getDepth()), rand.nextInt(field.getWidth()), rand.nextInt(8)+3, rand.nextInt(50)+25, field));
            if (chance == 1)
            clouds.add(new Rain(rand.nextInt(field.getDepth()), rand.nextInt(field.getWidth()), rand.nextInt(8)+3, rand.nextInt(50)+25, field));
            if (chance == 2)
            clouds.add(new Snow(rand.nextInt(field.getDepth()), rand.nextInt(field.getWidth()), rand.nextInt(8)+3, rand.nextInt(50)+50, field));
        }
        // Process the clouds.
        for(Iterator<Weather> it = clouds.iterator(); it.hasNext(); ) {
            Weather cloud = it.next();
            cloud.move();
            if (!cloud.exists())
                it.remove();
        }
        // Creates new plants.
        List<Plant> newPlants = new ArrayList<>();
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.grow();
            if(!plant.isAlive()) {
                it.remove();
            }
        }
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            // Toggles awake or sleep status.
            animal.setAsleep();
            if(animal.getAsleep()){
                continue;
            }
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born wolves and squirrels to the main lists.
        animals.addAll(newAnimals);
        
        // Draw elements.
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        clouds.clear();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with wolves, squirrels, sheep, eagles, and water tiles.
     */
    private void populate()
    {
        // Creating a river.
        Random rand = Randomizer.getRandom();
        field.clear();
        int width = field.getWidth();
        int rwidth = field.getWidth()/3; // Width of river;
        int river = width/2 - rwidth/2; // Start point of river.
        int direction = 0; // Stores information about the direction the last row of water 
                           // to create a smoother curvature in the river
        
        // Setting all tiles as land.
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                field.setLand(row, col, true);
            }
        }
        
        // Gow down row by row filling in tiles with water information
        for(int row = 0; row < field.getDepth(); row++) {
            int startriver = 0 > river? 0 : river; // Starting point of setting up the river on specific row
            int endriver = (river + rwidth) < width? river + rwidth : width; // Ending point...
            for(int col = startriver; col < endriver; col++) {
                field.setLand(row, col, false);
            }
            if (direction == 0) // Moved straight down.
            {
                direction = rand.nextInt(3) - 1;
            }else if (direction == 1) // Moved slightly right.
            {
                direction = rand.nextInt(3);
            }else if (direction == 2) // Moved hard right.
            {
                direction = rand.nextInt(2) + 1;
            }else if (direction == -1) // Moved slightly left.
            {
                direction = rand.nextInt(3) - 2;
            }else if (direction == -2)// Moved hard left.
            {
                direction = rand.nextInt(2) - 2;
            }
            river += direction;
        }
        
        // Adds plants and animals to field
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY && field.getLand(row, col)) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(field, location);
                    plants.add(plant);
                }
                else if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY && field.getLand(row, col)) {
                    Location location = new Location(row, col);
                    boolean randomGender = (rand.nextDouble()>0.5) ? true : false;
                    Wolf wolf = new Wolf(field, location, randomGender, true);
                    animals.add(wolf);
                }
                else if(rand.nextDouble() <= SQUIRREL_CREATION_PROBABILITY && field.getLand(row, col)) {
                    Location location = new Location(row, col);
                    boolean randomGender = (rand.nextDouble()>0.7) ? true : false;
                    Squirrel squirrel = new Squirrel(field, location, randomGender, true);
                    animals.add(squirrel);
                }
                else if(rand.nextDouble() <= SHEEP_CREATION_PROBABILITY && field.getLand(row, col)) {
                    Location location = new Location(row, col);
                    boolean randomGender = (rand.nextDouble()>0.7) ? true : false;
                    Sheep sheep = new Sheep(field, location, randomGender, true);
                    animals.add(sheep);
                }
                else if(rand.nextDouble() <= EAGLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean randomGender = (rand.nextDouble()>0.5) ? true : false;
                    Eagle eagle = new Eagle(field, location, randomGender, true);
                    animals.add(eagle);
                }
                else if(rand.nextDouble() <= KINGFISHER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean randomGender = (rand.nextDouble()>0.5) ? true : false;
                    Kingfisher kingfisher = new Kingfisher(field, location, randomGender, true);
                    animals.add(kingfisher);
                }
                else if(rand.nextDouble() <= SALMON_CREATION_PROBABILITY && !field.getLand(row, col)) {
                    Location location = new Location(row, col);
                    boolean randomGender = (rand.nextDouble()>0.7) ? true : false;
                    Salmon salmon = new Salmon(field, location, randomGender, true);
                    animals.add(salmon);
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
