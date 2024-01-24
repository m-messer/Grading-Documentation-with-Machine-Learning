import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import javax.swing.*;

/**
 * A marine life predator-prey simulator, based on a rectangular field
 * containing animals, plants and humans (hunters).
 *
 * @version 12.02.2021
 */
public class Simulator
{
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 160;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
    // List of humans in the field.
    private List<Hunter> hunters;
    // The current state of the field.
    private Field field;

    // A boolean to record when hunters have been added to the field
    private static boolean huntersAdded = false;
    // The current step of the simulation.
    private int step;

    // A graphical view of the simulation.
    private SimulatorView view;
    // An object whose functionality is setting up the fied for the simulation.
    private FieldSetup fieldSetup;

    //An object to monitor the time of day (day or night); 
    private TimeOfDay time = new TimeOfDay(); 
    //The length of one day in the field
    private final int LENGTH_OF_DAY = 10; 

    //An object that keeps track of the weather in the field
    private Weather weather = new Weather(); 
    //The length of one season in the field
    private final int LENGTH_OF_SEASON = 100; 

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
        hunters = new ArrayList<>();

        field = new Field(DEFAULT_DEPTH, DEFAULT_WIDTH);
        view = new SimulatorView(DEFAULT_DEPTH, DEFAULT_WIDTH);
        fieldSetup = new FieldSetup(DEFAULT_DEPTH, DEFAULT_WIDTH, view, field, this);

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
     * Run the simulation from its current state for a period of one day
     * (one day and one night)
     */
    public void simulateOneDay()
    {
        simulate(LENGTH_OF_DAY*2);
    }

    /**
     * Run the simulation from its current state for a period of one week
     * (7 days and 7 nights)
     */
    public void simulateOneWeek()
    {
        simulate(LENGTH_OF_DAY*7*2);
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
            delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * organism.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();  
        // Provide space for newborn plants.
        List<Plant> newPlants = new ArrayList<>();  

        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }

        // Let all plants act.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(newPlants);
            if(! plant.isAlive()) {
                it.remove();
            }
        }

        // Let all hunters hunt.
        for(Iterator<Hunter> it = hunters.iterator(); it.hasNext(); ) {
            Hunter hunter = it.next();
            hunter.hunt();
            if(! hunter.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);
        // Add the newly born plants to the main lists.
        plants.addAll(newPlants);

        //adjust background colour for time of day 
        if(step%LENGTH_OF_DAY == 0){
            //change time of day
            time.changeTime(); 
            view.setTimeOfDayColor(); 
            view.setSettingLabelText();
        }

        //adjust weather label 
        if(step%LENGTH_OF_SEASON == 0){
            weather.changeWeather(); 
            view.setSettingLabelText();
        }

        //add hunters to the field after 3 days
        if((time.getDaysPassed() == 3) && !huntersAdded){
            fieldSetup.populateHunters(this); 
            huntersAdded = true; 
        }

        view.setDaysLabelText(); 

        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        time.setTime("day"); 
        view.setTimeOfDayColor(); 
        weather.setWeather("sunny");
        time.resetDaysPassed();
        view.setDaysLabelText(); 
        animals.clear();
        plants.clear();
        hunters.clear(); 
        huntersAdded = false; 

        // Repopulates field to initial state.
        fieldSetup.populate(this);

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * @return animals The list of animals on the field.
     */
    public List getAnimals()
    {
        return animals;
    }

    /**
     * @return plants The list of plants on the field.
     */
    public List getPlants()
    {
        return plants;
    }

    /**
     * @return hunters The list of hunters on the field.
     */
    public List getHunters()
    {
        return hunters;
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
