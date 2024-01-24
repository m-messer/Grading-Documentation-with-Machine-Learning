package src;

/**
 * An Enum for all the weather types allowed in the simulation.
 *
 * @version 2021.03.03
 */
public enum WeatherTypes {
    SUNNY(8, 0, 1, 1),
    RAINY(4, 4, 0, 0.4),
    TORRENTIAL_RAIN(2, 8, -1, 0.2),
    FOGGY(0, 2, -2, 0.05);

    // How sunny this weather is; affects plant growth and death
    private final int sun;
    // How much humid this weather is; affects plant growth and death
    private final int water;
    // How hard or easy it is to see in this weather; affects animal sight distance
    private final int sightAffection;
    // The probability for this weather to happen in our simulation
    // A randomizer calculates a random Double value and the weather is chosen as such: The last weather type in the enum,
        // with a probability higher or equal to the random value
    private final double probability;

    /**
     * Constructor for WeatherTypes enums. Initializes fields.
     * @param sun How sunny it is.
     * @param water How humid it is.
     * @param sight How easy/hard to see it is.
     * @param probability What are the odds of it happening.
     */
    WeatherTypes(int sun, int water, int sight, double probability){
        this.sun=sun;
        this.water = water;
        this.sightAffection = sight;
        this.probability = probability;
    }

    /**
     * @return How sunny the weather is.
     */
    public int getSun() {
        return sun;
    }

    /**
     * @return How humid the weather is.
     */
    public int getWater(){
        return water;
    }

    /**
     * @return How the weather affects sight.
     */
    public int getSightAffection(){
        return sightAffection;
    }

    /**
     * @return The probability for this weather to happen
     */
    public double getProbability() {
        return probability;
    }
}
