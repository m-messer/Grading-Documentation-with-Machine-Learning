import java.awt.Color;
import java.util.Random;
import java.util.List;

/**
 * This class generates the organism population of the simulation
 * in the beginning. 
 *
 * @version 2022.02.24
 */
public class PopulationGenerator
{
    // The probability that a grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.50;
    // The probability that a zebra will be created in any given grid position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.12;   
    // The probability that a buffalo will be created in any given grid position.
    private static final double BUFFALO_CREATION_PROBABILITY = 0.10;  
    // The probability that a hyena will be created in any given grid position.
    private static final double HYENA_CREATION_PROBABILITY = 0.05;
    // The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.04;    //

    /**
     * Initialise population generator in simulator view. 
     * @param view The visualisation
     */
    public PopulationGenerator(SimulatorView view)
    {
        // Setup associations between the organisms and color
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Zebra.class, Color.YELLOW);
        view.setColor(Buffalo.class, Color.BLUE);
        view.setColor(Hyena.class, Color.ORANGE);
        view.setColor(Lion.class, Color.RED);
    }

    /**
     * Randomly populate the field with grass, zebras, buffalos, hyenas and lions.
     */
    public void populate(Field field, List<Organism> actors)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if (rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Organism grass = new Grass(true, field, location);
                    actors.add(grass);
                }
                else if (rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Organism zebra = new Zebra(true, field, location);
                    actors.add(zebra);
                } 
                else if (rand.nextDouble() <= BUFFALO_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Organism buffalo = new Buffalo(true, field, location);
                    actors.add(buffalo);
                } else if (rand.nextDouble() <= HYENA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Organism hyena = new Hyena(true,field, location);
                    actors.add(hyena);
                } else if (rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row,col);
                    Organism lion = new Lion(true, field, location);
                    actors.add(lion);
                }
                // else leave the location empty.
            }
        }
    }  
}

