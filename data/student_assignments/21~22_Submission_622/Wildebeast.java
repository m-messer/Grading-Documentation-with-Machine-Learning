import java.util.List;

/**
 * Class to initialise new sub class of plant specifically, Wildebeast.
 * Subclass of Plant which holds primary simulation data.
 *
 * @version 2022.02.27 (3)
 */
public class Wildebeast extends Animal
{
    /**
     * Constructor for objects of class Wildebeast (Holds default Animal values)
     * @param randomAge - boolean for random age generation
     * @param field - Field type, field animal is on
     * @param location - Location type, location on field of animal
     */
    public Wildebeast(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location,
        "Wildebeast", //keyIdentifier
        new String[] {"Grass"}, //preyKeyIdentifiers
        10 * 2, //breedingAge
        50, //maxAge - years * days * dayunits
        0.8, //breedingProb
        8, //maxLitterSize
        10 * 2, //default_food_value
        true, //doesSleep
        5*2 //foodValue
        );
    }
    
    /**
     * Override function utilised to create child instances of subclass and append to Species.
     * @param newAnimals - List type species for appending new animals to
     * @param randomage - Boolean type depicting random age preconfiguration
     * @param field - Field type depicting field animal is on.
     * @param location - Location type depicting location on field of animal.
     */
    public void createChild(List<Species> newAnimals, boolean randomAge, Field field, Location location)
    {
        newAnimals.add(new Wildebeast(randomAge, field, location));
    }
}
