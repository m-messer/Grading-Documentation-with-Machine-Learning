
/**
 * Write a description of class LivingThing here.
 * All living things will age and die.
 * @version 2022.03.01 (15)
 */
public abstract class LivingThing extends ActingThing
{
    // Whether the animal is alive or not.
    private boolean alive;
    private int foodValue;
    private int age;
    private int max_age;
    private Disease disease;
    /**
     * Create a new living entity
     */
    public LivingThing(Field field, Location location,DateTime dateTime)
    {
        super(field,location,dateTime);
        alive = true;

    }

    /**
     * Check whether the living entity is alive or not.
     * @return true if the living entity is still alive.
     */
    protected boolean canAct()
    {
        return alive;
    }

    /**
     * Indicate that the living entity is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(getLocation() != null) {
            if(this instanceof Animal){
                getField().clearAnimal(getLocation());
            }
            else{
                getField().clearPlant(getLocation());
            }
            remove();
        }
    }

    /**
     * @return how much energy the living entity worth
     */
    protected int getFoodValue()
    {
        return foodValue;
    }

    /**
     * Increase the age. This could result in the living entity's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > max_age) {
            setDead();
        }
    }
    /**
     * Set a random age for the living entity.
     * Used for the starting of the simulation.
     */
    protected void setRandomAge()
    {
        age = rand.nextInt(max_age);
    }
    
    /**
     * Set up the data for the living thing.
     */
    protected void setData(int foodValue,int max_age)
    {
        this.foodValue = foodValue;
        this.max_age=max_age;
        age = 0;
    }
    /**
     * @return the maximum age of the living thing.
     */
    protected int getMaxAge()
    {
        return max_age;
    }

    /**
     * @return the current age of the living thing.
     */
    protected int getAge()
    {
        return age;
    }
    
    /**
     * @return the disease of the living thing (can be null)
     */
    protected Disease getDisease()
    {
        return disease;
    }
    /**
     * Give a disease to the living thing
     * @param disease a kind of disease
     */
    protected void setDisease(Disease disease)
    {
        this.disease = disease;
    }
}
