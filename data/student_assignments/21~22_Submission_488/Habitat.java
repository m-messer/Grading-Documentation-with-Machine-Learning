import java.util.*;
import java.awt.*;

/**
 * A predator-prey habitat.
 *
 */
public class Habitat {
    // Map of a point on the habitat to a grass object.
    private final HashMap<Point, Grass> grassMap = new HashMap<Point, Grass>();
    // Map of a point on the habitat to an animal object.
    private final HashMap<Point, Animal> animalMap = new HashMap<Point, Animal>();
    // Map of a point on the habitat to a color that would be displayed on screen.
    public final HashMap<Point, Color> topView = new HashMap<Point, Color>();
    // Map of a species to its population.
    public final HashMap<String, Integer> speciesData = new HashMap<String, Integer>();
    // The current virtual time.
    private int hour = 0;
    // The width and height.
    private int width, height;
    // The weather of this habitat. True if raining, false if drought.
    private boolean rain = true;
    // True of there are animals alive, false if not.
    private boolean alive = true;

    /**
     * Construct a habitat with defined dimensions.
     * 
     * @param width  The width of this panel;
     * @param height The height of this panel;
     */
    public Habitat(int width, int height) {

        Random rand = Helper.getRandom();
        this.width = width;
        this.height = height;

        generateGrass(0.5);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point point = new Point(x, y);

                // add new animals here
                if (rand.nextDouble() <= 0.01)
                    animalMap.put(point, new Witch(point, true));
                else if (rand.nextDouble() <= 0.02)
                    animalMap.put(point, new Zombie(point, true));
                else if (rand.nextDouble() <= 0.03)
                    animalMap.put(point, new FriedChicken(point, true));
                else if (rand.nextDouble() <= 0.04)
                    animalMap.put(point, new Pigman(point, true));
                else if (rand.nextDouble() <= 0.05)
                    animalMap.put(point, new Cow(point, true));
            }
        }
        update();
    }

    /**
     * Run the habitat from its current state for an virtual hour. Iterate over the
     * whole field to update the state of each organism.
     */
    public void passHalfDay() {
        hour += 12;

        rain = Helper.getRandom().nextDouble() < 0.3 ? true : false;

        if (rain)
            generateGrass(0.75);

        ArrayList<Grass> plants = new ArrayList<Grass>(grassMap.values());

        for (Grass grass : plants) {
            if (grass.isAlive())
                grass.grow();
        }

        ArrayList<Animal> animals = new ArrayList<Animal>(animalMap.values());

        for (Animal animal : animals) {
            if (animal.isAlive() && animal.nocturnal() == nightTime()) {
                animalMap.remove(animal.point());
                animal.actOn(getNeighborsFor(animal));

                if (animal.isAlive())
                    animalMap.put(animal.point(), animal);

                for (Animal offspring : animal.offsprings) {
                    animalMap.put(offspring.point(), offspring);
                }
            }
        }

        update();
    }

    /**
     * Gets the neighboring organims surrounding an organism.
     * 
     * @param organism The organism to get neighbors from.
     * 
     * @return List of organisms.
     */
    private Map<Point, Organism> getNeighborsFor(Animal animal) {
        Map<Point, Organism> neighbors = new HashMap<Point, Organism>();

        for (int xOffset = -2; xOffset <= 2; xOffset++)
            for (int yOffset = -2; yOffset <= 2; yOffset++) {
                if (xOffset != 0 && yOffset != 0) {
                    int nX = animal.point().x + xOffset;
                    int nY = animal.point().y + yOffset;

                    if (nX >= 0 && nX < width & nY < height && nY >= 0) {

                        Point point = new Point(nX, nY);
                        Grass grassNeighbor = grassMap.get(point);
                        Animal animalNeighbor = animalMap.get(point);

                        neighbors.put(point, grassNeighbor);
                        if (animalNeighbor != null && animalNeighbor.isAlive())
                            neighbors.put(animalNeighbor.point(), animalNeighbor);
                    }
                }
            }

        return neighbors;
    }

    /**
     * Generates grasses across the habitat.
     * 
     * @param probability The chance of a grass spawning in one cell.
     */
    private void generateGrass(double probability) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point point = new Point(x, y);
                Organism grass = grassMap.get(point);

                if (grass == null || !grass.isAlive()) {
                    if (Helper.getRandom().nextDouble() < probability)
                        grassMap.put(point, new Grass(point));
                }
            }
        }
    }

    /**
     * Checks if there are any animals alive.
     * 
     * @return true if there are animals alive, false if not.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Gets the current hour of the habitat.
     * 
     * @return The current hour.
     */
    public int time() {
        return hour;
    }

    /**
     * Gets the current weather.
     * 
     * @return True if raining, false if drought.
     */
    public boolean isRaining() {
        return rain;
    }

    /**
     * Gets the top view of the habitat.
     * 
     * @return A HashMap with each point mapped to a color.
     */
    public void update() {
        alive = false;

        for (String key : speciesData.keySet()) {
            speciesData.put(key, 0);
        }

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                Point point = new Point(x, y);
                topView.put(point, Color.decode("#F5F5F5"));
                updateOrganismStats(grassMap.get(point));
                updateOrganismStats(animalMap.get(point));
            }
    }

    /**
     * Updates the statistics of an organism.
     * 
     * @param The organism to be updated.
     */
    private void updateOrganismStats(Organism organism) {
        if (organism != null && organism.isAlive()) {
            topView.put(organism.point(), organism.color());
            incrementData(organism.getClass().getSimpleName() + "," + Helper.hexFrom(organism.color()));

            if (organism instanceof Animal) {
                this.alive = true;
                if (((Animal) organism).hasSTD()) {
                    topView.put(organism.point(), new Color(255, 0, 0, 200));
                    incrementData("STD Infected" + "," + Helper.hexFrom(Color.red));
                }
            }
        }
    }

    /**
     * Increments the population of a species.
     * 
     * @param key the name of the species combined with its color.
     */
    private void incrementData(String key) {
        if (speciesData.containsKey(key))
            speciesData.put(key, speciesData.get(key) + 1);
        else
            speciesData.put(key, 0);
    }

    /**
     * Gets the time of the day.
     * 
     * @return True if it's night time, false if day time.
     */
    public boolean nightTime() {
        return hour % 24 == 0 ? true : false;
    }
}
