import java.util.List;
import java.util.Random;
 
/**
 * A simple model of a cat.
 * cats age, move, eat rats, mate, and die.
 *
 * @version 2021.03.01 (3)
 */
public class Cat extends Animal
{
    //Characteristics shared by all cats (class variables).
 
    //The age at which this species can start to breed.
    private static final int BREEDING_AGE = 9;
 
    //The age to which this species can live.
    private static final int DEFAULT_MAX_AGE = 170;
 
    //The likelihood of this species rat breeding.
    private static final double DEFAULT_BREEDING_PROBABILITY = 0.475;
 
    //The maximum number of births.
    private static final int DEFAULT_MAX_LITTER_SIZE = 2;
 
    //How many steps this animal will survive after each meal
    private static final int DEFAULT_FOOD_VALUE = 55;
 
    //The minimum number of steps between matings for this species
    private static final int MATING_COOLDOWN = 2;
 
    //Set to true or false according to when you want the species to move and eat.
    private static final boolean MOVE_OR_EAT_DURING_DAY = false ;
 
    //Set to true or false according to when you want the species to mate.
    private static final boolean MATE_DURING_DAY = false ;
 
    //Set to true if this species should eat plants instead of a certain type of prey.
    //If this species eats other animals, the checkFoodType() method should be changed accordingly
    private static final boolean IS_HERBIVORE = false;
 
    //A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
 
    //How likely this species is to spread disease.
    private static final double DISEASE_SPREADING_PROBABILITY = 0.1;
 
    //Maximum age this species may live up to when sick.
    private static final int DISEASED_MAX_AGE = 160;
 
    //How likely this species is to breed when sick.
    private static final double DISEASED_BREEDING_PROBABILITY = 0.377;
 
    //Max number of offspring this species may create when sick.
    private static final int DISEASED_MAX_LITTER_SIZE = 2;
 
    //How many steps this animal will survive after each meal, when sick
    private static final int DISEASED_FOOD_VALUE = 50;
 
    //How many steps disease lasts for this species.
    //Note: The moment an animal falls ill is counted as the first step.
    private static final int DISEASE_LEVEL_START_VALUE = 15;
 
    //Instance fields holding values that dictate the longevity and possibility to procreate for this individual (instance) of this species.
    //These get modified depending on whether the animal is sick or not.
    private int ActualMaxAge;
    private double ActualBreedingProbability;
    private int ActualMaxLitterSize;
    private int ActualFoodValue;
 
    /**
     * Create a new object of this species. This animal may be created with age
     * zero (a new born) or with a random age.
     * @param randomAge If true, the animal will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cat(boolean randomAge, Field field, Location location, Simulator simulator)
    {
        super( randomAge, field, location, simulator);
        initializeDefaultStats();
        foodLevel = rand.nextInt(DEFAULT_FOOD_VALUE);
    }
 
    /**
     * Creates and returns a new animal of this specific species type object, to act as offspring.
     * @return A new animal of this specific species type object.
     * @param randomAge If true, the new animal will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field to put the new animal in.
     */
    public Animal getNewYoung( boolean randomAge, Field field, Location loc, Simulator simulator){
        return new Cat( false, field, loc, simulator);
    }
 
    /**
     * Checks if a passed object is of the same type as this species.
     * @return True if passed object is of the same type as this species, false otherwise
     */
    public boolean checkMate(Object object){
        if (object instanceof Cat){
            Cat holdCat = (Cat) object;
            if (!this.getGender().equals(holdCat.getGender()) && holdCat.canMate())
                return true;
            else
                return false;
        }
        return false;
    }
 
    /**
     * Specifies whether the object passed through the parameter represents the food this species eats.
     * @return boolean True if the passed object is food for this specific species, false otherwise
     */
    public boolean checkFoodType(Object object){ 
        return (object instanceof Rat || object instanceof WildChicken);
    }
 
    /**
     * Modifies longevity,nutrition and breeding probability values to simulate a sick animal.
     */
    public void getDisease(){
        diseaseLevel = DISEASE_LEVEL_START_VALUE;
        ActualMaxAge = DISEASED_MAX_AGE;
        ActualBreedingProbability = DISEASED_BREEDING_PROBABILITY;
        ActualMaxLitterSize = DISEASED_MAX_LITTER_SIZE;
        ActualFoodValue = DISEASED_FOOD_VALUE;
    }
 
    /**
     * Initializes/changes certain behaviour affecting values to 
     * default values.
     * Also used to make an animal not sick.
     */
    public void initializeDefaultStats(){
        ActualMaxAge = DEFAULT_MAX_AGE;
        ActualBreedingProbability = DEFAULT_BREEDING_PROBABILITY;
        ActualMaxLitterSize = DEFAULT_MAX_LITTER_SIZE;
        ActualFoodValue = DEFAULT_FOOD_VALUE;
    }
 
    /**
     * Return the value of the BREEDING_AGE constant;
     * @return Minimum age for breeding for this species.
     */
    public int getBreedingAge(){
        return BREEDING_AGE;
    }
 
    /**
     * Returns the value of the normal age an animal of this species may live up to
     * @return int Maximum age a healthy animal of this species may live up to.
     */
    public int getDefaultMaxAge(){
        return DEFAULT_MAX_AGE;
    }
 
    /**
     * Returns a number representing the steps an animal of this species
     * must wait before being able to mate again.
     * @return int The value of the "mating cooldown"
     */
    public int getMatingCooldown(){
        return MATING_COOLDOWN;
    }
 
    /**
     * Specifies if this species moves or eats during the day.
     * @return true if this species moves or eats during the day, false otherwise
     */
    public boolean movesOrEatsDuringDay(){
        return MOVE_OR_EAT_DURING_DAY;
    }
 
    /**
     * Specifies if this animal mates during the day.
     * @return true if this species mates during the day, false otherwise
     */
    public boolean matesDuringDay(){
        return MATE_DURING_DAY;
    }
 
    /**
     * Specifies whether this animal is a herbivore.
     * @return true if it is, false otherwise 
     */
    public boolean isHerbivore(){
        return IS_HERBIVORE;
    }
 
    /**
     * Return the probability that this specific species passes on a disease to another animal
     * @return double value is probability of a disease being passed on to another
     */
    public double getDiseaseSpreadingProbability(){
        return DISEASE_SPREADING_PROBABILITY;
    }
 
    /**
     * Return the current maximum age this species may live up to.
     * @return Current maximum age for this species.
     */
    public int getMaxAge(){
        return ActualMaxAge;
    }
 
    /**
     * Return the value dictating how probable this species is to breed.
     * @return Breeding probability of this species.
     */
    public double getBreedingProbability(){
        return ActualBreedingProbability;
    }
 
    /**
     * Return the maximum number of offspring this species may have at the moment.
     * @return Current max litter size for this species.
     */
    public int getMaxLitterSize(){
        return ActualMaxLitterSize;
    }
 
    /**
     * Returns the number of steps this animal may survive after having eaten.
     * @return int The food value for this species
     */
    public int getFoodValue(){
        return ActualFoodValue;
    }
}
 