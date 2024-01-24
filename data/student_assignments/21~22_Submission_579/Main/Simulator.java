package Main;


import java.util.Random;
import java.util.Map;
import java.util.LinkedHashMap;

import Environment.Habitat;
import Environment.HabitatGenerator;

import GUI.SimulatorWindow;
import GUI.EventCode;
import GUI.HabitatRenderer;
import GUI.RenderMode;

import Entities.CreatureActivity;

/**
 * A predator-prey simulator, based in a rectangular habitat containing land with a few rivers and lakes.
 * 
 * The habitat contains a number of animals. There are fish in the rivers and
 * lakes, which eat the algae growing in them, alongside other animals such as birds, bears, deer and wolves.
 * Some animals are predators which hunt prey, which only feed off of plants. 
 * Birds are treated as both predators and prey.
 * 
 * An object of the Simulator class contains both a Habitat and a SimulatorWindow (which represents the
 * user interface for interacting with the Habitat). The Simulator continuously polls the SimulatorWindow
 * for new events (such as a user command to start or stop the simulation) while also telling the Habitat
 * that it needs to generate a new "world" using a Noise Map. 
 * This world, representing the habitat, is generated in stages, described below.
 *
 * @version 2022.02.08
 */
public class Simulator
{
    // The stages that occur during generation of the habitat
    public static final Map<SimulationStage, String> stageNames = new LinkedHashMap<>(
        Map.of(
            SimulationStage.ELEVATION_GENERATION, "Elevation generation stage",
            SimulationStage.WEATHER_GENERATION, "Weather generation stage",
            SimulationStage.WATER_GENERATION, "Water generation stage",
            SimulationStage.SATURATION_GENERATION, "Saturation generation stage",
            SimulationStage.FLORA_GENERATION, "Flora generation stage",
            SimulationStage.FAUNA_GENERATION, "Fauna generation stage",
            SimulationStage.SIMULATION, "Simulation stage"
        )
    );
    
    // The different views of the simulation
    private static final Map<RenderMode, String> viewNames = new LinkedHashMap<>(
        Map.of(
            RenderMode.NORMAL , "Normal view",
            RenderMode.ELEVATION, "Elevation view",
            RenderMode.SATURATION, "Saturation view"
        )
    );
    
    // The current state of the habitat.
    private Habitat habitat;
    // A graphical view of the simulation.
    private SimulatorWindow window;
    
    // The stage that the simulation is in
    private SimulationStage stage;
    // The step of the stage that the simulation is in
    private long stageStep;
    
    // Whether or not the simulation is paused
    boolean paused;
    
    /**
     * Construct a simulation habitat with default size and seed.
     */
    public Simulator() {
        this(null, null, null);
    }
    
    /**
     * Create a simulation of a habitat with the given size and seed. If null parameters are given,
     * defaults will be used.
     * @param width Width of the habitat. Must be greater than zero.
     * @param height Height of the habitat. Must be greater than zero.
     */
    public Simulator(Long seed, Integer width, Integer height)
    {
        // Create a hanitat
        habitat = HabitatGenerator.generateHabitat(seed, width, height);
        
        // Create a viewer to view the habitat through.
        window = new SimulatorWindow(habitat.getWidth(), habitat.getHeight());
        // Set the render mode back to normal
        setRenderMode(RenderMode.NORMAL);
        setCloudVisibility(true);
        
        // Reset the simulation and the habitat to their initial states
        reset();
    }
    
    /**
     * Infinite loop to keep the window checking for new events and
     * running the simulation. Closing the window will exit the entire program,
     * including this loop.
     */
    public void run() {
        while(true) {
            // Process events
            // Get all window events (such as buttons pressed)
            EventCode[] codes = window.pollEventCodes();
            // Process each event code
            for(EventCode code : codes) {
                processEventCode(code);
            }
            
            // Run the simulation if it is not paused
            if(!paused) {
                step();
            }
            
            // Update the GUI with any changes
            window.update(habitat, stageNames.get(stage), stageStep);
        }
    }
    
    /**
     * Process an EventCode
     * @param code The EventCode to process
     */
    private void processEventCode(EventCode code) {
        switch(code) {
        // Pause or unpause the simulation if the pause/unpause button is pressed
        case PAUSE_UNPAUSE_PRESSED:
            togglePaused();
            break;
        case NEXT_STEP_PRESSED:
            step();
            break;
        case RESET_SIMULATION_PRESSED:
            reset();
            break;
        case CHANGE_VIEWING_MODE_PRESSED:
            cycleRenderMode();
            break;
        case TOGGLE_CLOUDS_PRESSED:
            toggleCloudVisibility();
            break;
        }
    }
    
    /**
     * Step the simulation through one step, whatever stage it is in.
     * If the simulation is ready to move to the next stage, it will
     */
    private void step() {
        // Make sure the step for this stage is being counted
        stageStep++;
        
        switch(stage) {
            // Simulate the habitat if the simulation is in the simulation stage
            case SIMULATION:
                habitat.step();
                break;
            // Generate the flora if the simulation is in the flora generation stage
            case FAUNA_GENERATION:
                HabitatGenerator.generateFauna(habitat);
                setStage(SimulationStage.SIMULATION);
                break;
            // Generate the flora if the simulation is in the flora generation stage
            case FLORA_GENERATION:
                HabitatGenerator.generateFlora(habitat);
                setStage(SimulationStage.FAUNA_GENERATION);
                break;
            // Generate the saturation if the simulation is in the saturation generation stage
            case SATURATION_GENERATION:
                HabitatGenerator.generateSaturations(habitat);
                setStage(SimulationStage.FLORA_GENERATION);
                break;
            // Generate the water if the simulation is in the water generation stage
            case WATER_GENERATION:
                HabitatGenerator.generateWater(habitat);
                setStage(SimulationStage.SATURATION_GENERATION);
                break;
            // Generate the weather if the simulation is in the weather generation stage
            case WEATHER_GENERATION:
                HabitatGenerator.generateWeather(habitat);
                setStage(SimulationStage.WATER_GENERATION);
                break;
            // Generate the elevation if the simulation is in the elevation generation stage
            case ELEVATION_GENERATION:
                HabitatGenerator.generateElevation(habitat);
                setStage(SimulationStage.WEATHER_GENERATION);
                break;
        }
    }
    
    /**
     * Reset the habitat.
     */
    private void reset() {
        // Reset the habitat
        habitat.reset();
        // Set the stage of the simulation back to terrain generation
        setStage(SimulationStage.ELEVATION_GENERATION);
    }
    
    /**
     * Set the stage of the simulation
     * @param stage The SimulationStage to set the simulation to
     */
    private void setStage(SimulationStage stage) {
        this.stage = stage;
        stageStep = 0;
        setPaused(true);
        switch(stage) {
            case ELEVATION_GENERATION:
            case WEATHER_GENERATION:
            case WATER_GENERATION:
            case SATURATION_GENERATION:
            case FLORA_GENERATION:
            case FAUNA_GENERATION:
                window.getControlPanel().setPausable(false);
                break;
            case SIMULATION:
                window.getControlPanel().setPausable(true);
                break;
        }
    }
    
    /**
     * Toggle whether the simulation is paused
     */
    private void togglePaused() {
        setPaused(!paused);
    }
    
    /**
     * Set whether or not the simulation is paused
     * @param paused Whether or not the simulation should be paused
     */
    private void setPaused(boolean paused) {
        this.paused = paused;
        window.getControlPanel().setPaused(paused);
    }
    
    /**
     * Toggles whether the clouds in the simulation are visible or not.
     */
    private void toggleCloudVisibility() {
        HabitatRenderer renderer = window.getHabitatRenderer();
        boolean cloudsVisible = renderer.getCloudVisibility();
        setCloudVisibility(!cloudsVisible);
    }
    
    /**
     * Set the visibility of the clouds to be either true (visible) or false (hidden). 
     * @param visible   either true or false
     */
    private void setCloudVisibility(boolean visible) {
        window.getHabitatRenderer().setCloudVisibility(visible);
        window.getControlPanel().setCloudVisibility(visible);
    }
    
    /**
     * Changes the render mode of the simulation to show different views and representations of the habitat.
     * Cycles from normal to elevation to saturation view, then back to normal.
     */
    private void cycleRenderMode() {
        HabitatRenderer renderer = window.getHabitatRenderer();
        RenderMode[] modes = RenderMode.values();
        RenderMode previousMode = renderer.getRenderMode();
        RenderMode nextMode = modes[(previousMode.ordinal()+1)%(modes.length)];
        setRenderMode(nextMode);
    }
    
    /**
     * Sets the type of mode we wish to represent the habitat with (normal, elevation, saturation).
     * @param mode      the type of mode we wish to display
     */
    private void setRenderMode(RenderMode mode) {
        window.getHabitatRenderer().setRenderMode(mode);
        window.getControlPanel().setViewButtonText(viewNames.get(mode));
    }
}
