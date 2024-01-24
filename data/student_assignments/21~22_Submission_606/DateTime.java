
/**
 * A class represent the date and time of the simulation world
 * Assume one step equals to one hour.
 * @version 2022.03.01 (15)
 */
public class DateTime
{
    private static final int DAY_START =  6;
    private static final int DAY_END =  18;
    private int date;
    private int time;
    private int step;

    /**
     * increment the step, date and time
     */
    public void increment()
    {
        step++;
        time++;
        if(time>=24){
            date++;
            time=0;
        }

    }
    /**
     * @return the date and time in this format: Day: 5 14:00
     */
    public String getDateTime()
    {
        if(time<10){
            return "Day: "+date +" 0"+time+":00";
        }
        else{
            return "Day: "+date +" "+ time+":00";
        }
    }
    /**
     * @return the current step
     */
    public int getStep()
    {
        return step;
    }
    /**
     * set the time to the start which is 0
     */
    public void reset()
    {
        date = 0;
        time = 0;
        step = 0;
    }
    /**
     * @return true if the time is between day start and end.
     */
    public boolean isDay()
    {
        return time>=DAY_START&&time<=DAY_END;
    }
}
