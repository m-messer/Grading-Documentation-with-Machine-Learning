package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * The control panel for the simulation, containing the buttons the user may press to modify
 * certain aspects of the simulation, e.g. making clouds visible or not.
 * All buttons done in JPanel.
 *
 * @version 2022.02.19
 */
public class SimulatorControlPanel extends JPanel
{
    SimulatorWindow parentWindow;
    
    private JPanel simulationPanel;
    private JLabel stageAndStepLabel;
    private JButton pauseButton, nextStepButton, resetButton;
    
    private JPanel viewPanel;
    private JButton changeViewButton, toggleCloudsButton;
    
    /**
     * Constructor for class SimulatorControlPanel
     */
    public SimulatorControlPanel(SimulatorWindow parentWindow)
    {
        super(new BorderLayout());
        this.parentWindow = parentWindow;
        
        simulationPanel = new JPanel(new BorderLayout());
        
        stageAndStepLabel = new JLabel();
        simulationPanel.add(stageAndStepLabel, BorderLayout.NORTH);
        stageAndStepLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        pauseButton = createButton("", EventCode.PAUSE_UNPAUSE_PRESSED);
        simulationPanel.add(pauseButton, BorderLayout.WEST);
        
        nextStepButton = createButton("Next step", EventCode.NEXT_STEP_PRESSED);
        simulationPanel.add(nextStepButton, BorderLayout.CENTER);
        
        resetButton = createButton("Reset", EventCode.RESET_SIMULATION_PRESSED);
        simulationPanel.add(resetButton, BorderLayout.EAST);
        
        add(simulationPanel, BorderLayout.NORTH);
        
        
        viewPanel = new JPanel(new BorderLayout());
        changeViewButton = createButton("", EventCode.CHANGE_VIEWING_MODE_PRESSED);
        viewPanel.add(changeViewButton, BorderLayout.NORTH);
        toggleCloudsButton = createButton("", EventCode.TOGGLE_CLOUDS_PRESSED);
        viewPanel.add(toggleCloudsButton, BorderLayout.SOUTH);
        simulationPanel.add(viewPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create a button wuth a particular name and code to execute upon press.
     * @param buttonText        name of the button
     * @param code          code to attempt to execute
     * @return button       the button with the above attributes
     */
    private JButton createButton(String buttonText, EventCode code) {
        JButton button = new JButton(buttonText);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == button) {
                    parentWindow.addEventCode(code);
                }
            }
        });
        return button;
    }
    
    /**
     * Sets the stage, number of steps passed and additional information on the Stage and Step label.
     * @param stageName     the name of the current simulation stage
     * @param step      number of steps passed since start of simulation phase
     * @param extra     any additional text to be displayed
     */
    public void setStageAndStepLabel(String stageName, long step, String extra) {
        String stepText = (pauseButton.isVisible() ? " (step " + step + ")" : "") + extra;
        stageAndStepLabel.setText("" + stageName + stepText);
    }
    
    /**
     * Sets the text on the view button.
     * @param text      the text to be displayed
     */
    public void setViewButtonText(String text) {
        changeViewButton.setText(text);
    }
    
    /**
     * Sets the pause button to be visible or invisible.
     * @param pausable      true if we want to see pause button, otherwise false
     */
    public void setPausable(boolean pausable) {
        pauseButton.setVisible(pausable);
    }
    
    /**
     * Sets the text on the screen to "Paused" or "Unpaused"
     * @param paused        true if we want to display "Paused", false if "Unpaused"
     */
    public void setPaused(boolean paused) {
        pauseButton.setText(paused ? " Paused " : "Unpaused");
    }
    
    /**
     * Sets the text on the screen to "Clouds: ON" or "Clouds: OFF"
     * @param paused        true if we want to display "Clouds: ON", false if "Clouds: OFF" 
     */
    public void setCloudVisibility(boolean visible) {
        toggleCloudsButton.setText(visible ? "Clouds: ON " : "Clouds: OFF");
    }
}