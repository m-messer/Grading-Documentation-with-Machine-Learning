import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A model of a general dinosaur with the common organism characteristics in addition to
 * a minimum breeding age and the amount of energy gained from the food that it has eaten.
 *
 * @version 2022-02-21
 */
public abstract class Dinosaur extends Organism{
    //The minimum age for the dinosaur to breed.
    private int breedingAge;
    //The amount of energy the dinosaur has from the food that it has consumed.
    private int foodConsumed;
    //The maximum amount of food that the dinosaur can consume.
    private int maxFoodConsumed;
    //The sex of the dinosaur (false is male, true is female)
    private boolean sex;

    /**
     * The constructor for the dinosaur class.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param age The current age.
     * @param maxAge The maximum age.
     * @param foodValue The number of steps it provides a predator.
     * @param breedingChance The chance that the organism will breed.
     * @param maxLitterSize The greatest possible size of a litter.
     */
    public Dinosaur(Field field, Location location, int age, int maxAge, int foodValue,
                    double breedingChance, int maxLitterSize, int breedingAge, int foodConsumed, int maxFoodConsumed, boolean shouldAct)
    {
        super(field, location, age, maxAge, foodValue, breedingChance, maxLitterSize, shouldAct);
        this.breedingAge = breedingAge;
        this.foodConsumed = foodConsumed;
        this.maxFoodConsumed = maxFoodConsumed;
        Random randSex = new Random();
        this.sex = randSex.nextBoolean();
    }

    /**
     * A method which increments the age and hunger of the dinosaur as well as
     * hunting prey. If its age increases past the threshold, it will die.
     * The method also enables the dinosaur to breed. After each rotation of the organism acting/not acting, the next rotation will be vice versa due o the changeShouldAct method
     * @param newOrganisms A list to receive newly born organisms.
     */
    public void act(List<Organism> newOrganisms) 
    {
        if(getShouldAct()){
            incrementAge();
            incrementHunger();
                if(isAlive()){
                giveBirth(newOrganisms);
                //Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null){
                    //No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                //See if it was possible to move.
                if(newLocation != null){
                    setLocation(newLocation);
                }else {
                    //Overcrowding.
                    setDead();
                }
            }
            changeShouldAct();
        }
        else{
            changeShouldAct();
        }
    }

    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    abstract protected Location findFood();


    /**
     * Check whether this dinosaur is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newDinos A list to return newly born dinosaurs.
     */
    abstract protected void giveBirth(List<Organism> newDinos);


    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    abstract protected int breed();

    /**
     * A dinosaur can breed if it has reached the breeding age and is a female.
     */
    protected boolean canBreed()
    {
        return (this.getAge() >= breedingAge && this.sex);
    }

    /**
     * Make this dinosaur more hungry. This could result in the dinosaur's death.
     */
    private void incrementHunger()
    {
        foodConsumed--;
        if(foodConsumed <= 0) {
            setDead();
        }
    }

    /**
     * Returns the food already consumed by the dinosaur.
     * @return int The food already consumed by the dinosaur.
     */
    protected int getFoodConsumed(){
        return foodConsumed;
    }

    /**
     * Sets the value of the food consumed to that stated in the parameter.
     * @param foodValue The value the foodConsumed variable should be updated to.
     */
    protected void setFoodConsumed(int foodValue)
    {
        this.foodConsumed = foodValue;
    }

    /**
     * Returns the boolean value corresponding to the dinosaurs sex.
     * @return boolean true if female, 0 if male.
     */
    public boolean getDinoSex()
    {
        return sex;
    }

    /**
     * Returns the maximum amount of food that the dinosaur is able to have consumed at one given time.
     * @return int the maximum amount of food that the dinosaur is able to have consumed at one time.
     */
    protected int getMaxFoodConsumed()
    {
        return maxFoodConsumed;
    }

}
