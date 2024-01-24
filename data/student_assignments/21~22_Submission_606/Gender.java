import java.util.Random;
import java.util.List;
import java.util.Iterator;
/**
 * A class representing shared characteristics of gender
 *
 * @version 2022.03.01 (15)
 */
public abstract class Gender
{
    private int breeding_age ;
    private int breeding_end ;
    // A shared random number generator to control breeding.
    protected static final Random rand = Randomizer.getRandom();

    /**
     * Create a gender
     * @param breeding_age The age the animal can breed.
     * @param breeding_end The maximum age the animal can breed. After this age, the animal cannot breed.
     */
    public Gender(int breeding_age,int breeding_end)
    {
        this.breeding_age = breeding_age;
        this.breeding_end = breeding_end;
    }

    /**
     * @return true if the animal's age is between breeding_age and breeding_end.
     */
    protected boolean canBreed(Animal animal)
    {
        return animal.getAge() >= breeding_age && animal.getAge()<=breeding_end;
    }

    /**
     * @return true if the animal has same type and different gender animal in the adjacent grid.
     */
    protected boolean isOppositeSexNearBy(Animal animal)
    {
        Field field = animal.getField();
        List<Location> adjacent = field.adjacentLocations(animal.getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Animal mate = (Animal)field.getAnimalAt(where);
            if(mate!=null){
                if(animal.getGender() instanceof Male){
                    if(mate.getGender().canBreed(mate)){
                        if(mate.getGender() instanceof Female&& mate.getClass().equals(animal.getClass())) {
                            if(mate.canAct()) { 
                                return true;
                            }
                        }
                    }
                }
                else{
                    if(mate.getGender().canBreed(mate)){
                        if(mate.getGender() instanceof Male&& mate.getClass().equals(animal.getClass())) {
                            if(mate.canAct()) { 
                                return true;
                            }
                        }
                    }
                }

            }

        }
        return false;
    }

    /**
     * Different gender has its own behaviour that will be determined in the subclass.
     * @return the next movement for the animal.
     */
    public abstract Location behave(Animal animal,List<LivingThing> newAnimals)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException;

}
