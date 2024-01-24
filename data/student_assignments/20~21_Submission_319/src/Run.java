package src;

/**
 * Class that creates a new simulator and runs a simulation.
 *
 * @version 2021.03.03
 */

public class Run {

    /**
     * Main method - creates and runs the simulation
     * @param Args args
     * @throws Exception exception
     */
    public static void main(String[] Args) throws Exception {
        Simulator sim = new Simulator(120,180);
        sim.runLongSimulation();
    }
}
