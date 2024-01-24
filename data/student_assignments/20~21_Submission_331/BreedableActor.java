import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map;

/**
 * A class for actors that can breed (get pregnant and have children).
 *
 * @version 2021.03.01
 */

public abstract class BreedableActor extends Actor {
    // The time for which the actor is pregnant, as a proportion of its whole life.
    private final double PREGNANCY_LENGTH = 0.001;
    // The list of all possible male names.
    private static final ArrayList<String> MALE_NAMES = new ArrayList<>();
    // The list of all possible female names.
    private static final ArrayList<String> FEMALE_NAMES = new ArrayList<>();

    // Read in all male names into a list.
    {
        try {
            InputStream is = getClass().getResourceAsStream("male_names.txt");
            InputStreamReader isr = new InputStreamReader(is);
            // A reader object used to read names from a text file.
            BufferedReader br = new BufferedReader(isr);
            while (br.readLine() != null) {
                MALE_NAMES.add(br.readLine());
            }
            br.close();
        } catch (Exception ignored) {
        }
    }

    // Read in all female names into a list.
    {
        try {
            InputStream is2 = getClass().getResourceAsStream("female_names.txt");
            InputStreamReader isr2 = new InputStreamReader(is2);
            // A reader object used to read names from a text file.
            BufferedReader br2 = new BufferedReader(isr2);
            while (br2.readLine() != null) {
                FEMALE_NAMES.add(br2.readLine());
            }
            br2.close();
        } catch (Exception ignored) {
        }
    }

    // All possible desires that the actor can have.
    private final String[] DESIRES = new String[] {"move", "breed", "findFood", "rest"};
    // The actor's age.
    protected int age;
    // The highest age the actor can reach before dying.
    protected int maxAge;
    // The age the actor must reach to start breeding.
    protected int breedingAge;
    // How hungry the actor is.
    protected int hungerLevel;
    // The probability that the actor will breed.
    protected double breedingProbability;
    // The maximum number of children that the actor can have.
    protected int maxLitterSize;
    // The actor's sex (male or female).
    protected char sex;
    // The actor's randomly generated first name.
    protected String name;
    // Whether or not the actor should be generated with a random age.
    private final boolean randomAge;

    // The actor's brain used to compute it's desires from sensory inputs
    private NeuralNetwork brain;
    // Calculated values about the actor and it's environment
    private double[] sensoryInputs;
    // The desires, and how much they are desired, that the actor currently has.
    protected LinkedHashMap<String, Double> desires = new LinkedHashMap<>();

    // The actor's partner that they are breeding with.
    private BreedableActor partner;
    // How long the actor has been pregnant for.
    protected int pregnantTicks = 0;
    // A shared random number generator to control breeding.
    protected static final Random rand = Randomizer.getRandom();

    /**
     * Create a new breedable actor at location in field.
     *
     * @param species The actor's species.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge Whether or not the actor should be generated with a random age.
     */
    public BreedableActor(String species, Field field, Location location, Boolean randomAge) {
        super(species, field, location);
        age = 0;
        sex = rand.nextBoolean() ? 'm' : 'f';  // Randomly choose gender.
        this.randomAge = randomAge;
        name = getRandomName(sex);

        if (randomAge) {
            setBrain(new NeuralNetwork(5, 8, 4));
        }
    }

    /**
     * Get the sex of the breedable actor.
     *
     * @return The actor's sex.
     */
    public char getSex() {
        return sex;
    }

    /**
     * Set the breedable actor's maximum age and current age if random age is true.
     *
     * @param maxAge The highest age the actor can reach.
     */
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        if (randomAge) {
            age = rand.nextInt(maxAge);
        } else {
            age = 0;
        }
    }

    /**
     * Set the age the actor must reach to start breeding.
     *
     * @param breedingAge The age the actor must reach to breed.
     */
    public void setBreedingAge(int breedingAge) {
        this.breedingAge = breedingAge;
    }

    /**
     * Set the probability that the actor will breed.
     *
     * @param breedingProbability The probability that the actor will breed.
     */
    public void setBreedingProbability(double breedingProbability) {
        this.breedingProbability = breedingProbability;
    }

    /**
     * Set the maximum number of children that the actor can have.
     *
     * @param maxLitterSize The maximum number of children that the actor can have.
     */
    public void setMaxLitterSize(int maxLitterSize) {
        this.maxLitterSize = maxLitterSize;
    }

    public void setBrain(NeuralNetwork brain) {
        this.brain = brain;
    }

    /**
     * Get the longest the actor can last before dying.
     *
     * @return The longest the actor can last before dying.
     */
    public abstract int getMaxHungerLevel();

    /**
     * Let the breedable actor decide what to do and act upon this desire.
     *
     * @param newPrey A list to store possible newborn actors.
     * @param time The current time.
     */
    public void act(List<Actor> newPrey, Time time)
    {
        incrementAge();

        if (isAlive()) {
            // Give birth if pregnant
            if (pregnantTicks > 1) {
                pregnantTicks--;
            }
            if (pregnantTicks == 1) {
                giveBirth(newPrey);
                incrementHunger(20);
                pregnantTicks--;
            }
        }

        if(!time.isNight()) {
            incrementHunger();
            if (isAlive()) {
                // Compute desires

                // Calculate sensory inputs
                sensoryInputs = new double[] {
                        (double) age / maxAge,
                        sex == 'f' ? 1 : 0,
                        (double) hungerLevel / getMaxHungerLevel(),
                        pregnantTicks > 0 ? 1 : 0,
                        (double) time.getTimeInMinutes() / time.getDayLength()
                };

                // Generate actor's brains prediction
                List<Double> desiresProbabilities = brain.predict(sensoryInputs);

                // Put actor's desires into a HashMap
                HashMap<String, Double> desiresMap = new HashMap<>();
                for (int i = 0; i < desiresProbabilities.size(); i++) {
                    desiresMap.put(DESIRES[i], desiresProbabilities.get(i));
                }

                // Sort actor's desire descending using LinkedHashMap
                desires = desiresMap.entrySet().stream()
                                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                                .collect(
                                        LinkedHashMap::new,
                                        (map, item) -> map.put(item.getKey(), item.getValue()),
                                        Map::putAll);

                Location newLocation;
                for (String desire: desires.keySet()) {  // Act based on desire
                    switch (desire) {
                        case "move": {
                            newLocation = getField().freeAdjacentLocation(getLocation());
                            if (newLocation != null) {
                                setLocation(newLocation);
                                incrementHunger();
                            } else {
                                //setDead(); // overcrowding
                                return;
                            }
                            break;
                        }
                        case "findFood": {
                            newLocation = findFood();
                            if (newLocation != null) {
                                setLocation(newLocation);
                                rewardBrain(desire);
                                return;
                            }
                            break;
                        }
                        case "breed": {
                            if (sex == 'f' && canBreed()) {
                                rewardBrain(desire);
                                return;
                            }
                            break;
                        }
                        case "rest": {
                            return;
                        }
                    }
                    if (!isAlive()) {
                        return;
                    }
                }
            }
        }
    }

    /**
     * Increase the age, which can result in the breedable actor's death.
     */
    private void incrementAge()
    {
        age++;
        if (age > maxAge) {
            setDead();
        }
    }

    /**
     * Make this breedable actor more hungry, which can result in the actor's death.
     */
    private void incrementHunger(int amount)
    {
        hungerLevel = hungerLevel - amount;
        if (hungerLevel <= 0) {
            setDead();
        }
    }

    /**
     * Increment the breedable actor's hunger by 1.
     */
    private void incrementHunger() {
        incrementHunger(1);
    }

    /**
     * Make the breedable actor look for food around itself and eat the first one it finds.
     *
     * @return The location of the actor that was just eaten.
     */
    private Location findFood()
    {
        if (hungerLevel >= getMaxHungerLevel()) {
            return null;
        }
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
            Actor actor = (Actor) field.getObjectAt(where);
            if (actor != null && foodSources.contains(actor.getSpecies())) {
                if (actor instanceof Plant) {
                    Plant plant = (Plant) actor;
                    if (plant.isAlive()) {
                        plant.setDead();
                        hungerLevel += plant.getFoodLevel();
                        return where;
                    }
                } else if (actor instanceof Prey) {
                    Prey prey = (Prey) actor;
                    if (prey.isAlive()) {
                        prey.setDead();
                        hungerLevel += prey.getFoodLevel();
                        return where;
                    }
                }

            }
        }
        return null;  // No food found.
    }


    /**
     * Check whether or not this actor is to give birth at this step. New births will be made into free adjacent locations.
     *
     * @param newActors A list to return newly born actors.
     */
    protected void giveBirth(List<Actor> newActors)
    {
        // New breedable actors are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);

            // Create young
            BreedableActor young = createYoung(field, loc);

            // Crossover parents' brains to generate child brain
            NeuralNetwork youngBrain = brain.crossover(partner.brain);
            youngBrain.mutate();
            young.setBrain(youngBrain);

            newActors.add(young);
        }
    }

    /**
     * Create a newborn breedable actor with the correct characteristics.
     *
     * @param field The field currently occupied.
     * @param loc The location within the field.
     * @return The newborn breedable actor.
     */
    protected abstract BreedableActor createYoung(Field field, Location loc);

    /**
     * Generate a number representing the number of births, if it can breed.
     *
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        if (rand.nextDouble() <= breedingProbability) {
            return rand.nextInt(maxLitterSize) + 1;
        } else {
            return 0;
        }
    }

    /**
     * Check whether the breedable actor is able to breed in the current location.
     *
     * @return true if the actor can breed, false otherwise.
     */
    private boolean canBreed()
    {
        if (age < breedingAge) {
            return false;
        }

        if (sex == 'f') {
            if (pregnantTicks == 0) {
                Field field = getField();
                List<Location> nearby = field.adjacentLocations(getLocation());
                List<Actor> nearbyActors = new ArrayList<>();

                // Find nearby actors
                nearby.forEach(loc -> {
                    Actor nearbyActor = (Actor) field.getObjectAt(loc);
                    if (nearbyActor != null) {
                        nearbyActors.add(nearbyActor);
                    }
                });

                // Find if theres a male actor of same species which can breed
                BreedableActor availableActor = (BreedableActor) nearbyActors.stream().filter(actor ->
                        actor.species.equals(species) &&
                                ((BreedableActor) actor).getSex() == 'm' &&
                                ((BreedableActor) actor).canBreed()
                ).findFirst().orElse(null);
                if (availableActor != null) {
                    partner = availableActor;
                    pregnantTicks = (int) (maxAge * PREGNANCY_LENGTH);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Get a random name for the breedable actor.
     *
     * @param sex The sex ('f' or 'm') of the breedable actor.
     * @return A random name corresponding to the sex.
     */
    public String getRandomName(char sex) {
        if(sex == 'f') {
            return FEMALE_NAMES.get(new Random().nextInt(FEMALE_NAMES.size()));
        } else {
            return MALE_NAMES.get(new Random().nextInt(MALE_NAMES.size()));
        }
    }

    /**
     * Reward the actor's neural network by training the current input values with wanted outputs.
     * Wanted outputs is an array where the value referenced by the index of desireToIncreaseCertainty
     * is 1, and all other desires are 0.
     *
     * @param desireToIncreaseCertainty desire to reward
     */
    private void rewardBrain(String desireToIncreaseCertainty) {
        // Get index of desire to increase certainty
        int indexToIncreaseCertainty = Arrays.asList(DESIRES).indexOf(desireToIncreaseCertainty);

        // Create a new array of expected outputs
        double[] wantedOutputValues = new double[DESIRES.length];
        wantedOutputValues[indexToIncreaseCertainty] = 1.0;

        // Train actors neural network with current sensory inputs and wanted outputs
        brain.train(sensoryInputs, wantedOutputValues);
    }

}
