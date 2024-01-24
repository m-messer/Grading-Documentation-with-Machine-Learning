import java.text.DecimalFormat;

/**
 * A simple model of a prey. Preys age, move, breed, and die.
 *
 * @version 2021.03.01
 */
public class Prey extends BreedableActor {
    // The numerical amount of food that the prey is worth when eaten.
    private final int foodLevel;

    /**
     * Create a new prey. A prey may be created with age zero (a new born) or with a random age.
     *
     * @param species The species of the prey.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge If true, the prey will have a random age and hunger level.
     * @param foodLevel The numerical amount of food that the prey is worth when eaten.
     */
    public Prey(String species, Field field, Location location, boolean randomAge, int foodLevel)
    {
        super(species, field, location, randomAge);
        this.foodLevel = foodLevel;

        if (randomAge) {
            hungerLevel = rand.nextInt(foodLevel);
        }
        else {
            hungerLevel = foodLevel;
        }
    }

    /**
     * Get the numerical amount of food that the prey is worth when eaten.
     *
     * @return The amount of food that the prey is worth when eaten.
     */
    public int getFoodLevel() {
        return foodLevel;
    }

    /**
     * Create a newborn prey with the correct characteristics.
     *
     * @param field The field currently occupied.
     * @param loc The location within the field.
     * @return The newborn prey.
     */
    protected BreedableActor createYoung(Field field, Location loc) {
        Prey newPrey = new Prey(species, field, loc, false, foodLevel);
        newPrey.setMaxAge(maxAge);
        newPrey.setBreedingAge(breedingAge);
        newPrey.setBreedingProbability(breedingProbability);
        newPrey.setMaxLitterSize(maxLitterSize);
        newPrey.setFoodSources(foodSources);
        return newPrey;
    }

    /**
     * Get the longest the actor can last before dying.
     *
     * @return The longest the actor can last before dying.
     */
    public int getMaxHungerLevel() {
        return foodLevel;
    }

    /**
     * Return the predator's statistics text.
     *
     * @return The predator's formatted species, name, age, sex, hunger level, food level, pregnancy status and desires.
     */
    public String getStats() {
        StringBuilder text = new StringBuilder(super.getStats());
        text.append("\nName: ").append(name);
        text.append("\nAge: ").append(
                new DecimalFormat("#.##").format(age / (double) Simulator.TIME_MULTIPLIER)
        );
        text.append("\nSex: ").append(sex);
        text.append("\nHunger Level: ").append(hungerLevel);
        text.append("\nFood Level: ").append(foodLevel);
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
