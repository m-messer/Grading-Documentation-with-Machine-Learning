import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 2020.02.20 (2)
 */
public abstract class Animal extends Actor
{    
    private Gender gender;
    // The animal's age
    public int age;
    // The age to which an animal can live.
    private int maxAge;
    //
    private int foodLevel;
    
    private int foodValue;
    // The maximum number of births
    private int maxLitterSize;
    
    private Disease disease;
    
    /**
     * Constructor for objects of class Animal
     * @param randomAge The age of the animal at the beggining of the simulation
     * @param maxAge The maximum age that this animal can live to
     * @param field The field currently occupied
     * @param location The location of the grass
     * @param foodValue The value of the food this animal is eating 
     * @param maxLitterSize The maximum number of births
     */
    public Animal(boolean randomAge, int maxAge, Field field, 
        Location location, int foodValue, int maxLitterSize)
    {
        super(randomAge, maxAge, location, field);
        this.foodValue = foodValue;
        this.maxLitterSize = maxLitterSize;
        
        if (randomAge) {
            this.foodLevel = rand.nextInt(foodValue);
        }
        else{
            this.foodLevel = foodValue;
        }
       
       double probability = rand.nextDouble();
       if (probability < 0.5) {
           this.gender = Gender.MALE;
        } else {
           this.gender = Gender.FEMALE;
        }
    }
    
   /**
    * @return the type of food of this animal
    */
   public abstract Class getFoodType();
   
   /**
    * How this animal is acting
    */
   public abstract void act(List<Actor> newAnimals);
   
   /**
    * Check whether or not this animal is to give birth at this step.
    * New births will be made into free adjacent locations.
    */
   public abstract void giveBirth(List<Actor> newAnimals);
   
   /**
    * Randomly check if the animal is diagnosed with a disease
    * Diseases range from Rabies, Flu, or nothing
    */
   public void haveDisease()
   {
        int probability = rand.nextInt(2);
        
        if(probability == 0){
            this.disease = Disease.RABIES;
        }
        else if (probability == 1){
            this.disease = Disease.FLU;
        }
        else{
            this.disease = Disease.NOTHING;
        }
   }
    
   /**
    * @return the disease of the animal
    */
   public Disease getDisease()
   {
       return this.disease;
   }
    
   
   /**
    * Generates a disease of the animal
    * @return the disease of the animal
    */
   public Disease setDisease(Disease disease)
   {
       this.disease = disease;
       return this.disease;
   }
   
   /**
    * @return gender of animal
    */
   public Gender getGender()
   {
       return this.gender;
   }
    
   /**
    * An animal can breed if it has reached the breeding age.
    * @return true if the animal can breed 
    */
   public boolean canBreed()
   {
        return this.gender == Gender.FEMALE && age >= getBreedingAge() &&
            this.getField().isMaleAdjacent(this);
   }
    
   /**
    * Return the breeding age of this animal.
    * @return the breeding age of this animal.
    */
   protected abstract int getBreedingAge();
   
   /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
   public int breed()
   {
       int births = 0;
       if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(maxLitterSize) + 1;
       }
       return births;
   }
    
   /**
    * Make this fox more hungry. This could result in the fox's death.
    */
   public void incrementHunger()
   {
       foodLevel--;
       if(foodLevel <= 0) {
           this.setDead();
       }
   }
    
   /**
    * Make this fox less hungry.
    * Food level of the animal is added by adding the value of the food eaten 
    */
   public void decrementHunger()
   {
       this.foodLevel += this.foodValue;
   }
    
   /**
    * Find food in neighboring cells 
    * The food that the animal has eaten is set dead after
    * @param foodType The type of food that this animal eats
    * @return the location of food if found or null if isn't 
    */
   public <T extends Actor> Location findFood(Class<T> foodType)
   {
       Field field = getField();
       List<Location> adjacent = field.adjacentLocations(this.getLocation());
       Iterator<Location> it = adjacent.iterator();
       while(it.hasNext()) {
           Location where = it.next();
           Object animal = field.getObjectAt(where);
           if(animal != null && animal.getClass() == foodType ) {
               T food = (T) animal;
               if(food.isActive()) { 
                   food.setDead();
                   this.decrementHunger();
                   return where;
               }
           }
         return null;
       }
       return this.getLocation();
   }
    
   /**
    * Return the breeding Probability of this animal.
    * @return the breeding Probability of this animal.
    */
   public abstract double getBreedingProbability();
}