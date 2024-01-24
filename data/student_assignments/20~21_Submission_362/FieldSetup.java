import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.util.Random;

/**
 * The functionality of this class is to populate the field with all organisms.
 *
 * @version 01.03.2021
 */
public class FieldSetup
{
    // Constants representing configuration information for the simulation.
    
    // The probability that a shark  will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.02;

    // The probability that a whale will be created in any given grid position.
    private static final double WHALE_CREATION_PROBABILITY = 0.07;  

    // The probability that a crab will be created in any given grid position.
    private static final double CRAB_CREATION_PROBABILITY = 0.09; 

    // The probability that a squid  will be created in any given grid position.
    private static final double SQUID_CREATION_PROBABILITY = 0.06;

    // The probability that a dolphin  will be created in any given grid position.
    private static final double DOLPHIN_CREATION_PROBABILITY = 0.07;

    // The probability that a phytoplankton will be created in any given grid position.
    private static final double PHYTOPLANKTON_CREATION_PROBABILITY = 0.10;     

    // The probability that a seaweed  will be created in any given grid position.
    private static final double SEAWEED_CREATION_PROBABILITY = 0.10;

    // The probability that a hunter will be created in any given grid position.
    private static final double HUNTER_CREATION_PROBABILITY = 0.005;

    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field.
    private List<Plant> plants;
    // List of humans in the field.
    private List<Hunter> hunters;

    // The current state of the field.
    private Field field;

    // A graphical view of the simulation.
    private SimulatorView view;

    /**
     * Constructor for objects of class FieldSetup
     */
    public FieldSetup(int depth, int width, SimulatorView view, Field field, Simulator simulator)
    {
        this.field = field;
        this.view =view;
        initialiseColors();
        populate(simulator);
    }

    /**
     * This method assigns unique colors to each type of organism.
     *
     */
    public void initialiseColors()
    {
        // Create a view of the state of each location in the field.
        view.setColor(Crab.class, Color.ORANGE);
        view.setColor(Squid.class, Color.PINK);
        view.setColor(Shark.class, Color.MAGENTA);
        view.setColor(Whale.class, Color.BLUE);
        view.setColor(Dolphin.class, Color.GRAY);
        view.setColor(Seaweed.class, Color.GREEN);
        view.setColor(Phytoplankton.class, Color.CYAN);
        view.setColor(Hunter.class, Color.RED);
    }

    /**
     * Add hunters to the field. 
     *
     */
    public void populateHunters(Simulator simulator)
    {
        hunters = simulator.getHunters();
        Random rand = Randomizer.getRandom();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= HUNTER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hunter hunter = new Hunter(field, location);
                    hunters.add(hunter);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Randomly populate the field with organisms.
     */
    public void populate(Simulator simulator)
    {
        animals = simulator.getAnimals();
        plants = simulator.getPlants();
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= SQUID_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Squid squid = new Squid(true, field, location);
                    animals.add(squid);
                }
                else if(rand.nextDouble() <= CRAB_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Crab crab = new Crab(true, field, location);
                    animals.add(crab);
                }
                else if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location);
                    animals.add(shark);
                }
                else if(rand.nextDouble() <= WHALE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Whale whale = new Whale(true, field, location);
                    animals.add(whale);
                }
                else if(rand.nextDouble() <= DOLPHIN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Dolphin dolphin = new Dolphin(true, field, location);
                    animals.add(dolphin);
                }
                else if(rand.nextDouble() <= SEAWEED_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seaweed seaweed = new Seaweed(true, field, location);
                    plants.add(seaweed);
                }
                else if(rand.nextDouble() <= PHYTOPLANKTON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Phytoplankton phytoplankton = new Phytoplankton(true, field, location);
                    plants.add(phytoplankton);
                }
                // else leave the location empty.
            }
        }
    }

}
