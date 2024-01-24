import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals in an arctic climate.
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
    // probabilities of each animal being created. Each must be higher than
    // the last as they represent a range between themselves and the previous one.
    private static final double FOX_CREATION_PROBABILITY  = 0.02;
    private static final double SEAL_CREATION_PROBABILITY  = 0.04;
    private static final double PENGUIN_CREATION_PROBABILITY  = 0.06;
    private static final double FISH_CREATION_PROBABILITY  = 0.08;
    private static final double POLARBEAR_CREATION_PROBABILITY  = 0.1;
    private static final double ALGAE_CREATION_PROBABILITY  = 0.12;
    
    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // A graphical view of the simulation.
    private SimulatorView view;
    // one step in the simulation
    private Step step;
    // the weather per step
    private Weather weather;
    
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
        
        // initialises the objects of the constructor class
        animals = new ArrayList<>();
        field = new Field(depth, width);
        step = new Step(); 
        weather = new Weather();
        
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(PolarBear.class, Color.RED);
        view.setColor(Penguin.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLACK);        
        view.setColor(Seal.class, Color.PINK);
        view.setColor(Fish.class, Color.BLUE);  
        view.setColor(Algae.class, Color.GREEN);
        
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
            //delay(50);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        //set the conditions for the animals to act in.
        step.incrementStep();
        step.setDay();

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        
        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            //change the animal's behaviour depending on the weather
            weatherChange(animal);
            //let the animals act depending on the time of day
            if(step.isDay()) {
               animal.actDay(newAnimals);
            }
            else {
                animal.actNight(newAnimals);
            }

            if(! animal.isAlive()) {
                it.remove();
            }
            
        }


        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);
        weather.generateTemp();
        view.showStatus(step.getStep(), field, weather.getTemp());
        
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step.reset();
        animals.clear();
        populate(step);
        
        // Show the starting state in the view.
        view.showStatus(step.getStep(), field, weather.getTemp());
    }
    
    /**
     * Randomly populate the field with all the animals in the simulation.
     */
    private void populate(Step step)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                double prob = rand.nextDouble();
                
                if (prob <= ALGAE_CREATION_PROBABILITY) {
                   Location location = new Location(row, col); 
                   Animal animal = null;
                   if(prob <= FOX_CREATION_PROBABILITY){
                      animal = new Fox(true, field, location, step);
                   }
                   else if(prob <= SEAL_CREATION_PROBABILITY){
                      animal = new Seal(true, field, location, step);
                   }
                   else if(prob <= PENGUIN_CREATION_PROBABILITY){
                      animal = new Penguin(true, field, location, step);
                   }
                   else if(prob <= FISH_CREATION_PROBABILITY){
                      animal = new Fish(true, field, location, step);
                   }
                   else if(prob <= POLARBEAR_CREATION_PROBABILITY){
                      animal = new PolarBear(true, field, location, step);
                   }
                   else if(prob <= ALGAE_CREATION_PROBABILITY){
                      animal = new Algae(true, field, location, step);
                   }
                   animals.add(animal);
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
     * Checks the current weather and updates each animal's actions accordingly
     */
    private void weatherChange(Animal animal)
    {
       if (weather.globalWarming()) {
           animal.setDead();    
           System.out.println("GLOBAL WARMING KILLED ME!!!!!");
       }
             
       if(weather.isSnowing()){
           if(animal instanceof Algae){
                 Algae algae = (Algae) animal;
                 algae.decreaseGrowth();       
           }
           animal.snowingEffect();
       }
       else {
           if (animal instanceof Algae) {
                Algae algae = (Algae) animal;
                algae.resetGrowth(); 
           }
       }
    }
}
