import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing Albertadromeus. Objects of this class are able to hunt and breed.
 *
 * @version 2022-02-23
 */
public class Albertadromeus extends Dinosaur{
    /**
     * The constructor for the Albertadromeus class.
     * @param randomAge Determines whether a random age is given to the dinosaur when instantiated.
     * @param field The field that the object occupies.
     * @param location The location in the field that the object occupies.
     */
    public Albertadromeus(boolean randomAge, Field field, Location location)
    {
        super(field, location, 0, 120, 26, 0.18, 4, 10, 0, 34, true);
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
    protected Location findFood() {
        if (this.getFoodConsumed() <= 0.3 * this.getMaxFoodConsumed()) 
        {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object organism = field.getObjectAt(where);
                if (organism instanceof Cycad) {
                    Cycad cycad = (Cycad) organism;
                    if (cycad.isAlive()) {
                        cycad.setDead();
                        if((this.getFoodConsumed() + cycad.getFoodValue()) > this.getMaxFoodConsumed()){
                            this.setFoodConsumed(this.getMaxFoodConsumed());
                        }else {
                            this.setFoodConsumed(this.getFoodConsumed() + cycad.getFoodValue());
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
        // New albertadromeus are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if((organism instanceof Albertadromeus) && ((Albertadromeus) organism).getDinoSex() != this.getDinoSex()) {
                int births = breed();
                for (int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Albertadromeus young = new Albertadromeus(false, field, loc);
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
