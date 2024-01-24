import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * An underwater habitat simulator, based on a rectangular field
 * containing sharks, fish, seals, whales and algae.
 *
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 180;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 140;
    // The probability that a shark will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.03;
    // The probability that a fish will be created in any given grid position.
    private static final double FISH_CREATION_PROBABILITY = 0.1;
    //The probability that a seal will be created in any given grid position.
    private static final double SEAL_CREATION_PROBABILITY = 0.065;
    //The probability that a whale will be created in any given grid position.
    private static final double WHALE_CREATION_PROBABILITY = 0.045;
    //The probability that a Seahorse will be created in any given grid position.
    private static final double SEAHORSE_CREATION_PROBABILITY = 0.08;
    //The probability that an Algae will be created in any given grid position.
    private static final double ALGAE_CREATION_PROBABILITY = 0.1;
    //The probability that an animal will be created with a disease.
    private static final double INFECTION_PROBABILITY = 0.05;
    //divides the day into 4 six hour chunks.
    private static final int HOURS = 24;
    

    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The current 6 hour chunk.
    private int dayStep;
    // The number of days that have passed.
    private int numberOfDays;
    // If it is currently night
    private boolean night;
    
    private int numberOfInfections;
    
    private ArrayList<Animal> infections;
    
    

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
        
        animals = new ArrayList<>();
        infections = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.

        views = new ArrayList<>();
        
        SimulatorView view = new GridView(depth, width);
        view.setColor(Fish.class, Color.ORANGE);
        view.setColor(Shark.class, Color.BLUE);
        view.setColor(Seal.class, Color.RED);
        view.setColor(Whale.class, Color.BLACK);
        view.setColor(Seahorse.class, Color.PINK);
        view.setColor(Algae.class, Color.GREEN);
        views.add(view);
        
        
        view = new GraphView(500, 150, 500);
        view.setColor(Fish.class, Color.ORANGE);
        view.setColor(Shark.class, Color.BLUE);
        view.setColor(Seal.class, Color.RED);
        view.setColor(Whale.class, Color.BLACK);
        view.setColor(Seahorse.class, Color.PINK);
        view.setColor(Algae.class, Color.GREEN);
        views.add(view);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (2000 steps).
     */
    public void runLongSimulation()
    {
        simulate(300);
    }
    
    /**
     * Keeps track of the time of day. Each step is a period of 1 hour.
     * Daytime and nighttime each last 12 hours.
     */
    
    private void timeOfDay()
    {
        if(dayStep<HOURS){
            dayStep++;
        }
        else{
            dayStep= 1;
            numberOfDays++;
        }
        
        if (dayStep<=12){
            night = false;
        }
        else{
            night = true;
        }

    }
    
    /**
     * Returns whether it is night as a boolean value.
     * @return night If it is night time.
     */
    private boolean getTimeOfDay()
    {
        return night;
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
            delay(30);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal.
     */
    public void simulateOneStep()
    {
        step++;
        timeOfDay();
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        List<Animal> newInfections = new ArrayList<>();
        List<Animal> newDeadInfections = new ArrayList<>();
       
        // Let all animals act.
       
        if (night == false){
            for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                Animal animal = it.next();
                animal.act(newAnimals);
                if(! animal.isAlive() && animal.isInfected()){
                    newDeadInfections.add(animal);
                }
                else if(animal.isAlive() && animal.isInfected()){
                    newInfections.add(animal);
                }
               
                if(! animal.isAlive()) {
                    it.remove();
                }
            }
        }
        else{
            for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                Animal animal = it.next();
                animal.nightAct(newAnimals);
                if(! animal.isAlive() && animal.isInfected()){
                    newDeadInfections.add(animal);
                }
                else if(animal.isAlive() && animal.isInfected()){
                    newInfections.add(animal);
                }
               
                if(! animal.isAlive()) {
                    it.remove();
                }
            }          
        }
       
        //Calculates the current infections in the habitat.
        infections.addAll(newInfections);
        infections.removeAll(newDeadInfections);
        numberOfInfections = infections.size();
             
        // Add the newly born sharks,fish,algae,seals and whales to the main lists.
        animals.addAll(newAnimals);

        updateViews();
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        dayStep = 0;
        numberOfDays = 0;
        numberOfInfections = 0;
        animals.clear();
        infections.clear();
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
            view.showStatus(step, field, numberOfDays, numberOfInfections , night);
        }
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location, true);
                    animals.add(shark);
                }
                else if(rand.nextDouble() <= FISH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fish fish = new Fish(true, field, location, true);
                    animals.add(fish);
                }
                else if(rand.nextDouble() <= SEAL_CREATION_PROBABILITY) {
                    Location location = new Location (row, col);
                    Seal seal = new Seal(true, field, location, true);
                    animals.add(seal);
                }
                else if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY) {
                    Location location = new Location (row, col);
                    Whale whale = new Whale(true, field, location, true);
                    animals.add(whale);
                }
                else if(rand.nextDouble() <=SEAHORSE_CREATION_PROBABILITY){
                    Location location = new Location (row, col);
                    Seahorse seahorse = new Seahorse(true, field, location, true);
                    animals.add(seahorse);
                }
                else if(rand.nextDouble() <=ALGAE_CREATION_PROBABILITY){
                    Location location = new Location (row, col);
                    Algae algae = new Algae(true, field, location);
                    animals.add(algae);
                }
                // else leave the location empty.
            }
        }
        
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            if(rand.nextDouble() <= INFECTION_PROBABILITY){
                animal.setDisease();
                infections.add(animal);
                numberOfInfections = infections.size();
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
