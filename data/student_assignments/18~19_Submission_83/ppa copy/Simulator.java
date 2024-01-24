
import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing moose and wolves.
 *
 * @version 2019.02.22 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a wolf will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.04;
    // The probability that a moose will be created in any given grid position.
    private static final double MOOSE_CREATION_PROBABILITY = 0.08;
    //The probability that a coyote will be created in any given grid position.
    private static final double COYOTE_CREATION_PROBABILITY = 0.05;
    //The probability that a zebra will be created in any given grid position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.09;
    //The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.05;
    //The probability that a plant will be created in any given grid position.
    private static double PLANT_CREATION_PROBABILITY = 0.11;
    //The frequency a plant respawns.
    private static double baseGrowth = 0.0005;


    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;

    // Keep track of the state of the weather.
    private boolean isDrought;
    private boolean isRain;
    private boolean isFoggy;

    //The time of day.
    private int time;
    //Keep track of day/night cycle.
    private boolean day;

    private List<Plant> plants;
    
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

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Moose.class, Color.ORANGE);
        view.setColor(Wolf.class, Color.BLUE);
        view.setColor(Plant.class, Color.GREEN);
        view.setColor(Zebra.class, Color.BLACK);
        view.setColor(Lion.class, Color.RED);
        view.setColor(Coyote.class, Color.PINK);

        //Create a view for the weather buttons;
        createButtonsView();

        // At the beginning there is no weather issue.
        isDrought = false;
        isRain = false;
        isFoggy = false;

        time = 0;
        day = false;

        // Setup a valid starting point.
        reset();

    }

    /**
     * Create a view for the weather buttons.
     */
    private void createButtonsView() {

        JFrame buttonsView = new JFrame("Buttons");
        JPanel panel = new JPanel();

        panel.setLayout(new FlowLayout());

        buttonsView.getContentPane().add(panel);

        JButton rainButton = new JButton("Rain");
        rainButton.addActionListener(e -> changeWeather(rainButton.getText()));

        JButton fogButton = new JButton("Fog");
        fogButton.addActionListener(e -> changeWeather(fogButton.getText()));

        JButton droughtButton = new JButton("Drought");
        droughtButton.addActionListener(e -> changeWeather(droughtButton.getText()));

        panel.add(rainButton);
        panel.add(fogButton);
        panel.add(droughtButton);

        buttonsView.pack();
        buttonsView.setVisible(true);
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
                if(time == 24) {
                    time = 0;
                }
                if(22 < time || time < 6) {
                    day = false;
                }else{day = true;}
                delay(60);
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * wolf and moose.
     */
    public void simulateOneStep()
    {
        step++;
        time++;


        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        // Let all animals act.
        if( day ) {
            for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                Animal animal = it.next();
                animal.act(newAnimals);
                if (!animal.isAlive()) {
                    it.remove();
                }
            }
        }

        //Keep generating plants at random free locations;
        for(int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++){
                Random rand = Randomizer.getRandom();
                double PLANT_PROBABILITY = rand.nextDouble();
                 Location location = new Location(row, col);
                if (field.getObjectAt(location) == null) {
                     if(PLANT_PROBABILITY<baseGrowth){
                     Plant plant = new Plant(field, location);
                    plants.add(plant);
                    }
                }
            }
        }
               
        // Add the newly born animals and plants to the main lists.
        animals.addAll(newAnimals);

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
     * Randomly populate the field with animals and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, field, location, rand.nextBoolean(), rand.nextBoolean());
                    animals.add(wolf);
                }
                else if(rand.nextDouble() <= MOOSE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Moose moose = new Moose(true, field, location, rand.nextBoolean(), rand.nextBoolean());
                    animals.add(moose);
                }
                else if(rand.nextDouble() <= COYOTE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Coyote coyote = new Coyote(true, field, location, rand.nextBoolean(), rand.nextBoolean());
                    animals.add(coyote);
                }
                else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, field, location, rand.nextBoolean(), rand.nextBoolean());
                    animals.add(zebra);
                }
                else if(rand.nextDouble() <= LION_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location, rand.nextBoolean(), rand.nextBoolean());
                    animals.add(lion);
                }
                else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY){
                    // else leave the location empty.
                        Location location = new Location(row, col);
                        Plant plant = new Plant(field, location);
                        plants.add(plant);
                }
            }
        }
    }

    /**
     * Update the behaviour of the animals and plants according to the weather selected.
     * @param label The label of the button clicked.
     */
    private void changeWeather(String label) {
        // If there is a drought, all plants die.
        if(label.equals("Drought")) {
            isDrought = ! isDrought;
            //It also stops raining if there is a drought.
            isRain = false;
            for(Plant plant : plants){
                plant.setDead();
            }
        }
        //If it is raining, plants respawn more often.
        else if(label.equals("Rain")) {
            isRain = ! isRain;
            //The drought also stops if there is one.
            isDrought = false;
            baseGrowth = 0.0007;
        }
        else {
            isFoggy = ! isFoggy;
            day = false;
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
     * Return the time of day.
     * @return the time of day.
     */
    public int getTime() {
        return time;
    }
}
