import java.util.*;
import java.awt.*;

/**
 * The animal class. It is a child class of Organism and defines the basic
 * fields and methods for all animals.
 *
 */
public abstract class Animal extends Organism {

    // Whether this animal is female or male.
    private final boolean isFemale;
    // The current food level;
    private int foodLevel = foodValue();
    // The offsprings the animal gave birth.
    public final ArrayList<Animal> offsprings = new ArrayList();
    // Whether this animal has caught the STD.
    private boolean hasSTD;

    /**
     * Create an Animal with a specific point.
     * 
     * @param point The point on the habitat.
     * @param adult Whether this animal should be created as an adult or new born.
     */
    protected Animal(Point point, boolean adult) {
        super(point);
        
        Random random = Helper.getRandom();
        isFemale = random.nextDouble() < 0.5 ? true : false;
        hasSTD = random.nextDouble() < stdProbability() ? true : false;
        
        if(adult) {
            setAge(pubertyAge());
        }
    }

    /**
     * Let the animal act on its neighbors.
     * 
     * @param neighbors The neighors of the organism.
     */
    public void actOn(Map<Point, Organism> neighbors) {
        offsprings.clear();
        Map<Point, Organism> mates = new HashMap<Point, Organism>();
        Map<Point, Organism> preys = new HashMap<Point, Organism>();
        Map<Point, Organism> emptyCell = new HashMap<Point, Organism>();

        for (Point point : neighbors.keySet()) {
            Organism neighbor = neighbors.get(point);

            if (neighbor == null) {
                emptyCell.put(point, neighbor);
                break;
            }

            if (neighbor instanceof Animal && canReproduceWith((Animal) neighbor))
                mates.put(point, neighbor);

            if (canEat(neighbor))
                preys.put(point, neighbor);

            if (neighbor instanceof Grass || !neighbor.isAlive())
                emptyCell.put(point, neighbor);
        }

        if (!mates.isEmpty() && !emptyCell.isEmpty()) {
            reproduce(emptyCell.keySet());
            if (hasSTD)
                for (Organism mate : mates.values()) {
                    ((Animal) mate).infected();
                }

        } else if (!preys.isEmpty() && this.foodLevel < foodValue())
            hunt((Organism) Helper.randomObjectFrom(preys.values()));

        else if (!emptyCell.isEmpty())
            point().setLocation((Point) Helper.randomObjectFrom((Collection<Point>) emptyCell.keySet()));

        else
            setDead();

        if (naturalDeath())
            setDead();
    }

    /**
     * Infect the animal with the STD.
     * 
     */
    public void infected() {
        this.hasSTD = true;
    }

    /**
     * Eat an organism and move to its location.
     * 
     * @param prey The organism to be eaten.
     */
    private void hunt(Organism prey) {
        prey.setDead();
        point().setLocation(prey.point());
        foodLevel += this.foodValue();

        if (prey instanceof Animal && ((Animal) prey).hasSTD())
            this.infected();
    }

    /**
     * Gets whether this animal would die naturally.
     * 
     * @return true if it dies, false if not.
     */
    @Override
    protected boolean naturalDeath() {
        incrementAge();

        if (hasSTD())
            foodLevel = foodLevel / 5;
        else
            foodLevel--;

        return age() > maxAge() || foodLevel <= 0;
    }

    /**
     * Gets whether this animal could reproduce with another.
     * 
     * @param animal The animal to reproduce with.
     * 
     * @return true if it could, false if not.
     */
    private boolean canReproduceWith(Animal animal) {
        return isFemale && animal.getClass().equals(this.getClass()) && !animal.isFemale()
                 && age() >= pubertyAge();
    }

    /**
     * Reproduce and give birth to offsprings at given locations.
     * 
     * @param emptyPoints The locations to place the offsprings.
     */
    private void reproduce(Collection<Point> emptyPoints) {
        int counter = 0;
        
        for (Point point : emptyPoints) {
            if (counter > maxOffspring())
                break;

            if (Helper.getRandom().nextDouble() < this.reproductionProbability()) {
                this.offsprings.add(makeOffspring(point));
                counter++;
            }
        }
    }
    
    /**
     * Gets whether this animal could eat an organism.
     * 
     * @param organism The organism to be eaten.
     * 
     * @return true if it could, false if not.
     */
    private boolean canEat(Organism organism) {
        return Arrays.stream(foodSource()).anyMatch(food -> food.equals(organism.getClass()));
    }

    /**
     * Gets whether this animal is female or male.
     * 
     * @return true if it is female, false if male.
     */
    public boolean isFemale() {
        return isFemale;
    }

    /**
     * Gets whether this animal is infected.
     * 
     * @return true if it has a STD, false if not.
     */
    public boolean hasSTD() {
        return hasSTD;
    }

    /**
     * Gets an offsring of this animal.
     * 
     * @param point The location of the offspring.
     * 
     * @return The offsring.
     */
    abstract protected Animal makeOffspring(Point point);

    /**
     * Gets the maximum number of offsprings this animal could have at once. 
     * 
     * @return The max amount of offsping.
     */
    abstract int maxOffspring();
    
    /**
     * The age that this animal must reach before it could reproduce.
     * 
     * @return The age of puberty.
     */
    abstract int pubertyAge();

    /**
     * Gets the level of food this animal could get from each prey.
     * 
     * @return The max food level.
     */
    abstract int foodValue();

    /**
     * Gets the preys of this animal.
     * 
     * @return An array of classes for the species that could be eaten. 
     */
    abstract Class[] foodSource();

    /**
     * Gets whether this animal is active at night or day
     * 
     * @return true if active at night, false if active at day.
     */
    abstract boolean nocturnal();

    /**
     * Gets the probability that this animal could reproduce.
     * 
     * @return the probability.
     */
    abstract double reproductionProbability();

    /**
     * Gets the probability that this animal would catch a STD on instantiation.
     * 
     * @return the probability.
     */
    abstract double stdProbability();
}
