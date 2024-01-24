import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Goats and Dragones.
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
    // The probability that a Dragon will be created in any given grid position.
    private static final double Dragon_CREATION_PROBABILITY = 0.005;
    // The probability that a Tiger will be created in any given grid position. 
    private static final double Tiger_CREATION_PROBABILITY = 0.02;
    // The probability that a Goat will be created in any given grid position.
    private static final double Goat_CREATION_PROBABILITY = 0.04;   
    // The probability that a Boar will be created in any given grid position. 
    private static final double Boar_CREATION_PROBABILITY = 0.04; 
    // The probability that a Deer will be created in any given grid position. 
    private static final double Deer_CREATION_PROBABILITY = 0.04; 
    // The probability that a Grass will be created in any given grid position. 
    private static final double Grass_CREATION_PROBABILITY = 0.2; 

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plants> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The counter of changes of day and night 
    private int changeTime; 
    // The time (hour) in the simulation, so that we can change Day with Night 
    private int hour; 
    // The Daytime (Night or Day) 
    private boolean isDay;
    // The Weather (Rain) 
    private boolean isRain; 
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
        
        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);
        
        // Setting the time. 
        hour = 0; 
        isDay = true;
        changeTime = 1; 
        isRain = false; 

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Goat.class, Color.ORANGE);
        view.setColor(Dragon.class, Color.RED);
        view.setColor(Tiger.class, Color.BLACK);
        view.setColor(Boar.class, Color.BLUE);
        view.setColor(Deer.class, Color.MAGENTA);
        view.setColor(Grass.class, Color.GREEN);
        
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
            // delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * Dragon and Goat.
     */
    public void simulateOneStep()
    {
        step++;
        // Keeping track of time (Day and Night) 
        hour++; 
        if(hour == 12) {
            if(changeTime%2 == 0) {
                isDay = true; 
            } else {
                isDay = false; 
            }
            changeTime += 1;
            hour = 0; 
        }
        // Random rain 
        Random randomNumberWeather = new Random();
        int randomWeather = randomNumberWeather.nextInt(5);
        if(randomWeather < 4) {
            isRain = true;
        } else {
            isRain = false; 
        }
        // Provide space for newborn animals and plants
        List<Animal> newAnimals = new ArrayList<>(); 
        List<Plants> newPlants = new ArrayList<>();
        // Let all animals act and plants
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            if(animal instanceof Dragon) {
                if(isDay == false) {
                    animal.act(newAnimals);
                } else {
                    Dragon Dragon = (Dragon) animal;
                    Dragon.incrementAge();
                }
            } else {
                if(animal instanceof Tiger) {
                   if(isDay == true) {
                      animal.act(newAnimals);
                   } else {
                      Tiger Tiger = (Tiger) animal;
                      Tiger.incrementAge();
                   }  
                } else {
                   animal.act(newAnimals);
                }
            }
            // removes if it is dead. 
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        for(Iterator<Plants> it = plants.iterator(); it.hasNext(); ) {
            Plants plants = it.next();
            if(plants instanceof Grass && isRain == true) {
                plants.act(newPlants);
            } 
            // removes if it is dead. 
            if(! plants.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born Dragones and Goats to the main lists.
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
     * Randomly populate the field with Dragones and Goats and other animals.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= Dragon_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Dragon Dragon = new Dragon(true, field, location);
                    animals.add(Dragon);
                }
                else if(rand.nextDouble() <= Goat_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Goat Goat = new Goat(true, field, location);
                    animals.add(Goat);
                }
                else if(rand.nextDouble() <= Tiger_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Tiger Tiger = new Tiger(true, field, location);
                    animals.add(Tiger);
                }
                else if(rand.nextDouble() <= Boar_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Boar Boar = new Boar(true, field, location);
                    animals.add(Boar);
                }
                else if(rand.nextDouble() <= Deer_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer Deer = new Deer(true, field, location);
                    animals.add(Deer);
                }
                else if(rand.nextDouble() <= Grass_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass Grass = new Grass(true, field, location);
                    plants.add(Grass);
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
