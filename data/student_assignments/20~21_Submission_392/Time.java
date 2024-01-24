/**
 * A class representing the time.
 *
 * @version 2021.03.03
 */
public class Time {

    double time;
    double unitPerDay;
    /**
     * Instantiate an object of class 'Weather'.
     * @param unitPerDay the number of units in a day. 24/ unitPerday gives the number of steps until a day changes
     */
    public Time(int unitPerDay) {
        time = 0;
        this.unitPerDay = unitPerDay;
    }

    /**
     * Give the current time.
     * @return the current time
     */
    public double whatIsTheTime() {
        return ((24 / unitPerDay) * time);
    }

    /**
     * Increments time by one unit. If it's midnight, set to 0.
     */
    public void incrementTime() {
        time++;
        if (time % unitPerDay == 0) {
            time = 0;
        }
    }

    /**
     * Return the current time in String
     * @return  the current time in String.
     */
    public String stringTime() {
        String string = "";
        int minutes = (int) ((whatIsTheTime() % 1) * 60);
        int hours = (int) (whatIsTheTime() - (whatIsTheTime() % 1));
        if (hours < 10) {
            string += "0" + hours + ":";
        } else string += hours + ":";
        if (minutes < 10) {
            string += "0" + minutes;
        } else string += minutes;
        return string;
    }

    public double getUnitPerDay() {
        return unitPerDay;
    }

}
