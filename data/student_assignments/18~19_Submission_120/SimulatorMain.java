
/**
 * This class is part of the Predator/Prey simulator application. 
 * 
 * This class defines a main method within it to run the simulation.
 *
 * @version 22.02.19
 */
public class SimulatorMain
{
    /**
     * The starting point for the simulation.
     * @param args Program arguments.
     */
    public static void main(String[] args)
    {
        // if the length of the array is one,
        // assume this argument is the user's name
        // and print a welcome statement
        if(args.length == 1) {
            System.out.println("Hello there: " + args[0]);
        }
        Simulator simulator = new Simulator(); // create a Simulator object
        simulator.runLongSimulation(); // run the simulation
    }
}
