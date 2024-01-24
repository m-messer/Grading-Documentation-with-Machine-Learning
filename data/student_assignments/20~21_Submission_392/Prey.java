import java.util.Iterator;
import java.util.List;

/**
 * A class representing shared characteristics of preys.
 * Preys eat plants and they can be eaten by predators
 *
 * @version 2021.03.03
 */
public abstract class Prey extends Animal {


    private final int nutritionalValue;

    public Prey(Field field, Location location, boolean isFemale, int breedingAge, int maxAge, double breedingProbability, int maxLitterSize, int maxHungerLevel, int nutritionalValue, double creationProbability) {
        super(field, location, isFemale, breedingAge, maxAge, breedingProbability, maxLitterSize, maxHungerLevel, creationProbability);
        this.nutritionalValue = nutritionalValue;
    }
    /**
     * Search for neighbouring plants to eat. If no found return null.
     * @return The location where food is available. If no food is found, return null.
     */

    @Override
    public Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object creature = field.getObjectAt(where);
            if(creature instanceof Plant) {
                Plant plant = (Plant) creature ;
                if(plant.isAlive()) {
                    plant.setDead();
                    if((this.getHungerLevel() + plant.getNutritionalValue())>this.getMaxHungerLevel()){
                        this.setHungerLevel(this.getMaxHungerLevel());
                    }
                    else this.setHungerLevel(this.getHungerLevel() + plant.getNutritionalValue());
                    return where;
                }
            }
        }
        return null;
    }
    public int getNutritionalValue() {
        return nutritionalValue;
    }
}
