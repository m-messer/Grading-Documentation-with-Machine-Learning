import java.util.List;
import java.util.Iterator;

/**
 * A simple model of Male
 *
 * @version 2022.03.01 (15)
 */
public class Male extends Gender
{
    /**
     * Create a male
     */
    public Male(int breeding_age,int breeding_end)
    {
        super(breeding_age,breeding_end);
    }

    /**
     * A male will find a female and breed.
     * @return the best movement to find the nearest female.
     */
    public Location behave(Animal animal,List<LivingThing> newAnimals)
    {

        if(canBreed(animal)){
            Field field =animal.getField();
            Location loc = animal.getLocation();
            Location targetLoc = nearestFemale(animal);
            if(targetLoc!=null){
                return field.shortestWayFirstStep(loc, targetLoc, animal.getHuntingDistance());
            }

        }

        return null;
    }

    /**
     * Search for a female within the hunting distance.
     * Only the first found female will be located.
     * @return the location of the nearest female.
     */
    private Location nearestFemale(Animal animal)
    {
        Field field = animal.getField();
        for(int i =1;i<=animal.getHuntingDistance();i++){
            List<Location> adjacent = field.adjacentLocations(animal.getLocation(),i);
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Animal mate = (Animal)field.getAnimalAt(where);
                if(mate!=null){
                    if(mate.getGender().canBreed(mate)){
                        if(mate.getGender() instanceof Female&& mate.getClass().equals(animal.getClass())) {
                            if(mate.canAct()) { 
                                return mate.getLocation();
                            }
                        }
                    }
                }

            }

        }
        return null;
    }
}
