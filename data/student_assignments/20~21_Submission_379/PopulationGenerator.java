import java.util.Random;
import java.awt.Color;
import java.util.List;
import java.util.HashMap;

/**
 * A generator for the population of all simulating types.
 * The creation probability of each simulating type's population is 
 * used to create its initial population. The populations are reflected
 * on the simulator view through different colors representing each 
 * simulating type.
 *
 * @version 2021.03.03
 */
public class PopulationGenerator
{
    // The probability that a tiger will be created in any given grid position.
    private static final double TIGER_CREATION_PROBABILITY = 0.01;
    // The probability that a capybara will be created in any given grid position.
    private static final double CAPYBARA_CREATION_PROBABILITY = 0.02;    
    // The probability that a rat will be created in any given grid position.
    private static final double RAT_CREATION_PROBABILITY = 0.015;
    // The probability that a snake will be created in any given grid position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.012;
    // The probability that a deer will be created in any given grid position.
    private static final double DEER_CREATION_PROBABILITY = 0.016;
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.024;
    // The probability that castor plant will be created in any given grid position.
    private static final double CASTOR_CREATION_PROBABILITY = 0.006;
    // The probability that blueberry will be created in any given grid position.
    private static final double BERRY_CREATION_PROBABILITY = 0.021;

    // To store each simulating type with it's gender count.
    private HashMap<String, Integer> genderCount;

    /**
     * Construct the generator to create the populations.
     * @param view The graphical view of the simulation.
     */
    public PopulationGenerator(SimulatorView view)
    {
        genderCount = new HashMap<>();
        setColor(view);
    }

    /**
     * Randomly populate the field with species of animals and plants.
     * @param plants A list of initial plants in the simulation.
     * @param animals A list of initial animals in the simulation.
     * @param field The field of the simulation.
     */
    public void populate(List<Plant> plants, List<Animal> animals, Field field)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= CASTOR_CREATION_PROBABILITY) {
                    Location location = new Location(row,col);
                    Castor castor = new Castor(true, field, location);
                    plants.add(castor);
                } 
                else if(rand.nextDouble() <= TIGER_CREATION_PROBABILITY) {
                    Location location = new Location(row,col);
                    boolean isFemale = gender("tiger");
                    Tiger tiger = new Tiger(true, isFemale, field, location);
                    animals.add(tiger);
                } 
                else if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row,col);
                    boolean isFemale = gender("snake");
                    Snake snake = new Snake(true, isFemale, field, location);
                    animals.add(snake);
                }
                else if(rand.nextDouble() <= RAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isFemale = gender("rat");
                    Rat rat = new Rat(true, isFemale, field, location);
                    animals.add(rat);
                }
                else if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isFemale = gender("deer");
                    Deer deer = new Deer(true, isFemale, field, location);
                    animals.add(deer);
                }
                else if(rand.nextDouble() <= CAPYBARA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    boolean isFemale = gender("capybara");
                    Capybara capybara = new Capybara(true, isFemale, field, location);
                    animals.add(capybara);
                }
                else if(rand.nextDouble() <= BERRY_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Berry berry = new Berry(true, field, location);
                    plants.add(berry);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    plants.add(grass);
                }

                // else leave the location empty.
            }
        }
    }
    
    /**
     * Set the color of each animal and plant class in the simulation.
     * @param view The graphical view of the simulation.
     */
    public void setColor(SimulatorView view)
    {
        view.setColor(Capybara.class, Color.PINK);
        view.setColor(Tiger.class, Color.ORANGE);
        view.setColor(Rat.class, Color.RED);
        view.setColor(Deer.class, Color.MAGENTA);
        view.setColor(Snake.class, Color.BLACK);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Castor.class, Color.GRAY);
        view.setColor(Berry.class, Color.BLUE);
    }
    
    /**
     * Determine the initial gender of the animal.
     * @param count The count of the animal type created.
     * @return true if it is a female, otherwise, false if 
     * it is a male.
     */
    private boolean gender(String animal)
    {
        int count;
        if(!genderCount.containsKey(animal)) {
            genderCount.put(animal, 1);
            return false;
        } else {
            count = genderCount.get(animal);
            count++;
            genderCount.put(animal, count);
        }
        return count % 2 == 0;
    }
}