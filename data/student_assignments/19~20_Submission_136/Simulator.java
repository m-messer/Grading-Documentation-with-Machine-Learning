import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals: leopards, lions, gazelles, baboons, giraffes, and zebras.
 * plants: bushes and acacia trees.
 *
 * @version 2020.02.23
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 140;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 100;
    
    // The current step of the simulation.
    private int step;
    // The current number of infected species at a current step.
    private int counterForInfectedSpecies;
    // Check if it's day and night
    private boolean isDay;
    // HashSet of infected species
    private HashSet<String> infectedSpecies;
    // Keep reference to the first step of the new weather
    private int weatherFirstStep;
    // The number of steps for weather
    private int numberOfDaysForWeather;
    private Weather weather;
    private PopulationGenerator populationGenerator;
    private Random random;
    
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
        random = new Random();
        infectedSpecies = new HashSet<>();
        populationGenerator = new PopulationGenerator(depth, width);
        weather = new Weather();
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
        for(int step = 1; step <= numSteps && populationGenerator.getView().isViable(populationGenerator.getField()); step++) {
            simulateOneStep();
            //reset button is not visible while simulation is running
            populationGenerator.getView().resetButton(this).setVisible(false);
            delay(60);  // uncomment this to run more slowly
        }
        //reset button is visible when simulation ends
        populationGenerator.getView().resetButton(this).setVisible(true);
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each actor.
     * Generate a random number steps for weather
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn animals.
        List<Actor> newActors = new ArrayList<>();

        if(step % 5 == 0) {
            isDay = !isDay;
        }

        if(weatherFirstStep + numberOfDaysForWeather == step) {
            numberOfDaysForWeather = random.nextInt(10) + 1;
            weatherFirstStep = step;
            changeWeather();
        }
        
        infectedStats();
        // Update the infection Label on the screen
        populationGenerator.getView().setInfectedLabel(counterForInfectedSpecies, infectedSpecies);
        // Let all actors act.
        // For example, plants (Bushes and Acacias) acts when is day and it is raining
        for(Iterator<Actor> it = populationGenerator.getActors().iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            if(isDay) {
                if(weather.isRaining()) {
                    if(actor instanceof Plant) {
                        actor.act(newActors);
                    }
                    else if(actor instanceof Animal) {
                        actor.act();
                    }
                }
                else {
                    if(actor instanceof Animal) {
                        actor.act(newActors);
                    }
                    else if(actor instanceof Plant) {
                        actor.act();
                    }
                }
            }
            //night
            else {
                if(weather.isRaining()) {
                    if(actor instanceof Plant || actor instanceof Gazelle || 
                       actor instanceof Zebra || actor instanceof Leopard) {
                        actor.act(newActors);
                    }
                    else if(actor instanceof Lion || actor instanceof Baboon || 
                            actor instanceof Giraffe) {
                        actor.act();
                    }
                }
                else {
                    if(actor instanceof Leopard || actor instanceof Gazelle ||  
                       actor instanceof Giraffe) {
                        actor.act(newActors);
                    }
                    else  {
                        actor.act();
                    }
                }
            }
            if(! actor.isAlive()) {
                it.remove();
            }
        }
        // Add the newly born actors to the main lists.
        populationGenerator.getActors().addAll(newActors);
        //stats for day
        if(isDay) {
            populationGenerator.getView().showStatus(step, "day", populationGenerator.getField(), weather);
        }
        //stats for night
        else {
            populationGenerator.getView().showStatus(step, "night",populationGenerator.getField(), weather);
        }
    }

    /**
     * Change the weather randomly
     * The probability for raining is 25%
     * The probability for windy is 25%
     * The probability for sunny is 50%
     */
    private void changeWeather() 
    {
        double rainy_probability = 0.25;
        double windy_probability = 0.25;
        double sunny_probability = 1.0;

        double randomWeather = random.nextDouble();

        //rainy
        if(randomWeather <=  rainy_probability) {
            weather.setSunny(false);
            weather.setWindy(false);
            weather.setRaining(true);
        }
        //windy
        else if(randomWeather <= windy_probability) {
            weather.setSunny(false);
            weather.setWindy(true);
            weather.setRaining(false);
        }
        //sunny
        else if(randomWeather <= sunny_probability) {
            weather.setSunny(true);
            weather.setWindy(false);
            weather.setRaining(false);
        }
    }
    
    /**
     * Update the counterForInfectedSpecies at every step
     * Update the infectedSpecies HashSet at every step
     */
    public void infectedStats() 
    {
        infectedSpecies.clear();
        counterForInfectedSpecies = 0;
        for(Object i : populationGenerator.getActors()) {
            if( (i instanceof Animal) && ((Animal) i).isInfected() ) {
                infectedSpecies.add(i.getClass().getName());
                counterForInfectedSpecies++;
            }
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        populationGenerator.getActors().clear();
        step = 0;
        populationGenerator.populate();
        infectedStats();
        isDay = random.nextBoolean();
        weatherFirstStep = 0;
        numberOfDaysForWeather = random.nextInt(10) + 10;
        changeWeather();
        populationGenerator.getView().setInfectedLabel(counterForInfectedSpecies, infectedSpecies);
        if(isDay) {
            populationGenerator.getView().showStatus(step, "day", populationGenerator.getField(), weather);
        }
        else {
            populationGenerator.getView().showStatus(step, "night",populationGenerator.getField(), weather);
        }
        populationGenerator.getView().resetButton(this).setVisible(false);
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