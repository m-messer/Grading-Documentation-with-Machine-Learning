import java.util.ArrayList;
import java.util.Random;

/**
 * A class that builds the initial actors based on creation parameters.
 *
 * @version 2021.03.01
 */
public class ActorBuilder {
    // The species of the actor to be built.
    private final String species;
    // The type (predator, prey or plant) of the actor.
    private final String type;
    // The probability that an actor is built.
    private double creationProbability;
    // The food sources that the actor can eat.
    private ArrayList<String> foodSources;
    // The numerical amount of food that the actor is worth when eaten.
    private int foodLevel;
    // The highest age the actor can reach before dying.
    private int maxAge;
    // The default age the actor must reach to start breeding.
    private int breedingAge = 1000;
    // The probability that the actor will breed.
    private double breedingProbability;
    // The maximum number of children that the actor can have.
    private int maxLitterSize;
    // The longest the actor can last before dying.
    private int maxHungerLevel;

    /**
     * Create a new actor builder.
     *
     * @param species The species of the actor to be built.
     * @param type The type (predator, prey or plant) to be built.
     * @param creationProbability The probability that an actor is built.
     */
    public ActorBuilder(String species, String type, double creationProbability) {
        this.species = species;
        this.type = type;
        this.creationProbability = creationProbability;
    }

    /**
     * Set the types of food source that the actor can eat.
     *
     * @param foodSources An array of food source species names.
     */
    public void setFoodSources(ArrayList<String> foodSources) {
        this.foodSources = foodSources;
    }

    /**
     * Set the numerical amount of food that the actor is worth when eaten.
     *
     * @param foodLevel The numerical amount of food the actor is worth.
     */
    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    /**
     * Set the highest age the actor can reach before dying.
     *
     * @param maxAge The highest age the actor can reach.
     */
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Set the age the actor must reach to start breeding.
     *
     * @param breedingAge The age the actor must reach to breed.
     */
    public void setBreedingAge(int breedingAge) {
        this.breedingAge = breedingAge;
    }

    /**
     * Set the probability that the actor will breed.
     *
     * @param breedingProbability The probability that the actor will breed.
     */
    public void setBreedingProbability(double breedingProbability) {
        this.breedingProbability = breedingProbability;
    }

    /**
     * Set the maximum number of children that the actor can have.
     *
     * @param maxLitterSize The maximum number of children that the actor can have.
     */
    public void setMaxLitterSize(int maxLitterSize) {
        this.maxLitterSize = maxLitterSize;
    }

    /**
     * Set the longest the actor can last before dying.
     *
     * @param maxHungerLevel The longest the actor can last before dying.
     */
    public void setMaxHungerLevel(int maxHungerLevel) {
        this.maxHungerLevel = maxHungerLevel;
    }

    /**
     * Set the probability that an actor is built.
     *
     * @param creationProbability The probability that an actor is built.
     */
    public void setCreationProbability(double creationProbability) {
        this.creationProbability = creationProbability;
    }

    /**
     * Get the species of the actor to be built.
     *
     * @return The species of the actor to be built.
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Build a random actor based on the builder's parameters.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return The Actor that was built, or null if no actor was built.
     */
    public Actor buildActor(Field field, Location location) {
        Random rand = Randomizer.getRandom(); // might need to move this x
        if (rand.nextDouble() <= creationProbability) {  // Check whether any actor will be built.
            switch (type) {  // Build the correct type of actor with the correct characteristics.
                case "prey": {
                    Prey newPrey = new Prey(species, field, location, true, foodLevel);
                    newPrey.setMaxAge(maxAge);
                    newPrey.setBreedingAge(breedingAge);
                    newPrey.setBreedingProbability(breedingProbability);
                    newPrey.setMaxLitterSize(maxLitterSize);
                    newPrey.setFoodSources(foodSources);
                    return newPrey;
                }
                case "predator": {
                    Predator newPredator = new Predator(species, field, location, true, maxHungerLevel);
                    newPredator.setMaxAge(maxAge);
                    newPredator.setBreedingAge(breedingAge);
                    newPredator.setBreedingProbability(breedingProbability);
                    newPredator.setMaxLitterSize(maxLitterSize);
                    newPredator.setFoodSources(foodSources);
                    return newPredator;
                }
                case "plant": {
                    Plant newPlant = new Plant(species, field, location);
                    newPlant.setFoodLevel(foodLevel);
                    newPlant.setReproductionProbability(breedingProbability);
                    newPlant.setMaxSeedSize(maxLitterSize);
                    return newPlant;
                }
            }
        }
        return null;  // If no actor was buit.
    }
}
