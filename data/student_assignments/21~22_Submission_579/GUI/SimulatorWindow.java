package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

import Main.Simulator;
import Main.SimulationStage;
import Environment.Habitat;
import Environment.Tile;

/**
 * A graphical view of the simulation grid.
 * The view displays a coloured rectangle for each location 
 * representing its contents. It uses a default background colour.
 * Colours for each type of species can be defined using the
 * setColour method.
 *
 * @version 2022.02.08
 */
public class SimulatorWindow extends JFrame
{
    private final String TITLE = "The Birds, the Wolves, and the Bears";
    private final String POPULATION_PREFIX = "Population: ";
    
    SimulatorControlPanel controlPanel;
    private HabitatRenderer habitatRenderer;
    private ArrayList<EventCode> eventCodes;
    
    private JLabel populationLabel, infoLabel;
    
    /**
     * Create a view of the given width and height.
     * @param width The simulation's width.
     * @param height The simulation's height.
     */
    public SimulatorWindow(int width, int height)
    {
        // Set the title of the window
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        eventCodes = new ArrayList<>();
        
        // Create contents of the window
        Container contents = getContentPane();
        // Create the panel for information
        populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        // Create the panel and renderer for the habitat
        HabitatPanel habitatPanel = new HabitatPanel(width, height);
            habitatRenderer = new HabitatRenderer(habitatPanel);
        
        // Create a panel for buttons
        controlPanel = new SimulatorControlPanel(this);
            
        // Add the contents to the window
        contents.add(infoPanel, BorderLayout.NORTH);
        contents.add(habitatPanel, BorderLayout.CENTER);
        contents.add(populationLabel, BorderLayout.SOUTH);
        contents.add(controlPanel, BorderLayout.EAST);
        
        // Finish construction
        setLocation(100, 50);
        pack();
    }

    /**
     * Update the status of the window.
     * @param habitat The habitat being viewed.
     * @param stage The stage that the simulation is in.
     * @param step The step that the simulation is on
     */
    public void update(Habitat habitat, String stageName, long step)
    {
        makeVisible();
        
        habitatRenderer.render(habitat);
        
        String extra = "";
        if(stageName == Simulator.stageNames.get(SimulationStage.SIMULATION)) {
            long time = step * habitat.getSecondsPerStep();
            long minute = (time / (60)) % 60;
            long hour = (time / (60*60)) % 24;
            long day = time / (60*60*24);
            extra = (" Day " + day + " at " + hour + ":" + (minute < 10 ? "0" + minute : minute));
        }
        controlPanel.setStageAndStepLabel(stageName, step, extra);
        
        populationLabel.setText(POPULATION_PREFIX + HabitatStats.getPopulationDetails(habitat));
        
        pack();
    }
    
    /**
     * Sets the simulator window visible.
     */
    private void makeVisible() {
        if(!isVisible()) {
            setVisible(true);
        }
    }
    
    /**
     * @return controlPanel     the control panel for the simulation
     */
    public SimulatorControlPanel getControlPanel() {
        return controlPanel;
    }
    
    /**
     * @return habitatRenderer      the habitat renderer for the simulation
     */
    public HabitatRenderer getHabitatRenderer() {
        return habitatRenderer;
    }
    
    /**
     * Add an event code to the window, indicating that action should be taken.
     * No visibility modifier indicates only usable from within GUI package.
     */
    public void addEventCode(EventCode code) {
        eventCodes.add(code);
    }
    
    /**
     * Get all the event codes that were added since pollEventCodes was last pressed.
     */
    public EventCode[] pollEventCodes() {
        EventCode[] codes = eventCodes.toArray(new EventCode[eventCodes.size()]);
        eventCodes.clear();
        return codes;
    }
}
