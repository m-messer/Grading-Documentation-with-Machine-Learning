import java.util.*;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals and plants
 *
 * @version 2021.03.03
 */
public class Simulator {
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // List of Creatures in the field.
    private List<Creature> creatures;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // A list of all creature types.
    ArrayList<Creature> creatureTypes = new ArrayList<>();
    private Time clock;
    private static final Random rand = Randomizer.getRandom();
    private Weather weather = new Weather();
    //the current weather of the simulation.
    private String currentWeather;
    private Disease disease;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        creatures = new ArrayList<>();
        field = new Field(depth, width);
        Location loc = new Location(0, 0);

        // Add all the creature types to the Array.
        creatureTypes.add(new Tiger(false, field, loc));creatureTypes.add(new Monkey(false, field, loc));
        creatureTypes.add(new Leopard(false, field, loc));
        creatureTypes.add(new Capybara(false, field, loc));
        creatureTypes.add(new Sloth(false, field, loc));
        creatureTypes.add(new Plant(false, field, loc));


        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Tiger.class, Color.ORANGE);
        view.setColor(Capybara.class, Color.BLUE);
        view.setColor(Leopard.class, Color.yellow);
        view.setColor(Monkey.class, Color.darkGray);
        view.setColor(Plant.class, Color.GREEN);
        view.setColor(Sloth.class, Color.RED);

        // Add the weather options of the simulation along with their weight.
        weather.addWeather("Rain", 5);
        weather.addWeather("Fog", 4);
        weather.addWeather("Poisonous gas", 1);
        weather.addWeather("Sunny", 10);

        currentWeather = "Sunny";


        clock = new Time(4);

        disease= new Disease(0.01,10);

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation() {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps) {
        for (int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Increment the time and every day randomise a new weather condition.
     * Iterate over the whole field updating the state of creature.
     */
    public void simulateOneStep() {
        step++;
        clock.incrementTime();
        if (step % clock.getUnitPerDay() == 0) {
            currentWeather = weather.randomiseWeather();
            reactToWeather(currentWeather);
        }

        // Provide space for newborn creatures.
        List<Creature> newCreatures = new ArrayList<>();
        // Let all rabbits act.
        Iterator<Creature> it = creatures.iterator();
        while (it.hasNext()) {
            List<Creature> newCreaturesTemp = new ArrayList<>();
            Creature creature = it.next();
            creature.act(newCreaturesTemp, isDayTime());
            infect(creature);
            if (!creature.isAlive()) {
                it.remove();
            }
            newCreatures.addAll(newCreaturesTemp);
        }

        // Add the newly born foxes and rabbits to the main lists.
        creatures.addAll(newCreatures);

        view.showStatus(step, field);
        view.showWeather(currentWeather);
        view.showTime(clock);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        creatures.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with creatures.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();


        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                boolean alreadyPopulated = false;
                for (Creature CreatureType : creatureTypes) {
                    if (!alreadyPopulated) {
                        if (rand.nextDouble() <= CreatureType.getCreationProbability()) {
                            Location location = new Location(row, col);
                            Creature newCreature = CreatureType.getCreature(true, field, location);
                            creatures.add(newCreature);
                            alreadyPopulated = true;
                        }
                    }
                }
            }
            // else leave the location empty.
        }
    }


    /**
     * Pause for a given time.
     *
     * @param millisec The time to pause for, in milliseconds
     */
    private void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ie) {
            // wake up
        }
    }

    /**
     * Returns whether or not it is day time.
     *
     * @return true if the time is at the middle of the day, false otherwise
     */
    private Boolean isDayTime() {
        return (clock.whatIsTheTime() >= 6 && clock.whatIsTheTime() <18 );
    }

    /**
     * Infects a creature with a given probability.
     * @param creature the creature getting infected
     */
    private void infect(Creature creature) {
        if (disease.isInfected()) {
            creature.setInfected(true);
            creature.setTimeUntilDeath(disease.getTimeUntilDeath());
        }
    }

    /**
     * Certain weather conditions trigger responses from creatures.
     * @param weather the current weather
     */
    private void reactToWeather(String weather) {
        switch (weather) {
            case "Rain": { //When raining, plants grow an extra step
                List<Creature> newCreatures = new ArrayList<>();
                // Let all creatures act.
                Iterator<Creature> it = creatures.iterator();
                while (it.hasNext()) {
                    List<Creature> newCreaturesTemp = new ArrayList<>();
                    Creature creature = it.next();
                    if (creature instanceof Plant) {
                        creature.act(newCreaturesTemp, isDayTime());
                        if (!creature.isAlive()) {
                            it.remove();
                        }
                        newCreatures.addAll(newCreaturesTemp);
                    }
                }
                // Add the newly born creatures to the main lists.
                creatures.addAll(newCreatures);
                break;
            }
            case "Fog": { //when foggy, preys move an extra step
                List<Creature> newCreatures = new ArrayList<>();

                Iterator<Creature> it = creatures.iterator();
                while (it.hasNext()) {
                    List<Creature> newCreaturesTemp = new ArrayList<>();
                    Creature creature = it.next();
                    if (creature instanceof Prey) {
                        creature.act(newCreaturesTemp, isDayTime());
                        if (!creature.isAlive()) {
                            it.remove();
                        }
                        newCreatures.addAll(newCreaturesTemp);
                    }
                }
                creatures.addAll(newCreatures);
                break;

            }
            case "Poisonous gas": { // When poisonous gas, animals die with a given probability
                Iterator<Creature> it = creatures.iterator();
                while (it.hasNext()) {
                    Creature creature = it.next();
                    if (creature instanceof Animal) {
                        if (rand.nextInt(100) < 5) {
                            creature.setDead();
                        }
                    }


                }
                break;
            }
            default:
                break;

        }
    }

}




