import java.util.Random;
import java.util.List;
import java.util.Iterator;
/**
 * A simple model of disease
 *
 * @version 2022.03.01 (15)
 */
public class Disease
{
    private static final double KILLING_PROBABILITY =0.002;
    private static final double ILL_PROBABILITY =0.01;
    private static final double TRANSMITTION_PROBABILITY = 0.0005;
    private static final Random rand = Randomizer.getRandom();
    /**
     * Constructor for objects of class Disease
     */
    public Disease()
    {

    }

    /**
     * A disease action. That is to transmit,to make an animal ill, to kill a living thing.
     */
    public void act(LivingThing thing)
    {

        if(thing.canAct()&&thing instanceof Animal){
            Animal affectedAnimal = (Animal)thing;
            List<Location> adjacent = affectedAnimal.getField().adjacentLocations(affectedAnimal.getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Animal animal = affectedAnimal.getField().getAnimalAt(where);
                if(animal!=null&&rand.nextDouble()<=TRANSMITTION_PROBABILITY){
                    animal.setDisease(this);
                }

            }

            if(rand.nextDouble()<=ILL_PROBABILITY){
                affectedAnimal.decrementHuntingDistance();
            }
        }

        if(rand.nextDouble()<=KILLING_PROBABILITY){
            thing.setDead();
        }

    }
}