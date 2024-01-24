import java.util.List;
import java.util.Random;

/**
 * A class for plants in the simulation.
 *
 * @version 2021.03.01
 */
public class Plant extends Actor {
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The probability that the plant will reproduce.
    private double reproductionProbability;
    // The maximum number of seeds (children) that the plant can have.
    private int maxSeedSize;
    // The default numerical amount of food that the plant is worth when eaten.
    private int foodLevel = 0;

    /**
     * Create a new plant at location in field.
     *
     * @param species The species of the plant.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(String species, Field field, Location location) {
        super(species, field, location);
    }

    /**
     * Set the numerical amount of food that the plant is worth when eaten.
     *
     * @param foodLevel The amount of food that the plant is worth when eaten.
     */
    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    /**
     * Set the probability that the plant will reproduce.
     *
     * @param reproductionProbability The probability that the plant will reproduce.
     */
    public void setReproductionProbability(double reproductionProbability) {
        this.reproductionProbability = reproductionProbability;
    }

    /**
     * Set the maximum number of seeds (children) that the plant can have.
     *
     * @param maxSeedSize The maximum number of seeds that the plant can have.
     */
    public void setMaxSeedSize(int maxSeedSize) {
        this.maxSeedSize = maxSeedSize;
    }

    /**
     * Get the numerical amount of food that the plant is worth when eaten.
     *
     * @return The amount of food that the plant is worth when eaten.
     */
    public int getFoodLevel() {
        return foodLevel;
    }


    /**
     * Make this plant act - reproduce if it can.
     *
     * @param newActors A list to receive newly born plants.
     * @param time The current time.
     */
    @Override
    public void act(List<Actor> newActors, Time time) {
        if (isAlive()) {
            reproduce(newActors);
        }
    }

    /**
     * Try and create new plant children in adjacent locations.
     *
     * @param newPlants A list to receive newly born plants.
     */
    private void reproduce(List<Actor> newPlants) {
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = generateReproductions();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            // Configure the new plant.
            Plant young = new Plant(species, field, loc);
            young.setFoodLevel(foodLevel);
            young.setReproductionProbability(reproductionProbability);
            young.setMaxSeedSize(maxSeedSize);
            newPlants.add(young);
        }
    }

    /**
     * Calculate the number of new children to generate.
     *
     * @return The number of new plants to birth.
     */
    private int generateReproductions() {
        int reproductions = 0;
        if (rand.nextDouble() <= reproductionProbability) {
            reproductions = rand.nextInt(maxSeedSize) + 1;
        }
        return reproductions;
    }

    /**
     * Return the plant's statistics text.
     *
     * @return The plant's formatted species and food level.
     */
    public String getStats() {
        String text = super.getStats();
        text += "\nFood Level: " + foodLevel;
        return text;
    }
}
