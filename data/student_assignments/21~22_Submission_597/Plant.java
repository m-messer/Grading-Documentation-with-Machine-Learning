import java.util.List;

/**
 * A model of a plant including the characteristics that aren't inherited from the Organism class.
 * 21/02/2022
 */
public abstract class Plant extends Organism
{

    //The rate at which the plant grows and the food value increases.
    private int growthRate;

    /**
     * Creates a plant with a specific growth rate.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param age The current age.
     * @param maxAge The maximum age.
     * @param foodValue The number of steps it provides a predator.
     * @param breedingChance The chance that the organism will breed.
     * @param maxLitterSize The greatest possible size of a litter.
     * @param growthRate The rate at which the plant grows.
     * @param shouldAct Dictates whether the plant is in sync with the time of day to act
     */
    public Plant(Field field, Location location, int age, int maxAge, int foodValue,
                 double breedingChance, int maxLitterSize, int growthRate, boolean shouldAct)
    {
        super(field, location, age, maxAge, foodValue, breedingChance, maxLitterSize, shouldAct);
        this.growthRate = growthRate;
    }

    /**
     * A method which causes the foodValue field to increase. Everytime it increases,
     * the growthRate field decreases by 1. After each rotation of the plant acting/not acting, the next rotation will be vice versa 
     * @param newOrganisms A list to receive newly born organisms.
     */
    public void act(List<Organism> newOrganisms) 
    {
        if(getShouldAct()){
            incrementAge();
            if(this.getAge() % this.growthRate == 0){
                increaseFoodValue(1);
            }
    
            if(isAlive()) {
                giveBirth(newOrganisms);
                // Try to move into a free location.
                Location newLocation = getField().freeAdjacentLocation(getLocation());
                   //See if it was possible to move.
                if(newLocation != null){
                    setLocation(this.getLocation());
                }
                else {
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
     * An abstract class as the way species give birth can be different but it is a method all species share
     */
    protected abstract void giveBirth(List<Organism> newPlant);

}
