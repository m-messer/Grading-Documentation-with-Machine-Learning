import java.util.Random;

/**
 * Represents a road in the simulation, which manifests as an impassable terrain tile that irreversibly seperates areas of the field.
 *
 * @version 2020.02
 */
public class Road
{
    private boolean roadConstruction; // determines whether or not a road is in construction
    private float slope; // the constant slope of the road
    private int startRoad; // the position of where the road starts
    private int currentRoadStep = 0; // incremented by 1 for each iteration of the simulation AFTER a road construction is called

    // true if starting from the top (going down), false if starting from the left (going right)
    private boolean fromTop;

    private static Field field;

    // A shared random number generator
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for class Road.
     */
    public Road(Field field)
    {
        this.field = field;
        roadConstruction = true;
        createRoad();
        fromTop = rand.nextDouble() < 0.5;
    }

    /**
     * Generate the road tiles at appropriate locations in the field.
     */
    public void generateRoad()
    {   
        int startPoint = startRoad+(Math.round(currentRoadStep*slope));
        if (roadConstruction) {
            if (fromTop) {
                for (int col = startPoint; col<(startPoint + 8); col++) {
                    field.getField()[currentRoadStep][col] = new Tarmac();
                }
                currentRoadStep++;
                if (currentRoadStep == field.getDepth()) roadConstruction = false;
            }
            else {
                for (int row = startPoint; row<(startPoint + 8); row++) {
                    field.getField()[row][currentRoadStep] = new Tarmac();
                }
                currentRoadStep++;
                if (currentRoadStep == field.getWidth()) roadConstruction = false;
            }
        }
    }

    /**
     * Create a random value for the road's starting point, and calculate the road's slope.
     */
    public void createRoad()
    {
        if (fromTop) { // top to bottom
            startRoad = rand.nextInt(field.getWidth()-5)+5; // create the start point within sensible bounds
            int end = field.getWidth() - startRoad;
            slope = (float)(end-startRoad)/field.getDepth(); // obtain the (inverted) gradient of our road
        }
        else { // left to right
            startRoad = rand.nextInt(field.getDepth()-5)+5; // create the start point within sensible bounds
            int end = field.getDepth() - startRoad;
            slope = (float)(end-startRoad)/field.getWidth(); // obtain the (inverted) gradient of our road
        }
    }
}
