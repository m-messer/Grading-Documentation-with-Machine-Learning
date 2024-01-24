import java.util.List;
/**
 * A simple model of a flower.
 * Flowers pollenate and die
 *
 * @version 2020.02.23
 */
public class Flower extends Plant
{
    // The rate at which the grass pollenates.
    public static final int POLLENATION_RATE = 1;
    // The age to which the grass can live.
    public static final int MAX_AGE = 15;
    // The likelihood of grass breeding
    private static final double BREEDING_PROBABILITY = 0.05;
    
    /**
     * Constructor for objects of class Flower
     * @param location The location of the flower
     * @param field The field currently occupied
     */
    public Flower(boolean randomAge, Location location, Field field)
    {
        super(randomAge , MAX_AGE, location, field);  
    }

    /**
     * @param newFlowers A list of newly created flowers
     */
    public void pollenate(List<Actor> newFlowers)
    {
      if(rand.nextDouble() <= BREEDING_PROBABILITY ){
          int pollenation = rand.nextInt(POLLENATION_RATE) + 1;
          
          // List of free adjacent locations
          List<Location> free = this.getField().getFreeAdjacentLocations(getLocation());
          
          for(int b = 0; b < pollenation && free.size() > 0; b++) {  
              Location loc = free.remove(0);
              //Initialise a new flower 
              Flower young = new Flower(false, loc, this.getField());
              //Add it to the list of new grass
              newFlowers.add(young);
          }
      }
    }
    
    /**
     * This is what a flower does most of the time:it pollenates
     * and might die of snow.
     * A flower pollenates more when the weather is sunny
     * @param newGrass A list to return new grass.
     */
    public void act(List<Actor> newFlowers)
    {
        this.incrementAge();
        
        if(isActive()){ 
             this.pollenate(newFlowers);
             
             // Die if the weather is snowy
             if(this.getField().getWeather() == Weather.SNOWY && rand.nextDouble() < 0.5){
                 this.setDead();
             }
         
             // Pollenate more when the weather is sunny
             if(this.getField().getWeather() == Weather.SUNNY){
                 this.pollenate(newFlowers);
             }
        }
    }

    /**
     * @return the max age that the flower can live to
     */
    public int getMaxAge()
    {
        return MAX_AGE;
    }
}
