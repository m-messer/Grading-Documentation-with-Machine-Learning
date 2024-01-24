import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.awt.Color;
import java.io.IOException;


/**
 * A simple predator-prey simulator, based on a field containing various elements.
 *
 * @version 2020.02
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.

    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 200;
    // The default terrain size (relative size of the terrain structures).
    private static final int DEFAULT_TERRAIN_SIZE = 50;
    // The default seed for terrain generation noise map.
    private static final int DEFAULT_SEED = 123123;

    // Map to store the animal classes included in the simulation and their creation probabilities
    HashMap<String, Object[]> animalNames = new HashMap<>();

    // The current section of 24 hour period; day = true, night = false 
    private static boolean isDay = true;
    // The current section of 24 hour period; day = true, night = false 
    private static int currentDarkness = 0;
    // The current length of a day (48 steps), of which half is day and half is false
    private static final int dayLength = 24;
    // Determines if there is currently a drought occurring 
    private static boolean drought = false;
    
    // a list to store any created roads
    private static List<Road> roadList = new ArrayList<>();
    
    // List of animals in the field.
    private List<Actor> actors;
    // The current field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // List of graphical views of the simulation.
    private List<SimulatorView> views;

    // A shared random number generator
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator() throws IOException
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH, DEFAULT_TERRAIN_SIZE, DEFAULT_SEED);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     * @param terrainSize, the relative size of the terrain stuctures.
     * @param seed, the seed for the terrain generation noise map.
     * @throws IOException - if an IO exception occurs
     */
    public Simulator(int depth, int width, double terrainSize, long seed) throws IOException
    {
        initialiseAnimals();  // Add the names of animal classes, and their creation probabilities
        
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();
        field = new Field(depth, width, terrainSize, seed);
        
        views = new ArrayList<>();
        SimulatorView newView = new GridView(depth, width);     //Create the normal simulation view
        views.add(newView);

        newView = new PopulationGraph(500, 150, 500);           //Create the population graph view
        views.add(newView);

        newView = new GrassGraph(500, 150, 500);                //Create the Grass graph view
        views.add(newView);
        
        // Supply colors to GUI for included animals in the simulation
        for(SimulatorView view : views) {
            view.setColor(Zebra.class, Color.YELLOW);
            view.setColor(Lion.class, Color.BLUE);
            view.setColor(Stickleback.class, Color.WHITE);
            view.setColor(Pike.class, new Color(204, 102, 0));
            view.setColor(Alligator.class, new Color(153, 153, 0));
        }

        // Setup a valid starting point.
        reset();
    }

    /**
     * Populate the Map of animal names, their creation probabilities and their creation locations.
     */
    public void initialiseAnimals()
    {
        Object[] lionParams = new Object[] {0.07, Grass.class};
        Object[] zebraParams = new Object[] {0.24, Grass.class};
        Object[] sticklebackParams = new Object[] {0.24, DeepWater.class};
        Object[] pikeParams = new Object[] {0.07, DeepWater.class};
        Object[] alligatorParams = new Object[] {0.03, ShallowWater.class};

        animalNames.put("Lion", lionParams);
        animalNames.put("Zebra", zebraParams);
        animalNames.put("Stickleback", sticklebackParams);
        animalNames.put("Pike", pikeParams);
        animalNames.put("Alligator", alligatorParams);
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
        for(int step = 1; step <= numSteps && views.get(0).isViable(field); step++) {
            simulateOneStep();
            // delay(30);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * actor in the simulation
     */
    public void simulateOneStep()
    {
        step++;
        
        // commit the necessary changes as determined by weather
        determineWeatherEvents();
        determineDay();
        
        
        for(int i =0; i < roadList.size(); i++) {
            roadList.get(i).generateRoad();
        }
        
        
        // Provide space for newborn animals.
        List<Actor> newActors = new ArrayList<>();
        // Let all animals act.
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            actor.act(newActors, isDay, drought); // parameter isDay to determine day/night specific actions
            if(! actor.isActive()) {
                it.remove();
            }
        }

        // Add the newly born animals to the main list.
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
    private void updateViews()
    {
        for (SimulatorView view : views) {
            view.showStatus(step, field, isDay, currentDarkness);
        }
    }

    /**
     * Randomly populate the field with the animals using reflection.
     */
    private void populate()
    {
        generateField();  // Initialise terrain tiles, and plants where applicable. 
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Set<String> names = animalNames.keySet();
                Iterator<String> it = names.iterator();
                boolean notPopulated = true;
                while(it.hasNext() && notPopulated) {
                    String name = it.next();
                    Object prob = animalNames.get(name)[0];
                    double probability = (Double) prob;
                    Object tile = animalNames.get(name)[1];
                    Class tileClass = (Class) tile;
                    if(rand.nextDouble() <= probability) {
                        Location location = new Location(row, col);
                        if(field.getTile(location).getClass() == tileClass){
                            try{
                                Object obj = Class.forName(name).getConstructor(boolean.class, Field.class, Location.class, int.class).newInstance(true, field, location, 0);
                                Actor animal = (Animal) obj;
                                actors.add(animal);
                            }
                            catch(ReflectiveOperationException e) {
                                System.out.println(e);
                            }
                        }
                    }

                    // else leave the location empty.
                }
            }
        }
    }

    /**
     * Generate the field with correct Tile type, depending on TerrainGenerator.
     */
    public void generateField()
    {
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                float luminance = TerrainGenerator.getLuminance(row, col);
                field.getField()[row][col] = Grassland.determineTile(luminance);
                Plant newPlant = field.getTile(row, col).getPlant();
                if(newPlant != null) {actors.add(newPlant);}
            }
        }
    }
    
    /**
     * Generate ONLY the tiles which have been recognized to change because of a flood.
     */
    public void generateFloodTiles()
    {
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if (!(field.getTile(row, col) instanceof Tarmac)) {
                    float luminance = TerrainGenerator.getLuminance(row, col);
                    if(luminance > (Grassland.getDeepWaterThreshold() - Grassland.getIncrement()) && luminance < Grassland.getDeepWaterThreshold() 
                    || luminance > (Grassland.getShallowWaterThreshold() - Grassland.getIncrement()) && luminance < Grassland.getShallowWaterThreshold()) {
                        field.getField()[row][col] = Grassland.determineTile(luminance);
                        Plant newPlant = field.getTile(row, col).getPlant();
                        if(newPlant != null) {actors.add(newPlant);}
                    }
                }
            }
        }
    }

    /**
     * Generate ONLY the tiles which have been recognized to change because of a drought.
     */
    public void generateDroughtTiles()
    {
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if (!(field.getTile(row, col) instanceof Tarmac)) {
                    float luminance = TerrainGenerator.getLuminance(row, col);
                    if(luminance < (Grassland.getDeepWaterThreshold() + Grassland.getIncrement()) && luminance > Grassland.getDeepWaterThreshold() 
                    || luminance < (Grassland.getShallowWaterThreshold() + Grassland.getIncrement()) && luminance > Grassland.getShallowWaterThreshold()) {
                        field.getField()[row][col] = Grassland.determineTile(luminance);
                        Plant newPlant = field.getTile(row, col).getPlant();
                        if(newPlant != null) {actors.add(newPlant);}
                    }
                }
            }
        }
    }

    /**
     * Create a new road and add it to the List of roads.
     */
    public void createRoad()
    {
        Road road = new Road(field);
        roadList.add(road);
    }
    
    /**
     * Act on weather. Droughting causes multiple side effects.
     */
    public void determineWeatherEvents()
    {
        Weather weather = field.getWeather();

        weather.step();  // Let weather object act for one step.
        if(weather.isFlooding() == true) {
            Grassland.flood();
            generateFloodTiles();
            drought = false;
        }
        else if(weather.isDroughting() == true) {
            Grassland.drought();
            generateDroughtTiles();
            drought = true;
        }
        else drought = false;
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
     * Determine if it's currently day (0-11) or night (12-23)
     * Thus there are 24 'time steps' in a day, 12 per day, 12 per night
     */
    private void determineDay()
    {
        isDay = (step % dayLength) < 12;

        // construct a darkness gradient which will be happen throughout day/night cycles
        int val;
        if (step%dayLength < 9) val = 0;
        else if (step%dayLength == 9 || step%dayLength == 23) val = 1;
        else if (step%dayLength == 10 || step%dayLength == 22) val = 2;
        else if (step%dayLength == 11 || step%dayLength == 21) val = 3;
        else val = 4;

        setCurrentDarkness(val);
    }

    /**
     * @return True, if it's day. False, otherwise.
     */
    private boolean isDay()
    {
        return isDay;
    }

    /**
     * Set the currentDarkness value to a specified value
     * @param value  The value to which currentDarkness will be set to
     */
    private void setCurrentDarkness(int value)
    {
        currentDarkness = value;
    }

    /**
     * @return how how many 'darkness' values the tiles must currently have
     */
    private int getCurrentDarkness()
    {
        return currentDarkness;
    }
}
