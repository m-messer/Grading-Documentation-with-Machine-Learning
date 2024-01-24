import java.util.List;

/**
 * Class to initialise new sub class of plant specifically, Lion.
 * Subclass of Plant which holds primary simulation data.
 *
 * @version 2022.02.27 (3)
 */
public class Lion extends Animal
{
    /**
     * Constructor for objects of class Lion (Holds default Animal values)
     * @param randomAge - boolean for random age generation
     * @param field - Field type, field animal is on
     * @param location - Location type, location on field of animal
     */
    public Lion(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location,
        "Lion", //keyIdentifier
        new String[] {"Gazelle", "Zebra", "Wildebeast"}, //preyKeyIdentifiers
        5 * 2, //breedingAge
        150, //maxAge - years * days * dayunits
        0.5, //breedingProb
        7, //maxLitterSize
        20 * 2, //default_food_value - days * dayunits
        false, //doesSleep
        0 //foodValue
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
        newAnimals.add(new Lion(randomAge, field, location));
    }
}
