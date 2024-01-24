package src;

/**
 * Dictates the delay between each step of the simulation.
 *
 * @version 2021.03.03
 */
public class delayTime {
    // The delay between each step in milliseconds
    private static int delay = 32;
    // The multiplier of the current delay (default x2)
    private static double mult = 2;
    // False if the simulation is paused
    private static boolean play = false;

    /**
     * Modifies the delay based on input:
     * doubles for k=1
     * halves for k=-1
     * @param k The input
     */
    public static void modifyDelay(int k){
        if(k == -1 && delay>1){
            delay /= 2;
            mult*= 2;
        }
        else if(k == 1 && delay < 1<<10){
            delay *= 2;
            mult/= 2;
        }
    }

    /**
     * @return The current multiplier
     */
    public static double getMult(){
        return mult;
    }

    /**
     * @return False if the simulation is paused.
     */
    public static boolean getPlay(){
        return play;
    }

    /**
     * Modifies the play field.
     * @param val The value to be assigned to play.
     */
    public static void setPlay(boolean val){
        play = val;
    }

    /**
     * @return The current delay.
     */
    public static int getDelay(){
        return delay;
    }
}
