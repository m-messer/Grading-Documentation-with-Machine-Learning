/**
 * Provide a counter for a participant in the simulation. This includes an
 * identifying species and a count of how many participants of this type
 * currently exist within the simulation.
 *
 * @version 2021.03.01
 */
public class Counter {
    // The name of the species being counted.
    private final String species;
    // How many of this species exist in the simulation.
    private int count;

    /**
     * Provide a for a species counter.
     *
     * @param species A species, e.g. "Panda".
     */
    public Counter(String species) {
        this.species = species;
        count = 0;
    }

    /**
     * Get the name of the species of this counter.
     *
     * @return The species name of this counter.
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Get the number of how many participants of this type currently exist.
     *
     * @return The current count for this species.
     */
    public int getCount() {
        return count;
    }

    /**
     * Increment the current count by one.
     */
    public void increment() {
        count++;
    }

    /**
     * Reset the current count to zero.
     */
    public void reset() {
        count = 0;
    }
}
