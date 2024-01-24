package src;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.awt.Color;
import java.util.List;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2021.03.03
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    private static final int INFECTION_PROBABILITY = 1; // chance of random infection %

    // List of actors in the field.
    private final List<Actor> actors;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // A graphical view of the infection rates
    private final InfectionView infView;

    // Randomizer
    private final Random rand = Randomizer.getRandom();


    /**
     * Construct a simulation field with default size.
     */
    public Simulator() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth,width);
        infView = new InfectionView(width);

        setColors();

        // Setup a valid starting point.
        reset();
    }

    /**
     * Set the colors the entities should be displayed as in the simulatorView
     */
    private void setColors(){
        view.setColor(Thrush.class, getAwtColor(javafx.scene.paint.Color.BROWN));
        view.setColor(Wolf.class, getAwtColor(javafx.scene.paint.Color.LIGHTGRAY));
        view.setColor(FruitFly.class, getAwtColor(javafx.scene.paint.Color.GREENYELLOW));
        view.setColor(DragonFly.class, Color.RED);
        view.setColor(Butterfly.class, Color.PINK);
        view.setColor(Mango.class, Color.ORANGE);
        view.setColor(Lavender.class, getAwtColor(javafx.scene.paint.Color.PURPLE));
        view.setColor(Frog.class, getAwtColor(javafx.scene.paint.Color.DARKGREEN));
        view.setColor(Python.class, getAwtColor(javafx.scene.paint.Color.KHAKI));
        view.setColor(Flower.class, getAwtColor(javafx.scene.paint.Color.MEDIUMVIOLETRED));
        view.setColor(Eagle.class, getAwtColor(javafx.scene.paint.Color.SANDYBROWN));

        view.setWeatherColor(WeatherTypes.SUNNY, getAwtColor(javafx.scene.paint.Color.LIGHTGOLDENRODYELLOW));
        view.setWeatherColor(WeatherTypes.RAINY, getAwtColor(javafx.scene.paint.Color.DARKCYAN));
        view.setWeatherColor(WeatherTypes.TORRENTIAL_RAIN, getAwtColor(javafx.scene.paint.Color.DARKBLUE));
        view.setWeatherColor(WeatherTypes.FOGGY, getAwtColor(javafx.scene.paint.Color.LIGHTGRAY));
    }

    /**
     * Convert a javafx color to a java.awt color
     * @param col - color to be converted
     * @return converted color
     */
    private Color getAwtColor(javafx.scene.paint.Color col){
       return new java.awt.Color((float) col.getRed(),
                (float) col.getGreen(),
                (float) col.getBlue(),
                (float) col.getOpacity());
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

        int step = 1;

        while(step <= numSteps && view.isViable(field)) {
            if(delayTime.getPlay()){
            simulateOneStep();
            delay(delayTime.getDelay());
            step++;
            }
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        if(step%4==0)
            field.generateWeather();
        // Provide space for newborn actors.
        List<Actor> newActors = new ArrayList<>();
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            if(actor.isAlive())
                actor.act(newActors);
            if(! actor.isAlive()) {
                it.remove();
            }
        }
        field.advanceTimeOfDay();

        actors.addAll(newActors);

        infectRandom();

        view.showStatus(step, field);
        infView.update(field);
    }

    /**
     * infect random animals
     */
    private void infectRandom(){
        int n = actors.size()*INFECTION_PROBABILITY/100;
        Actor act;

        int i=0;

        while(i<n){
            act = actors.get(rand.nextInt(actors.size()));
            if(act instanceof Animal)
            {
                ((Animal) act).infect(); // infect animals
            }
            i++;
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        step = 0;
        actors.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with animals.
     */
    private void populate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        HashMap<Class<?>, Double> spawnProbabilities= new HashMap<>();
        initializeProbabilities(spawnProbabilities);
        field.clear();
        field.generateWeather();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                for(Class<?> classType : spawnProbabilities.keySet())
                    if(rand.nextDouble() <= spawnProbabilities.get(classType))
                        actors.add((Actor) classType.getConstructor(boolean.class, Field.class, Location.class).newInstance(true, field, field.getLocation(row, col)));
            }
        }
    }

    /**
     * Set the probability of spawning for each animal (in the populate step)
     * @param spawnProbabilities - HashMap in which the probabilities are stored
     */
    private void initializeProbabilities(HashMap<Class<?>, Double> spawnProbabilities){
        spawnProbabilities.put(Butterfly.class, 0.2);
        spawnProbabilities.put(DragonFly.class, 0.1);
        spawnProbabilities.put(FruitFly.class, 0.5);
        spawnProbabilities.put(Lavender.class, 0.2);
        spawnProbabilities.put(Flower.class, 0.2);
        spawnProbabilities.put(Mango.class, 0.1);
        spawnProbabilities.put(Thrush.class, 0.08);
        spawnProbabilities.put(Wolf.class, 0.012);
        spawnProbabilities.put(Frog.class, 0.08);
        spawnProbabilities.put(Python.class, 0.0125);
        spawnProbabilities.put(Eagle.class, 0.01);
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
