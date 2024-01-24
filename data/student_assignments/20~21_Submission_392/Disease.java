import java.util.Random;

/**
 * A class representing disease.
 * @version 2021.03.03.
 */
public class Disease {
     private double chance;
    private static final Random rand = Randomizer.getRandom();
    private int timeUntilDeath;

    /**
     * Constructor of class disease.
     * @param chance the chance of infection
     * @param timeUntilDeath how many days until the creature dies of infection
     */
    public Disease(double chance, int timeUntilDeath){
        if(chance<1)  this.chance=chance;
        else this.chance = 0.01;
        this.timeUntilDeath=timeUntilDeath;
    }

    /**
     * Randomly decide with a given probability if the creature should get infected.
     * @return true if the creature gets infected.
     */
    public boolean isInfected(){
        return (rand.nextDouble()<chance);
    }

    /**
     * Return how much time (in steps) after infection does the creature die.
     * @return days until death.
     */
    public int getTimeUntilDeath(){
        return timeUntilDeath;
    }
}
