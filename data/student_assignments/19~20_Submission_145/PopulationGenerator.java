import java.util.Random;
import java.util.List;
import java.awt.Color;

/**
 * The population generator class generates the animals, 
 * plants and hunters in the simulation.
 * It contains the creation probability for each actor.
 * It sets the colour for each actor.
 * 
 * @version 2020.02.22
 */
public class PopulationGenerator
{
    // The probability that a tiger will be created in any given grid position.
    private static final double TIGER_CREATION_PROBABILITY = 0.01;
    // The probability that a chicken will be created in any given grid position.
    private static final double CHICKEN_CREATION_PROBABILITY = 0.03;    
    // The probability that a bear will be created in any given grid position
    private static final double BEAR_CREATION_PROBABILITY = 0.01;
    // The probability that a deer will be created in any given grid position.
    private static final double DEER_CREATION_PROBABILITY = 0.04;    
    // The probability that a weasel will be created in any given grid position
    private static final double WEASEL_CREATION_PROBABILITY = 0.01;
    // The probability that grass will be created in any given grid position
    private static final double GRASS_CREATION_PROBABILITY = 0.045;
    // The probability that a berry will be created in any given grid position
    private static final double BERRY_CREATION_PROBABILITY = 0.06;
    // The probability that a hunter will be created in any given grid position
    private static final double HUNTER_CREATION_PROBABILITY = 0.001;

    /**
     * Constructor for objects of class PopulationGenerator.
     */
    public PopulationGenerator(SimulatorView view)
    {
        view.setColor(Tiger.class, Color.BLUE);
        view.setColor(Chicken.class, Color.ORANGE);       
        view.setColor(Bear.class, Color.YELLOW);
        view.setColor(Deer.class, Color.PINK);
        view.setColor(Weasel.class, Color.RED);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Berry.class, Color.MAGENTA);
        view.setColor(Hunter.class, Color.BLACK);
    }

    /**
     * Randomly populates the field with actors.
     * Actors: Chicken, deer, weasel, tiger, bear, grass, berry, hunter.
     * 
     * @param field     The simulation's field.
     * @param animals   The animals generated in the simulation.
     * @param plants    The plants generated in the simulation.
     * @param hunters   The hunters generated in the simulation.
     */
    public void populate(Field field, List<Animal> animals, List<Plant> plants, List<Hunter> hunters)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        int hunterCount = 0;
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location);
                    animals.add(deer);
                }
                else if(rand.nextDouble() <= CHICKEN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Chicken chicken = new Chicken(true, field, location);
                    animals.add(chicken);
                }
                else if(rand.nextDouble() <= WEASEL_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Weasel weasel = new Weasel(true, field, location);
                    animals.add(weasel);
                }
                else if(rand.nextDouble() <= TIGER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Tiger tiger = new Tiger(true, field, location);
                    animals.add(tiger);
                }
                else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY){
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    animals.add(bear);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    plants.add(grass);
                }
                else if(rand.nextDouble() <= BERRY_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Berry berry = new Berry(true, field, location);
                    plants.add(berry);
                }else if(rand.nextDouble() <= HUNTER_CREATION_PROBABILITY && hunterCount < 4) {
                    Location location = new Location(row, col);
                    Hunter hunter = new Hunter(field, location);
                    hunters.add(hunter);
                    hunterCount ++;
                }
                // else leave the location empty.
            }
        }
        
        // to make sure there is at least one hunter in the simulation.
        // manually insert a hunter if no hunter created yet.
        if(hunterCount == 0) {
            Hunter hunter = new Hunter(field, new Location(0,0));
            hunters.add(hunter);
        }  
    }    
}
