import java.util.Iterator;
import java.util.List;

/**
 * A class representing shared characteristics of predators. Predators can eat preys.
 *
 * @version 2021.03.03
 */
public abstract class Predator extends Animal {
    public Predator(Field field, Location location, boolean isFemale, int breedingAge, int maxAge, double breedingProbability, int maxLitterSize, int maxHungerLevel, double creationProbability) {
        super(field, location, isFemale, breedingAge, maxAge, breedingProbability, maxLitterSize, maxHungerLevel, creationProbability);
    }

    /**
     * Search for neighbouring preys to eat. If no found, search for plants. If no found return null.
     * @return The location where food is available. If no food is found, a location of a plant is returned. If still nothing is found, return null.
     */

    public Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Object creature = field.getObjectAt(where);
            if (creature instanceof Prey) {
                Prey prey = (Prey) creature;
                if (prey.isAlive()) {
                    prey.setDead();
                    if((this.getHungerLevel() + prey.getNutritionalValue())>this.getMaxHungerLevel()){
                        this.setHungerLevel(this.getMaxHungerLevel());
                    }
                    else this.setHungerLevel(this.getHungerLevel() + prey.getNutritionalValue());
                    return where;
                }
            }

        }

        Iterator<Location> it2 = adjacent.iterator();

        while (it2.hasNext()) {
            Location where2 = it2.next();
            Object creature2 = field.getObjectAt(where2);
            if (creature2 instanceof Plant) {
                Plant plant = (Plant) creature2;
                if (plant.isAlive()) {
                    plant.setDead();
                    return where2;
                }
            }
        }
        return null;
    }
}
