import java.util.List;

/**
 * A simple model of grass 
 * Grass pollenate and die
 *
 * @version 2020.02.23
 */
public class Grass extends Plant
{
    // The rate at which the grass pollenates.
    private static final int POLLENATION_RATE = 3; 
    // The age to which the grass can live.
    private static final int MAX_AGE = 4;
    // The likelihood of grass breeding
    private static final double BREEDING_PROBABILITY = 0.14;
    
    /**
     * Constructor for objects of class Grass
     * @param location The location of the grass
     * @param field The field currently occupied
     */
    public Grass(boolean randomAge, Location location, Field field)
    {
        super(randomAge, MAX_AGE, location, field);
    }

    /**
     * Grass pollenates to produce more grass if the neighboring cell was empty
     * @param newGrass A list of new grass
     */
    public void pollenate(List<Actor> newGrass)
    {
        if (rand.nextDouble() <= BREEDING_PROBABILITY){
            int pollenation = this.getRand().nextInt(POLLENATION_RATE) + 1;
            
            // List of free adjacent locations
            List<Location> free = this.getField().getFreeAdjacentLocations(getLocation());
            
            for(int b = 0; b < pollenation && free.size() > 0; b++) {  
                Location loc = free.remove(0);
                //Initialise a new grass 
                Grass young = new Grass(false, loc, this.getField());
                //Add it to the list of new grass
                newGrass.add(young);
             }
        }
    }
    
    /**
     * This is what the grass does most of the time:it pollenates
     * and might die of snow.
     * Grass pollenates more when the weather is sunny
     * @param newGrass A list to return new grass.
     */
    public void act(List<Actor> newGrass)
    {
        this.incrementAge();
        
        if(isActive()){
          this.pollenate(newGrass);
          
          // Die if the weather is snowy
          if(this.getField().getWeather() == Weather.SNOWY && rand.nextDouble() < 0.5){
             this.setDead();
          }
         
          // Pollenate more when the weather is sunny
          if(this.getField().getWeather() == Weather.SUNNY){
             this.pollenate(newGrass);
          }
        }
    }
    
    /**
     * @return the max age of grass
     */
    public int getMaxAge(){
        return MAX_AGE;
    }
}
