import java.util.Random;
import java.util.List;
import java.awt.Color;
/**
 * This class sets the initial state of the population when 
 * the simulator is created.
 *
 * @version 2021.03.02
 */
public class PopulationGenerator
{
    private static final double FOX_CREATION_PROBABILITY = 0.03;
    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.09;
    
    private static final double WOLF_CREATION_PROBABILITY = 0.03;
    
    private static final double BEAR_CREATION_PROBABILITY = 0.05;
    
    private static final double DEER_CREATION_PROBABILITY = 0.07;
    
    private static final double GRASS_CREATION_PROBABILITY = 1;
    
    private SimulatorView view;

    /**
     * Constructor for objects of class PopulationGenerator
     */
    public PopulationGenerator()
    {
    }

    
    /**
     * This method randomly populated the field with various animals.
     */
    public void populate(List<Actor> actors,Field field)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY ) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    actors.add(fox);
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY ) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    actors.add(rabbit);
                }
                else if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, field, location);
                    actors.add(wolf);
                }
                else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    actors.add(bear);
                }
                else if(rand.nextDouble() <= DEER_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location);
                    actors.add(deer);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Grass grass = new Grass(field, location);
                    actors.add(grass);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * This method sets the color of all the subclasses of the animal class.
     * The animals will be represented in the simulator by the color set by tis method.
     */
    public void setColor(SimulatorView view)
    {
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Wolf.class, Color.GRAY);
        view.setColor(Deer.class, Color.BLACK);
        view.setColor(Bear.class, Color.RED);
        view.setColor(Grass.class,Color.GREEN);
    }
}
