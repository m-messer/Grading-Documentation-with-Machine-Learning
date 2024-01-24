import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2021.03.02 (3)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a cat will be created in any given grid position.
    private static final double CAT_CREATION_PROBABILITY = 0.02;
    // The probability that a rat will be created in any given grid position.
    private static final double RAT_CREATION_PROBABILITY = 0.15;    
    // The probability that a snake will be created in any given grid position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.02; 
    // The probability that a toad will be created in any given grid position.
    private static final double TOAD_CREATION_PROBABILITY = 0.2; 
    // The probability that a wild chicken will be created in any given grid position.
    private static final double WILDCHICKEN_CREATION_PROBABILITY = 0.14; 
    // The probability that a grass plant will be created in any given grid position (of the plant field)
    private static final double GRASS_CREATION_PROBABILITY = 0.4;
    // The probability that a fern plant will be created in any given grid position (of the plant field)
    private static final double FERN_CREATION_PROBABILITY = 0.4;
    // Constant that determines how many steps must pass between additions of new plants.
    private static final int PLANTS_COOLDOWN = 25;

    // List of animals in the field.
    private List<Organism> organisms;

    //*******************************
    private List<Plant> plants;
    // The current state of the field.
    private Field field;

    // *******************************
    private Field plantField;

    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;

    private boolean timeOfDay;

    private boolean diseaseThresholdPassed;

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

        organisms = new ArrayList<>();
        field = new Field(depth, width);
        // *******************************
        plantField = new Field(depth, width);
        plants = new ArrayList<>();
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rat.class, Color.ORANGE);
        view.setColor(Cat.class, Color.CYAN);
        view.setColor(Snake.class, Color.GRAY);
        view.setColor(Toad.class, Color.MAGENTA);
        view.setColor(WildChicken.class, Color.GREEN);
        
        diseaseThresholdPassed = false;
        // Setup a valid starting point.
        reset();

        timeOfDay = false; //true meaning day, false meaning night
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
            delay(30);   // uncomment this to run more slowly
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

        // Needed to simulate immunization: past a certain percentage of the total animal population being sick, we consider immunization to take place
        int diseaseCounter = 0;

        // The organisms list does not distinguish between animals and plants, so we need to count
        int animalCounter = 0;

        createNewPlantsCooldown();

        changeTimeOfDay(); 

        List<Organism> newOrganisms = new ArrayList<>();
        for (Iterator<Organism> it = organisms.iterator(); it.hasNext(); ){
            Organism organism = it.next();
            randomizeDisease(organism); // Any animal has a chance to get sick at any point, apart from receiving the sickness from a neighboring animal

            if(diseaseThresholdPassed == true)  // If enough animals are sick, random animals will start to get cured, simulating herd immunization
                randomizeCure(organism);

            if(organism.isDiseased())
                diseaseCounter++;


            if(!(organism instanceof Plant))  // Not ideal, but may prove useful to have
                 animalCounter++;       // organisms generalised in the future
                
            organism.act(newOrganisms);
            if (! organism.isAlive()) 
                it.remove();
        }

        

        if(diseaseCounter > animalCounter/3) // If the percentage of sick animals is high enough, trigger immunization
            diseaseThresholdPassed = true;

        if(diseaseCounter < animalCounter/10) // If the percentage of sick animals drops to below 10%, the flag turns to false and immunization stops
            diseaseThresholdPassed = false;

        organisms.addAll(newOrganisms);
        view.showStatus(step, diseaseCounter, field, plantField);
    }

    /*
     * With a very small chance, makes the passed organism object sick.
     */
    private void randomizeDisease(Organism object){
        Random rand = Randomizer.getRandom();
        double diseaseRand = rand.nextDouble();
        if (diseaseRand < 0.01){
            object.getDisease();
        }
    }

    /*
     * Cures the passed organism object with a 70% chance.
     */
    private void randomizeCure(Organism object){
        Random rand = Randomizer.getRandom();
        double cureRand = rand.nextDouble();
        if (cureRand < 0.7){
            object.cureDisease();
        }
    }

    /*
     * Changes the boolean flag that represents the time of day every 5 steps
     */
    private void changeTimeOfDay(){
        if(step%5 == 1){
            timeOfDay = !timeOfDay;
        }
    }

    /*
     * Enforces a cooldown between plant population additions
     */
    private void createNewPlantsCooldown(){
        if (step%PLANTS_COOLDOWN == 1)
        {
            createNewPlants();
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        populatePlants();

        // Show the starting state in the view.
        view.showStatus(step, 0, field, plantField);
    }

    /**
     * Randomly populate the field with cats, rats, snakes, toads and wild chickens.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= CAT_CREATION_PROBABILITY) { // 
                    Location location = new Location(row, col);
                    Cat cat = new Cat( true, field, location, this);
                    organisms.add(cat);
                }
                else if(rand.nextDouble() <= RAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rat rat = new Rat( true, field, location, this);
                    organisms.add(rat);
                }
                else if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Snake snake = new Snake( true, field, location, this);
                    organisms.add(snake);
                }
                else if(rand.nextDouble() <= TOAD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Toad toad = new Toad( true, field, location, this);
                    organisms.add(toad);
                }
                else if(rand.nextDouble() <= WILDCHICKEN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    WildChicken wildChicken = new WildChicken( true, field, location, this);
                    organisms.add(wildChicken);
                }

                // else leave the location empty.
            }
        }
    }

    /**
     * Returns the "plant-level" field
     * @return Field the plant field
     */
    public Field getPlantField(){
        return plantField;
    }

    /**
     * Populates the plant field from scratch.
     */
    private void populatePlants(){
        plantField.clear();  
        createNewPlants();
    }

    /**
     * Adds new plants to the plant field, whether it is already populated or not.
     */
    private void createNewPlants(){
        Random rand = Randomizer.getRandom();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {

                Location location = new Location(row, col);
                Object object = plantField.getObjectAt(location);
                if (object==null){
                    if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY){
                        Grass grass = new Grass(true, plantField, location, this);
                        organisms.add(grass);
                    }
                    else if(rand.nextDouble() <= FERN_CREATION_PROBABILITY){
                        Fern fern = new Fern(true, plantField, location, this);
                        organisms.add(fern);
                    }
                }
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
     * Specifies the time of day
     * @return true if it is day, false otherwise
     */
    public boolean getTimeOfDay(){
        return timeOfDay;
    }
}
