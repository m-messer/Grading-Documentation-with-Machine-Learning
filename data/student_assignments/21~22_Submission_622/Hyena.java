import java.util.List;

/**
 * Class to initialise new sub class of plant specifically, Hyena.
 * Subclass of Plant which holds primary simulation data.
 *
 * @version 2022.02.27 (3)
 */
public class Hyena extends Animal
{
    /**
     * Constructor for objects of class Hyena (Holds default Animal values)
     * @param randomAge - boolean for random age generation
     * @param field - Field type, field animal is on
     * @param location - Location type, location on field of animal
     */
    public Hyena(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location,
        "Hyena", //keyIdentifier
        new String[] {"Wildebeast"}, //preyKeyIdentifiers
        4 * 2, //breedingAge
        150, //maxAge - years * days * dayunits
        0.5, //breedingProb
        3, //maxLitterSize
        30 * 2, //default_food_value
        false, //doesSleep
        10*2 //foodValue
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
        newAnimals.add(new Hyena(randomAge, field, location));
    }
}
