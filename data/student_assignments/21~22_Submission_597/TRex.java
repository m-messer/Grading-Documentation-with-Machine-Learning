import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing T.Rex. Objects of this class are able to hunt and breed.
 *
 * @version 2022-02-21
 */
public class TRex extends Dinosaur{

    /**
     * The constructor for the TRex class.
     * @param randomAge Determines whether a random age is given to the dinosaur when instantiated.
     * @param field The field that the object occupies.
     * @param location The location in the field that the object occupies.
     */
    public TRex(boolean randomAge, Field field, Location location)
    {
        super(field, location, 0, 130, 0, 0.13, 2, 15, 0, 40, true);
        if(randomAge){
            this.setAge(this.getRand().nextInt(this.getMaxAge()));
            Random randFood = new Random();
            this.setFoodConsumed(randFood.nextInt(this.getMaxFoodConsumed()));
        }else {
            this.setFoodConsumed((Integer)(this.getMaxFoodConsumed()/2));
        }
    }

    /**
     * A method which enables the dinosaur to find its prey.
     * @return Location the location at which the prey is located.
     */
    protected Location findFood() 
    {
        if (this.getFoodConsumed() <= 0.76 * this.getMaxFoodConsumed()){
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object organism = field.getObjectAt(where);
                if (organism instanceof Triceratops) {
                    Triceratops triceratops = (Triceratops) organism;
                    if (triceratops.isAlive()) {
                        triceratops.setDead();
                        if((this.getFoodConsumed() + triceratops.getFoodValue()) > this.getMaxFoodConsumed()){
                            this.setFoodConsumed(this.getMaxFoodConsumed());
                        }else {
                            this.setFoodConsumed(this.getFoodConsumed() + triceratops.getFoodValue());
                        }
                        return where;
                    }
                } 
                else if (organism instanceof Albertadromeus) {
                    Albertadromeus albertadromeus = (Albertadromeus) organism;
                    if (albertadromeus.isAlive()) {
                        albertadromeus.setDead();
                        if((this.getFoodConsumed() + albertadromeus.getFoodValue()) > this.getMaxFoodConsumed()){
                            this.setFoodConsumed(this.getMaxFoodConsumed());
                        }else {
                            this.setFoodConsumed(this.getFoodConsumed() + albertadromeus.getFoodValue());
                        }
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * A method which gives birth to other dinosaurs under the correct conditions.
     * @param newDinos A list to return newly born dinosaurs.
     */
    protected void giveBirth(List<Organism> newDinos) 
    {
        // New t.rexes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if((organism instanceof TRex) && ((TRex) organism).getDinoSex() != this.getDinoSex()) {
                int births = breed();
                for (int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    TRex young = new TRex(false, field, loc);
                    newDinos.add(young);
                }
            }
        }
    }

    /**
     * A method to determine the size of the litter that the dinosaur will produce.
     * @return int the number of children the dinosaur will have.
     */
    protected int breed() 
    {
        int births = 0;
        if(this.canBreed() && this.getRand().nextDouble() <= this.getBreedingChance()) {
            births = this.getRand().nextInt(this.getMaxLitterSize()) + 1;
        }
        return births;
    }
}
