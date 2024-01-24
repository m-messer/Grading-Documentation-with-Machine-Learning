import java.util.List;

/**
 * Class to initialise new sub class of plant specifically, Grass.
 * Subclass of Plant which holds primary simulation data.
 *
 * @version 2022.02.27 (3)
 */
public class Grass extends Plant
{
    /**
     * Constructor for objects of class Grass (Holds default Plant values)
     * @param randomAge - boolean for random age generation
     * @param field - Field type, field plant is on
     * @param location - Location type, location on field of plant
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location,
        "Grass", //keyIdentifier
        new String[] {""}, //preyKeyIdentifiers
        0, //breedingAge
        1 * 300 * 2, //maxAge - years * days * dayunits
        0.8, //breedingProb
        2, //maxLitterSize
        9 * 2, //default_food_value
        10*2 //foodValue
        );
    }
    
    /**
     * Override function utilised to create child instances of subclass and append to Species.
     * @param newPlants - List type species for appending new plants to
     * @param randomage - Boolean type depicting random age preconfiguration
     * @param field - Field type depicting field plant is on.
     * @param location - Location type depicting location on field of plant.
     */
    public void createChild(List<Species> newAnimals, boolean randomAge, Field field, Location location)
    {
        newAnimals.add(new Grass(randomAge, field, location));
    }
}
