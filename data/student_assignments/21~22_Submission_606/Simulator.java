import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.LinkedList;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing different animals and plants.
 *
 * @version 2022.03.01 (15)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a wilddog will be created in any given grid position.
    private static final double WILDDOG_CREATION_PROBABILITY = 0.02;
    // The probability that a Hare will be created in any given grid position.
    private static final double HARE_CREATION_PROBABILITY = 0.2;   
    // and so on
    private static final double WARTHOG_CREATION_PROBABILITY = 0.08;
    private static final double HYENA_CREATION_PROBABILITY = 0.02;
    private static final double TOPI_CREATION_PROBABILITY = 0.05;
    private static final double WILDEBEEST_CREATION_PROBABILITY = 0.05;
    private static final double LION_CREATION_PROBABILITY = 0.008;
    private static final double STARGRASS_CREATION_PROBABILITY = 0.3; 
    private static final double REDOATGRASS_CREATION_PROBABILITY = 0.5; 

    private static final int NUMBER_OF_CLOUD = 10;
    //cloud's max radius;
    private static final int CLOUD_MAX_SIZE=10;
    //cloud's min raidus
    private static final int CLOUD_MIN_SIZE=5;
    private static final int NUMBER_OF_WATERBODY=50;
    private static final int WATER_MAX_SIZE=80;
    private static final int WATER_MIN_SIZE=30;
    // List of all actors like animals,plants,weathers in the field.
    private List<ActingThing> things;
    // The current state of the field.
    private Field field;
    // The current dateTime(step) of the simulation.
    private DateTime dateTime;
    // List of all views of the simulation.
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

        things = new ArrayList<>();
        field = new Field(depth, width);
        dateTime = new DateTime();
        // Create a view of the state of each animal in the field.
        views= new ArrayList<>();
        SimulatorView view = new AnimalView(depth, width);
        view.setColor(Hare.class, Color.ORANGE);
        view.setColor(WildDog.class, Color.LIGHT_GRAY);
        view.setColor(Warthog.class,Color.yellow);
        view.setColor(Hyena.class,Color.GRAY);
        view.setColor(Topi.class, Color.PINK);
        view.setColor(WildeBeest.class, Color.BLACK);
        view.setColor(Lion.class, Color.RED);
        views.add(view);

        // Create a view of the state of each plant in the field.
        view = new PlantView(depth, width);
        view.setColor(StarGrass.class,Color.GREEN);
        view.setColor(RedOatGrass.class,Color.YELLOW);
        views.add(view);

        // Create a graph view of the state of each animal in the field.
        view = new GraphView(500, 150, 500);
        view.setColor(Hare.class, Color.ORANGE);
        view.setColor(Warthog.class, Color.yellow);
        view.setColor(WildDog.class, Color.LIGHT_GRAY);
        view.setColor(Hyena.class,Color.GRAY);
        view.setColor(Topi.class, Color.PINK);
        view.setColor(WildeBeest.class, Color.BLACK);
        view.setColor(Lion.class, Color.RED);
        views.add(view);

        // Create a view of the state of the weather in the field.
        view = new WeatherView(depth,width);
        views.add(view);
        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {

        for(int step = 1; step <= numSteps && views.get(0).isViable(field); step++) {
            simulateOneStep();
            // delay(60);   // uncomment this to run more slowly
        }

    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * actors.
     */
    public void simulateOneStep()
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {
        dateTime.increment();

        field.clearAllWeather();

        if(CentralWeather.getCounter()<=5)
        {
            things.addAll(field.fillWeather(NUMBER_OF_CLOUD,CLOUD_MAX_SIZE,CLOUD_MIN_SIZE,dateTime));
        }
        // Provide space for newborn living things
        List<LivingThing> newThings = new ArrayList<>();   
        // Let all actors act.
        for(Iterator<ActingThing> it = things.iterator(); it.hasNext(); ) {
            ActingThing thing = it.next();
            thing.act(newThings);
            if(! thing.canAct()) {
                it.remove();
            }
        }

        // Add the newly born living things to the main lists.
        things.addAll(newThings);
        updateViews();
    }

    /**
     * update all the views
     */
    private void updateViews()
    {
        for (SimulatorView view : views) {
            view.showStatus(dateTime, field);
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        dateTime.reset();
        things.clear();
        populate();
        // Show the starting state in the view.
        updateViews();
    }

    /**
     * Randomly fill the field with animals,plants,water,weather.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        field.fillWater(NUMBER_OF_WATERBODY,WATER_MAX_SIZE,WATER_MIN_SIZE);
        things.addAll(field.fillWeather(NUMBER_OF_CLOUD,CLOUD_MAX_SIZE,CLOUD_MIN_SIZE,dateTime));
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(!field.hasWater(location)){
                    if(rand.nextDouble() <= WILDDOG_CREATION_PROBABILITY) {
                        WildDog wildDog = new WildDog(true, field, location,dateTime);
                        things.add(wildDog);
                    }
                    else if(rand.nextDouble() <= HARE_CREATION_PROBABILITY) {
                        Hare hare = new Hare(true, field, location,dateTime);
                        things.add(hare);
                    }
                    else if(rand.nextDouble() <= WARTHOG_CREATION_PROBABILITY) {
                        Warthog warthog = new Warthog(true, field, location,dateTime);
                        things.add(warthog);
                    }
                    else if(rand.nextDouble() <= HYENA_CREATION_PROBABILITY){
                        Hyena hyena = new Hyena(true, field, location,dateTime);
                        things.add(hyena);
                    }
                    else if(rand.nextDouble() <= WILDEBEEST_CREATION_PROBABILITY){
                        WildeBeest wildBeest = new WildeBeest(true, field, location,dateTime);
                        things.add(wildBeest);
                    }
                    else if(rand.nextDouble() <= TOPI_CREATION_PROBABILITY){
                        Topi topi = new Topi(true, field, location,dateTime);
                        things.add(topi);
                    }
                    else if(rand.nextDouble() <= LION_CREATION_PROBABILITY){
                        Lion lion = new Lion(true, field, location,dateTime);
                        things.add(lion);
                    }
                    if(rand.nextDouble() <= STARGRASS_CREATION_PROBABILITY){
                        StarGrass starGrass = new StarGrass(true, field, location,dateTime);
                        things.add(starGrass);
                    }
                    else if(rand.nextDouble() <= REDOATGRASS_CREATION_PROBABILITY){
                        RedOatGrass redOatGrass = new RedOatGrass(true, field, location,dateTime);
                        things.add(redOatGrass);
                    }

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
