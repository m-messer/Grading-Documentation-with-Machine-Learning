import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * Defining time activities of simulation.
 *
 * @version 2020.2.22
 */
public class Time
{
    
    /**
     * Constract of time.
     */
    public Time()
    {
    }

    /**
     * The behavior of day time.All animal will be active at day time.
     * @param animals  List of animals in the field.
     */
    public void day(List<Animal> animals)
    {
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            
            animal.act(newAnimals);
          
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);
    
    }
    
    /**
     * The behavior of night time.Only vole will be active at day time.
     * @param animals  List of animals in the field.
     */
    public void night(List<Animal> animals)
    {
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            //Only the vole can act in the night
            if(animal instanceof Vole){
             animal.act(newAnimals);
            }
          
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);
    
    }
    
}
