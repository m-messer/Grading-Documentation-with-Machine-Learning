/**
 * A class to keep track of the current time in the simulation, including day and night.
 *
 * @version 2021.03.01
 */

public class Time {
    // The current time in the simulation.
    private int timeInMinutes = 0;
    // The current day in the simulation.
    private int day = 1;
    // How long a day is: 1440 minutes is 24 hours.
    private final int dayLength = 1440;

    /**
     * Get the current time in the simulation.
     *
     * @return The current time.
     */
    public int getTimeInMinutes() {
        return timeInMinutes;
    }

    /**
     * Get the current day in the simulation.
     *
     * @return The current day.
     */
    public int getDay() {
        return day;
    }

    /**
     * Get the length of a day, in minutes.
     *
     * @return How long a day is, in minutes.
     */
    public int getDayLength() {
        return dayLength;
    }

    /**
     * Add a certain time increment to the current simulation time.
     *
     * @param increment The amount of time to increment by.
     */
    public void incrementTime(int increment) {
        if(timeInMinutes < dayLength - increment) {
            timeInMinutes += increment;
        } else {  // Start a new day
            day++;
            timeInMinutes = timeInMinutes + increment - dayLength;
        }
    }

    /**
     * Get a formatted string of the current time, in hh:mm format.
     *
     * @return The current time in hh:mm format.
     */
    public String getFormattedTime() {
        int hours = timeInMinutes / 60;
        int minutes = timeInMinutes % 60;
        return(String.format("%02d:%02d", hours, minutes));
    }

    /**
     * Find whether or not it is currently night-time.
     *
     * @return true if it is currently night-time.
     */
    public boolean isNight() {
        // Sunrise is at 5:30am, sunset is at 8pm.
        return timeInMinutes < 330 || timeInMinutes > 1200;
    }
}
