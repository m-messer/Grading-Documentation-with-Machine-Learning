import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field containing predators, prey and plants.
 *
 * @version 2021.03.01
 */
public class Simulator
{
    // Constants representing configuration information for the simulation:
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 100;
    // How many minutes are in each step.
    private static final int TIME_INCREMENT = 30;
    // How many steps are in each year.
    protected static final int TIME_MULTIPLIER = 525600 / TIME_INCREMENT;
    // Night transition colours.
    private final int[] NIGHT_TIME_COLOR = {173, 188, 230};  // Pale blue
    private final int TRANSITION_PHASES = 6;

    // List of animals in the field.
    private final List<Actor> actors;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final List<SimulatorView> views;
    // The current time.
    private final Time time;
    // The current phase of night transition.
    private int nightTransition = 0;

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
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        actors = new ArrayList<>();
        field = new Field(depth, width);
        time = new Time();
        views = new ArrayList<>();

        // Create a view of the state of each location in the field.
        SimulatorView view = new GridView(depth, width);
        setColors(view);
        views.add(view);

        view = new GraphView(width*6, 150, 500);
        setColors(view);
        views.add(view);
        
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
        for (int step = 1; step <= numSteps && views.get(0).isViable(field); step++) {
            simulateOneStep();
            updateViews();
            // delay(5);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each species.
     */
    public void simulateOneStep()
    {
        step++;
        time.incrementTime(TIME_INCREMENT);

        int alphaMultiplier = 255 / TRANSITION_PHASES;
        if (time.isNight()) {
            // Begin the day-to-night transition.
            if(nightTransition < TRANSITION_PHASES) {
                // Calculate the correct transparency of the current night transition phase.
                views.get(0).setEmptyColor(new Color(NIGHT_TIME_COLOR[0], NIGHT_TIME_COLOR[1], NIGHT_TIME_COLOR[2], nightTransition * alphaMultiplier));
                nightTransition++;
            }
        } else {
            // Check whether night-to-day transition is needed.
            if(nightTransition > 1) {
                // Calculate the correct transparency of the current day transition phase.
                views.get(0).setEmptyColor(new Color(255, 255, 255, (255 - nightTransition * alphaMultiplier)));
                nightTransition--;
            }
        }

        // Provide space for newborn actors.
        List<Actor> newActors = new ArrayList<>();
        // Let all species act.
        for (Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, time);
            if(!actor.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born actors to the main lists.
        actors.addAll(newActors);

        updateViews();
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        for (SimulatorView view : views) {
            view.reset();
        }
        populate();
        updateViews();
    }

    /**
     * Update all existing views.
     */
    private void updateViews() {
        for (SimulatorView view : views) {
            view.showStatus(step, time.getDay(), time.getFormattedTime(), field);
        }
    }
    
    /**
     * Randomly populate the field with the initial actors of all species.
     */
    private void populate()
    {
        field.clear();

        // Create instances of ActorBuilder for each Actor, defining Actor attributes.
        ArrayList<ActorBuilder> builders = new ArrayList<>();
        ActorBuilder manBuilder = new ActorBuilder("Man", "predator", 0.005);
        manBuilder.setFoodSources(new ArrayList<>() {{
            add("Panda");
            add("Gorilla");
        }});
        manBuilder.setMaxAge(80 * TIME_MULTIPLIER);
        manBuilder.setBreedingAge(16 * TIME_MULTIPLIER);
        manBuilder.setBreedingProbability(0.9);
        manBuilder.setMaxLitterSize(4);
        manBuilder.setMaxHungerLevel((int)(((double)1/52) * TIME_MULTIPLIER)); // 1 week
        builders.add(manBuilder);

        ActorBuilder snowLeopardBuilder = new ActorBuilder("Snow leopard", "predator", 0.005);
        snowLeopardBuilder.setFoodSources(new ArrayList<>() {{
            add("Panda");
            add("Gorilla");
        }});
        snowLeopardBuilder.setMaxAge(22 * TIME_MULTIPLIER);
        snowLeopardBuilder.setBreedingAge(4 * TIME_MULTIPLIER);
        snowLeopardBuilder.setBreedingProbability(0.9);
        snowLeopardBuilder.setMaxLitterSize(6);
        snowLeopardBuilder.setMaxHungerLevel((int)(((double)1/52) * TIME_MULTIPLIER)); // 1 week
        builders.add(snowLeopardBuilder);

        ActorBuilder pandaBuilder = new ActorBuilder("Panda", "prey", 0.04);
        pandaBuilder.setFoodSources(new ArrayList<>() {{
            add("Bamboo");
        }});
        pandaBuilder.setMaxAge(30 * TIME_MULTIPLIER);
        pandaBuilder.setBreedingAge(6 * TIME_MULTIPLIER);
        pandaBuilder.setBreedingProbability(0.7);
        pandaBuilder.setMaxLitterSize(6);
        pandaBuilder.setFoodLevel((int)(((double)10/365) * TIME_MULTIPLIER)); // 10 days
        builders.add(pandaBuilder);

        ActorBuilder gorillaBuilder = new ActorBuilder("Gorilla", "prey", 0.04);
        gorillaBuilder.setFoodSources(new ArrayList<>() {{
            add("Bamboo");
            add("Grass");
        }});
        gorillaBuilder.setMaxAge(40 * TIME_MULTIPLIER);
        gorillaBuilder.setBreedingAge(10 * TIME_MULTIPLIER);
        gorillaBuilder.setBreedingProbability(0.7);
        gorillaBuilder.setMaxLitterSize(4);
        gorillaBuilder.setFoodLevel((int)(((double)8/365) * TIME_MULTIPLIER)); // 8 days
        builders.add(gorillaBuilder);

        ActorBuilder bambooBuilder = new ActorBuilder("Bamboo", "plant", 0.3);
        bambooBuilder.setFoodLevel(100 * TIME_INCREMENT);
        bambooBuilder.setBreedingProbability(0.0001);
        bambooBuilder.setMaxLitterSize(2);
        builders.add(bambooBuilder);

        ActorBuilder grassBuilder = new ActorBuilder("Grass", "plant", 0.2);
        grassBuilder.setFoodLevel(100 * TIME_INCREMENT);
        grassBuilder.setBreedingProbability(0.00005);
        grassBuilder.setMaxLitterSize(1);
        builders.add(grassBuilder);

        // Try to create new actor for each cell in field until one is created.
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                for (ActorBuilder builder: builders) {
                    Actor actor = builder.buildActor(field, new Location(row, col));
                    if (actor != null) {
                        actors.add(actor);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Pause for a given time.
     *
     * @param millisec The time to pause for, in milliseconds.
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
     * Sets the correct actor colours in the view.
     *
     * @param view The SimulatorView of which to set the colours.
     */
    private void setColors(SimulatorView view) {
        view.setColor("Man", Color.decode("#945A2E"));  // Brown
        view.setColor("Snow leopard", Color.decode("#DEAC40"));  // Yellow ochre
        view.setColor("Panda", Color.decode("#D984D7"));  // Pink
        view.setColor("Gorilla", Color.decode("#262626"));  // Dark gray
        view.setColor("Bamboo", Color.decode("#A9C7BA"));  // Mint green
        view.setColor("Grass", Color.decode("#347D41"));  // Forest green
    }

    /**
     * The main method.
     *
     * @param args Arguments passed to the program.
     */
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        //simulator.simulate(5000);
        while (true) {
            simulator.simulateOneStep();
        }
    }
}
