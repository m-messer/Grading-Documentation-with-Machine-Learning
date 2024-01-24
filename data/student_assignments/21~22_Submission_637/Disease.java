import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of Disease.
 *
 * @version 2022.03.02
 */
public abstract class Disease{
    //Random generator
    private static final Random rand = Randomizer.getRandom();
    //The chance of the disease spreading when an infected animal meets a non infected animal.
    protected double contagiousness; 
    //The probability that a non-infected animal becomes infected without being in contact with an infected animal.
    protected double chanceOfInfection;

    /**
     * Create a new disease  at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Disease(double contagiousness, double infectionChance)
    {
        this.contagiousness = contagiousness;
        chanceOfInfection = infectionChance;
        Random rand = Randomizer.getRandom();
    }
    
    /**
     * 
     */
    abstract public boolean canBeInfected(Animal animal);
    
    /**
     * 
     */
    abstract public double returnAgeDecrease();
    
    /**
     * The disease will spread after a certain number of steps
     */
    public boolean spread(){
        return (rand.nextDouble() <= contagiousness);
    }
    
    /**
     * 
     * @Return
     */
    public boolean infect(){
        return (rand.nextDouble() <= chanceOfInfection);
    }

}

