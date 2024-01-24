import java.util.List;
import java.util.Random;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 2021.03.02
 */
public abstract class Animal implements Actor
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;

    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
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

    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Increase the age.
     * This could result in the animal's death.
     */
    protected void incrementAge()
    {
        int age = getAge();

        if(age > getMAX_AGE()) {
            setDead();
        }
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        Object animal = field.getObjectAt(location);
        int births = breed();
        if(animal instanceof Rabbit){
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Rabbit young = new Rabbit(false,field, loc);
                newAnimals.add(young);
            }
        }
        else if(animal instanceof Fox)
        {
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Fox young = new Fox(true, field, loc);
                newAnimals.add(young);
            }
        }
        else if(animal instanceof Wolf){
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Wolf young = new Wolf(true, field, loc);
                newAnimals.add(young);
            }
        }
        else if(animal instanceof Bear){
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Bear young = new Bear(true, field, loc);
                newAnimals.add(young);
            }
        }
        else if(animal instanceof Deer){
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Deer young = new Deer(false, field, loc);
                newAnimals.add(young);
            }
        }
    }

    /**
     * This method checks if the animal can breed and then checks all the adjacent locations
     * for other animals of the same gender that have reached the breeding age and that 
     * have a opsite gender.
     * 
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        List<Location> adjacent = getField().adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        if(canBreed() && getRandom().nextDouble() <= getBreedingProbability() ) 
        {
            while(it.hasNext() ) {
                Location where = it.next();
                String animalGender = getGender();
                Object animal = getField().getObjectAt(where);

                if(checkInstance(animal) && castAnimal(animal).canBreed()){

                    String adjacentAnimalGender  = castAnimal(animal).getGender();

                    if(!adjacentAnimalGender.equals(animalGender))
                    {
                        births = getRandom().nextInt(getMaxLitterSize()) + 1;
                       
                    }
                }
            }
        }

        return births;
    }

    /**
     * This method return the maximum age that an animal can reach.
     * @return int MAX_AGE
     */
    protected abstract int getMAX_AGE();

    /**
     * This method returns teh age of an animal.
     * @return int age
     */
    protected abstract int getAge();

    /**
     * This method cehecks if an animal has reached the breeding age and
     * returns a boolean value.
     * @return true if the animal hase reached the breeding age.
     * 
     */
    protected abstract boolean canBreed();

    /**
     * This method return the random factor created in each animal subclass.
     * @return Random rand
     */
    protected abstract Random getRandom();

    /**
     * This method returns the max litter size allowed for each animal.
     * @return int MAX_LITTER_SIZE
     */
    protected abstract int getMaxLitterSize();

    /**
     * This method returns the breeding probability of each animal.
     * @return BREEDING_PROBABILITY
     */
    protected abstract double getBreedingProbability();

    /**
     * This method returns a string that represents the gender of each animal.
     * @return String gender
     */
    protected abstract String getGender();

    /**
     * This method checks if the object animal is an instance of a particular type of animal.
     * @return boolean value
     */
    protected abstract boolean checkInstance(Object animal);

    /**
     * This method cast an object of type animal to a specific type of animal and
     * returns the object after it has been casted.
     * @return Animal animal
     */
    protected abstract Animal castAnimal(Object animal);
}
