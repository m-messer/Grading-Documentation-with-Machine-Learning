import java.awt.datatransfer.ClipboardOwner;
import java.util.*;
import java.awt.Color;

/**
 * 
 *
 * @version 2020.02.23
 */
public class PopulationGenerator
{
    // The probability that a gazelle will be created in any given grid position.
    private static final double GAZELLE_CREATION_PROBABILITY = 0.4;
    // The probability that a zebra will be created in any given grid position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.4;
    // The probability that a baboon will be created in any given grid position.
    private static final double BABOON_CREATION_PROBABILITY = 0.3;
    // The probability that a giraffe will be created in any given grid position.
    private static final double GIRAFFE_CREATION_PROBABILITY = 0.2;
    // The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.3;
    // The probability that a leopard will be created in any given grid position.
    private static final double LEOPARD_CREATION_PROBABILITY = 0.3;
    // The probability that a bush will be created in any given grid position.
    private static final double BUSH_CREATION_PROBABILITY = 0.8;
    // The probability that an acacia will be created in any given grid position.
    private static final double ACACIA_CREATION_PROBABILITY = 0.8;
    // The probability that an animal will be infected.
    private static final double INFECTED_PROBABILITY = 0.1;

    // List of actors in the field.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    /**
     * Constructs a new population and selects the colors for every specie
     * @param depth The depth of simulation field.
     * @param width The width of simulation field.
     */
    public PopulationGenerator(int depth, int width)
    {
        actors = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Gazelle.class, new Color(255, 255, 153));//light yellow
        view.setColor(Zebra.class, new Color(204, 204, 204));//light grey
        view.setColor(Baboon.class, new Color(153,102,0));//light brown
        view.setColor(Giraffe.class, Color.RED);//red
        view.setColor(Lion.class, Color.BLUE);//blue
        view.setColor(Leopard.class, new Color(51, 0, 0));//dark brown
        view.setColor(Bush.class, new Color(0,255, 51));//light green
        view.setColor(Acacia.class, new Color(0,102, 0));//dark green
    }
    
    /**
     * Randomly populate the field with actors:
     *  animals: lions, leopards, gazelles, zebras, baboons, giraffes 
     *  plants: bushes and acacias
     * Randomly sets the infected species of animals
     */
    public void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= GAZELLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Gazelle gazelle = new Gazelle(true, field, location);
                    if(rand.nextDouble() <= INFECTED_PROBABILITY) {
                        gazelle.setInfection();
                    }
                    actors.add(gazelle);
                }
                else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, field, location);
                    if(rand.nextDouble() <= INFECTED_PROBABILITY) {
                        zebra.setInfection();
                    }
                    actors.add(zebra);
                }
                else if(rand.nextDouble() <= BABOON_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Baboon baboon = new Baboon(true, field, location);
                    if(rand.nextDouble() <= INFECTED_PROBABILITY) {
                        baboon.setInfection();
                    }
                    actors.add(baboon);
                }
                else if(rand.nextDouble() <= GIRAFFE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Giraffe giraffe = new Giraffe(true, field, location);
                    if(rand.nextDouble() <= INFECTED_PROBABILITY) {
                        giraffe.setInfection();
                    }
                    actors.add(giraffe);
                }
                else if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    if(rand.nextDouble() <= INFECTED_PROBABILITY) {
                        lion.setInfection();
                    }
                    actors.add(lion);
                }
                else if(rand.nextDouble() <= LEOPARD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Leopard leopard = new Leopard(true, field, location);
                    if(rand.nextDouble() <= INFECTED_PROBABILITY) {
                        leopard.setInfection();
                    }
                    actors.add(leopard);
                }
                else if(rand.nextDouble() <= BUSH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bush bush = new Bush(true, field, location);
                    actors.add(bush);
                }
                else if(rand.nextDouble() <= ACACIA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Acacia acacia = new Acacia(true, field, location);
                    actors.add(acacia);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * @return The field.
     */
    public Field getField()
    {
        return field;
    }
    
    /**
     * @return The view.
     */
    public SimulatorView getView()
    {
        return view;
    }
    
    /**
     * @return The actors list.
     */
    public List getActors()
    {
        return actors;
    }
}