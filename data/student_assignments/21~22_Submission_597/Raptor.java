import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing Raptors. Objects of this class are able to hunt and breed.
 *
 * @version 2022-02-21
 */
public class Raptor extends Dinosaur{

    /**
     * The constructor for the Raptor class.
     * @param randomAge Determines whether a random age is given to the dinosaur when instantiated.
     * @param field The field that the object occupies.
     * @param location The location in the field that the object occupies.
     */
    public Raptor(boolean randomAge, Field field, Location location)
    {
        super(field, location, 0, 80, 0, 0.15, 5, 13, 0, 26, false);
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
        if (this.getFoodConsumed() <= 0.40 * this.getMaxFoodConsumed()){
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object organism = field.getObjectAt(where);
                if (organism instanceof Albertadromeus) {
                    Albertadromeus albertadromeus = (Albertadromeus) organism;
                    if (albertadromeus.isAlive()) {
                        int otherRaptorsClose = 0;
                        List<Location> otherRaptors = field.adjacentLocations(albertadromeus.getLocation());
                        Iterator<Location> itRaptors = otherRaptors.iterator();
                        while (itRaptors.hasNext() && otherRaptorsClose == 0) {
                            Object possibleRaptor = field.getObjectAt(itRaptors.next());
                            if (possibleRaptor instanceof Raptor && possibleRaptor != this) {
                                Raptor newRaptor = (Raptor) possibleRaptor;
                                albertadromeus.setDead();
                                if((this.getFoodConsumed() + (albertadromeus.getFoodValue() / 2)) > this.getMaxFoodConsumed()){
                                    this.setFoodConsumed(this.getMaxFoodConsumed());
                                }else {
                                    this.setFoodConsumed(this.getFoodConsumed() + (albertadromeus.getFoodValue() / 2));
                                }
                                if((this.getFoodConsumed() + (albertadromeus.getFoodValue() / 2)) > this.getMaxFoodConsumed()){
                                    newRaptor.setFoodConsumed(this.getMaxFoodConsumed());
                                }else {
                                    newRaptor.setFoodConsumed(this.getFoodConsumed() + (albertadromeus.getFoodValue() / 2));
                                }

                                otherRaptorsClose++;
                                return where;
                            }
                        }
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
        // New raptors are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if((organism instanceof Raptor) && ((Raptor) organism).getDinoSex() != this.getDinoSex()) {
                int births = breed();
                for (int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Raptor young = new Raptor(false, field, loc);
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

