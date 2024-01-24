
/**
 * Representations for all the valid time words for the field
 * along with a string in a particular language.
 *
 * @version 2022.02.26
 */
public enum Time { 
    //A value for each time word along its corresponding display string.
    DAY("Day"), NIGHT("Night");
    
    //The time string.
    private final String timeString; 
        
    /**
     * Initialise with the corresponding time string. 
     * @param timeString The time string.
     */
    Time (String timeString) 
    {
        this.timeString = timeString; 
    }
    
    /**
     * @return The time word as a string.
     */
    public String toString()
    {
        return timeString;
    }
}
