import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Velociraptors,Bears, Monkeys, Worms, Dragons, PsilocybinMushroom, Marijuana .
 *
 * @version 2022.03.02
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double VELOCIRAPTOR_CREATION_PROBABILITY = 0.02;
    // The probability that a rabbit will be created in any given grid position.
    private static final double Monkey_CREATION_PROBABILITY = 0.08;    
    // The probability that a bear will be created in any given grid position.
    private static final double BEAR_CREATION_PROBABILITY = 0.03;
    // The probability that a dragon will be created in any given grid position.
    private static final double DRAGON_CREATION_PROBABILITY = 0.001;
    
    private static final double WORM_CREATION_PROBABILITY = 0.1;
    
    private static final double MARIJUANA_CREATION_PROBABILITY = 0.1;
    
    private static final double PSILOCYBINMUSHROOM_CREATION_PROBABILITY = 0.1
    
    ;
    private List<Disease> diseases;
    // List of animals in the field.
    private List<Animal> animals;
    // The current state of the field.
    
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Is the simulation in day or night.
    private boolean isDay;
    
    private Rabies rabies;
    private Plague plague;
    
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
        
        diseases = new ArrayList<>();
        plants = new ArrayList<>();
        animals = new ArrayList<>();
        field = new Field(depth, width);
        rabies = new Rabies();
        plague = new Plague();
        
        addDiseases();
        
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Monkey.class, Color.ORANGE);
        view.setColor(Velociraptor.class, Color.BLUE);
        view.setColor(Bear.class, Color.YELLOW);
        view.setColor(Dragon.class, Color.RED);
        view.setColor(Marijuana.class, Color.GREEN);
        view.setColor(PsilocybinMushroom.class, Color.BLACK);
        view.setColor(Worm.class, Color.PINK);
        
        // Setup a valid starting point.
        reset();
    }
    
    
    private void addDiseases()
    {
        diseases.add(rabies);
        diseases.add(plague);
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
     * animal and plant
    */
    public void simulateOneStep()
    {
        step++;
        if (step%2000 >= 1000){
            if (isDay){
                isDay = false;
            }
        }
        else{
            if (!(isDay)){
                isDay = true;
            }
        }
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        List<Plant> newPlants = new ArrayList<>();
        // Let all rabbits act.
        if (isDay) {
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.actDay(newAnimals, diseases);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        for(Iterator<Plant> i = plants.iterator(); i.hasNext(); ) {
                Plant plant = i.next();
                plant.actDay(newPlants);
                if(! plant.isAlive()) {
                    i.remove();
                }
            }  
        }
        else {
             for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
                Animal animal = it.next();
                animal.actNight(newAnimals,diseases);
                if(! animal.isAlive()) {
                    it.remove();
                }
            }
            for(Iterator<Plant> i = plants.iterator(); i.hasNext(); ) {
                Plant plant = i.next();
                plant.actNight(newPlants);
                if(! plant.isAlive()) {
                    i.remove();
                }
            }  
            
            
            
            
            
        }
               
        // Add the newly born foxes and rabbits to the main lists.
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
        populate();
        isDay = true;
        
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
                if(rand.nextDouble() <= DRAGON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Dragon dragon= new Dragon(true, field, location);
                    animals.add(dragon);
                }
                if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    animals.add(bear);
                }
                else if(rand.nextDouble() <= VELOCIRAPTOR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Velociraptor velociraptor= new Velociraptor(true, field, location);
                    animals.add(velociraptor);
                }
                else if(rand.nextDouble() <= Monkey_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Monkey monkey = new Monkey(true, field, location);
                    animals.add(monkey);
                }
                 else if(rand.nextDouble() <= WORM_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Worm worm = new Worm(true, field, location);
                    animals.add(worm);
                }
                 else if(rand.nextDouble() <= PSILOCYBINMUSHROOM_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    PsilocybinMushroom psilocybinmushroom = new PsilocybinMushroom(true, field, location);
                    plants.add(psilocybinmushroom);
                }
                else if(rand.nextDouble() <= MARIJUANA_CREATION_PROBABILITY) {
                Location location = new Location(row, col);
                Marijuana Marijuana = new Marijuana(true, field, location);
                plants.add(Marijuana);
                //else leave the location empty. 
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
}
