
/**
 * This class keeps track of the time of day
 *
 * @version 21.02.2020 (2)
 */
public class DayNight
{

    //a variable which stores the time of the day
    private boolean isDay;
    
    //a temp variable which stores the calculation of steps
    private int a;
    
    //a variable which stores the current button pressed
    private boolean button;
    
    /**
     * crating the DayNight, initialising the variables 
     */
    public DayNight()
    {
        //seting the variable to true, so
        //always the simulator will start in day
        isDay = true;
        
        //seting the variable to true, so
        //always the simulator will be on its automatic mode
        button = false;
    }

    /**
     *  this method gets the steps from SimulatorView Class
     *  and then computes the the time of the day by
     *  having the count of steps
     *  each 100 step the day and night changes
     *  this method will only be running if the button
     *  is in automatic mode
     */
    public void computeStep(int step)
    {
        a= ((step - (step % 100))/100) % 2;  //calculating the time 
        
        //old calculation:
        //a = step % 100;
        //b = step - a;
        //c = b / 100;
        //d = c % 2;
        
        //if the button chosen is on automatic mode
        if (!button){
            //if the calculation shows that is day
            if(a == 0)
            {
                isDay = true;  //the current time changes to day
            }
            else
            {
                isDay = false;  //the current time changes to night
            }
        }
    }

    /**
     *  returns a boolean of the current time
     *  true if its day
     *  false if its night
     */
    public boolean isDay()
    {
        return isDay;
    }

    
    /**
     * returns a string of current time
     */
    public String showTime()
    {
        //if its day
        if(isDay())
        {
            return "ITS DAY!!!";

        }
        else
        {
            return "ITS NIGHT!!!";
        }
    }

    /**
     * this method will change the current time to day
     * and also the current mode to manual
     */
    public void day()
    {

        button = true;
        isDay = true;
    }

    /**
     * this method will change the current time to night
     * and also the current mode to manual
     */
    public void night()
    {
        button = true;
        isDay = false;
    }

    /**
     * this method changes the current time mode to auto
     */
    public void auto()
    {
        button = false;
    }

    /**
     * this method changes the time depending on what it is
     * if the current time is day, it changes to night 
     * vice versa
     */
    public void changeTime()
    {
        if (button)
        {
            if (isDay)
            {
                isDay = false;
            }
            else if(!isDay)
            {
                isDay = true;
            }
        }
    }

    /**
     * this method returns string of current time mode
     */
    public String showMode()
    {
        if (!button)
        {
            return "(Auto mode)";
        }
        else if (button)
        {
            return "(Manual mode)";
        }
        return "MODE";
    }
}
