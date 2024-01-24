import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of actors.
 *
 * @version 2020.02.20 (1)
 */
public abstract class Actor 
{
   // Current age of the actor
   private int age;
   // Maximum age of the actor
   private int maxAge;
   // Wheather the actor is alive
   private boolean alive;
   // Instance variable of class Location 
   private Location location;
   // Instance variable of class Actor
   private Actor actor;
   // Instance variable of Actor Field
   private Field field;
   // A shared random number generator to control breeding.
   public static final Random rand = Randomizer.getRandom();

    
   /**
     * Constructor for objects of class Actor
     * @param randomAge The age of the actor in the beggining of the simulation
     * @param location The location of the grass
     * @param field The field currently occupied
     */
   public Actor(boolean randomAge, int maxAge, Location location, Field field)
   {
       this.field = field;
       setLocation(location);
       this.alive = true;
       this.maxAge = maxAge;
       if (randomAge) {
            this.age = this.rand.nextInt(maxAge);
       }
        else{
            this.age = 0;
       }
   }
    
   /**
    * Check whether the animal is alive or not.
    * @return true if the animal is still alive.
    */
   public boolean isActive()
   {
       return alive;
   }    
    
   public abstract void act(List<Actor> actor);
   
   /**
    * Indicate that the animal is no longer alive.
    * It is removed from the field.
    */
   public void setDead()
   {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
   }
    
   public Random getRand() {
      return this.rand;  
   }
    
   public Field getField()
   {
      return field; 
   }
   
   public Location getLocation()
   {
       return location;
   }
    
   /**
    * Increase the age.
    * This could result in the animal's death.
    */
   public void incrementAge()
   {
       age++;
       if(age > getMaxAge()) {
           setDead();
       }
   }
    
   /**
    * Return the maximum age of this animal.
    * @return the maximum age of this animal.
    */
   public int getMaxAge()
   {
       return maxAge;
   }
   
   /**
    * Place the animal at the new location in the given field.
    * @param newLocation The animal's new location.
    */
    protected void setLocation(Location newLocation)
   {
       if(location != null) {
           field.clear(location);
       }
        
       location = newLocation;
       field.place(this, newLocation);
   }
}
