import java.text.DecimalFormat;

/**
 * A simple model of a predator.
 *
 * @version 2021.03.01
 */
public class Predator extends BreedableActor
{
    // The longest the actor can last before dying.
    private final int maxHungerLevel;

    /**
     * Create a predator. A predator can be created as a newborn (age
     * zero and not hungry) or with a random age and food level.
     *
     * @param species The species of the predator.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge If true, the predator will have random age and hunger level.
     * @param maxHungerLevel The longest the actor can last before dying.
     */
    public Predator(String species, Field field, Location location,
            boolean randomAge, int maxHungerLevel)
    {
        super(species, field, location, randomAge);

        this.maxHungerLevel = maxHungerLevel;
        if(randomAge) {
            hungerLevel = rand.nextInt(maxHungerLevel);
        }
        else {
            hungerLevel = maxHungerLevel;
        }
    }

    /**
     * Get the longest the actor can last before dying.
     *
     * @return The longest the actor can last before dying.
     */
    public int getMaxHungerLevel() {
        return maxHungerLevel;
    }

    /**
     * Create a newborn predator with the correct characteristics.
     *
     * @param field The field currently occupied.
     * @param loc The location within the field.
     * @return The newborn predator.
     */
    protected BreedableActor createYoung(Field field, Location loc) {
        Predator newPredator = new Predator(species, field, loc, false, maxHungerLevel);
        newPredator.setMaxAge(maxAge);
        newPredator.setBreedingAge(breedingAge);
        newPredator.setBreedingProbability(breedingProbability);
        newPredator.setMaxLitterSize(maxLitterSize);
        newPredator.setFoodSources(foodSources);
        return newPredator;
    }

    /**
     * Return the predator's statistics text.
     *
     * @return The predator's formatted species, name, age, sex, hunger level, pregnancy status and desires.
     */
    public String getStats() {
        StringBuilder text = new StringBuilder(super.getStats());
        text.append("\nName: ").append(name);
        text.append("\nAge: ").append(
                new DecimalFormat("#.##").format(age / (double) Simulator.TIME_MULTIPLIER)
        );
        text.append("\nSex: ").append(sex);
        text.append("\nHunger Level: ").append(hungerLevel);
        text.append("\nPregnant: ").append(pregnantTicks > 0);
        if (pregnantTicks > 0) {
            text.append("\nPregnant Ticks: ").append(pregnantTicks);
        }
        text.append("\n\nDesires: ");
        for (String desire: desires.keySet()) {
            text.append("\n - ").append(desire).append(": ").append(desires.get(desire));
        }
        return text.toString();
    }
}
