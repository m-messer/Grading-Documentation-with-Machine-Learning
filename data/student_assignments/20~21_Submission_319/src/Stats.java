package src;

import java.util.EnumMap;

/**
 * Every actor will be attributed a Stats object which will hold their respective stats.
 *
 * @version 2021.03.03
 */
public class Stats {
    // An EnumMap which holds an Integer value for every Stat of the actor
    private final EnumMap<StatTypes, Integer> statValues;

    /**
     * Constructor for the Stats class. Initializes the stats.
     * @param stats A vararg of integers which will be attributed to every statType, in order.
     */
    public Stats(int ... stats){
        statValues = new EnumMap<>(StatTypes.class);

        //initialize stats according to the given array
        int j = 0;
        for(StatTypes i : StatTypes.values()) {
            statValues.put(i, stats[j]);
            j++;
        }
    }

    /**
     * Return the value of the specified stat type in this instance.
     * @param type A stat type to query.
     * @return An integer corresponding to the received stat type.
     */
    public Integer getStat(StatTypes type){
        return statValues.get(type);
    }
}
