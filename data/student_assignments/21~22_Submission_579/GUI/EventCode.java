package GUI;


/**
 * Represents the states of the simulation when a particular button is pressed.
 * The buttons Pause/Unpause, Next Step, Reset Simulation, Change Viewing Mode and Toggle Clouds are represented.
 *
 * @version 2022.02.19
 */
public enum EventCode
{
    PAUSE_UNPAUSE_PRESSED,
    NEXT_STEP_PRESSED,
    RESET_SIMULATION_PRESSED,
    
    CHANGE_VIEWING_MODE_PRESSED,
    TOGGLE_CLOUDS_PRESSED;
}
