package src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2021.03.03
 */
abstract public class Animal extends Actor
{
    // A boolean array representing whether the animal is awake or not for every hour interval in our simulation
    // Currently the intervals are set to 00-06, 06-12, 12-18 and 18-00
    protected boolean[] isAwake;

    // Maximum starting food level for an animal and the maximum amount of food it can store
    private static final int MAX_FOOD_LEVEL = 55;
    // The animal's stats
    private final Stats stats;
    // Is true if the animal is infected by a disease
    public boolean infected;
    // The duration of the animal's infection in simulation steps
    private int infCounter;
    // Default disease duration
    private static final int DISEASE_DURATION = 10;
    // Is true if the animal has been infected before
    private boolean wasInfected;

    // What this animal eats
    private HashSet<Class<?>> food;

    // The animal's age.
    private int age;
    // The animal's food level, which is increased by eating.
    private int foodLevel;
    // A cooldown which keeps track of the amount of steps needed before this animal can breed again
    private int breedCooldown;
    // The gender of the animal, true for male and false for female
    private final boolean gender;

    /**
     * Create a new animal at location in field.
     *
     * @param randomAge If true, the animal will be allocated random age and food level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param stats The stats of the animal, every species having different ones.
     */
    public Animal(boolean randomAge, Field field, Location location, Stats stats)
    {
        super(field, location);
        gender=Randomizer.getRandom().nextBoolean();
        this.stats = stats;
        breedCooldown=0;
        if(randomAge) {
            age = rand.nextInt(stats.getStat(StatTypes.MAX_AGE));
            foodLevel = rand.nextInt(MAX_FOOD_LEVEL);
        }
        else{
            age = 0;
            foodLevel = MAX_FOOD_LEVEL;
        }
        infected = false;
        wasInfected = false;
    }

    /**
     * Sets the isAwake array of this instance.
     * @param isAwake The animal's sleeping schedule.
     */
    protected void setIsAwake(boolean[] isAwake){
        this.isAwake = isAwake;
    }

    /**
     * @return True if this animal is a male or false if it's female.
     */
    public boolean getGender(){
        return gender;
    }

    /**
     * Sets the species of animals this animal eats.
     * @param food The animal's food list (what it can and will eat).
     */
    protected void setFood(HashSet<Class<?>> food){
        this.food = food;
    }

    /**
     * Finds an available location to move to.
     * @return The location to which to move, chosen randomly
     */
    private Location moveRandomly(){
        return this.getField().freeAdjacentLocation(this.getLocation());
    }

    /**
     * Implements core actions for animals, such as hunting for food or for mates, as well as eating, breeding and aging.
     * @param newAnimals Newborn animals to be added into the world.
     */
    @Override
    public void act(List<Actor> newAnimals){
        breedCooldown--;
        if(isAwake[getField().getTimeOfDay()/6]) {
            Location newLoc = null;
            //newLoc = super.getField().randomAdjacentLocation(super.getLocation());
            if (foodLevel * 2 < MAX_FOOD_LEVEL)
                newLoc = seekFood();
            else if (canBreed())
                newLoc = seekMate();
            if (newLoc == null) {
                newLoc = moveRandomly();
            }
            if (newLoc != null)
                super.move(newLoc);
            if (!eat())
                giveBirth(newAnimals);
        }
        incrementAge();
        incrementHunger();
        if(infected && isAlive())
            disease();
    }

    /**
     * While the animal is hungry, it will eat whatever is in its diet and in the same location.
     * @return True if the animal ate.
     */
    private boolean eat(){
        boolean result = false;
        Animal an;
        Plant pl;
        ArrayList<Actor> toDie = new ArrayList<>();
        if(foodLevel * 2 < MAX_FOOD_LEVEL){{
                for(Actor act : getField().getActorsAt(getLocation())) {
                    //System.out.println(act.getClass());
                    if (act != null && foodLevel < MAX_FOOD_LEVEL && food.contains(act.getClass())) {
                        if (act instanceof Animal) {
                            an = (Animal) act;
                            foodLevel += an.getStats().getStat(StatTypes.NUTRITIONAL_VALUE);
                            toDie.add(act);
                        } else if (act instanceof Plant) {
                            pl = (Plant) act;
                            foodLevel += pl.isEaten(stats.getStat(StatTypes.BITE));
                            if(pl.getGrowth()<=0)
                                toDie.add(pl);
                        }
                        result = true;
                    }
                }
            }
            for(Actor dead : toDie){
                dead.setDead();
            }
            if(foodLevel>MAX_FOOD_LEVEL)
                foodLevel=MAX_FOOD_LEVEL;
        }

        return result;
    }


    /**
     * Points the animal to a food source if it is in its range of sight.
     * @return The location to which to move to in order to be closer to a food source.
     */
    private Location seekFood(){
        return see(food);
    }

    /**
     * Points the animal to the closest member of the same species of the opposite gender in order to mate.
     * @return The location to which to move to in order to be closer to a mate.
     */
    private Location seekMate(){
        HashSet<Class<?>> ownSpecies = new HashSet<>();
        ownSpecies.add(this.getClass());
        return see(ownSpecies);
    }

    /**
     * A searching algorithm based on Lee's algorithm which searches around the animal in it's vision field.
     * @param seeked The classes the animal should search for.
     * @return The location to which to move to in order to be closer to what the animal searched for.
     */
    private Location see(HashSet<Class<?>> seeked){
        sLoc best = null;
        boolean genderDoesNotMatter=true;
        if(seeked.contains(this.getClass()))
            genderDoesNotMatter = false;

        Stack<sLoc> open = new Stack<>();
        HashSet <Location> closed = new HashSet<>();

        open.push(new sLoc(super.getLocation(),0,null));
        closed.add(super.getLocation());

        sLoc now;

        sLoc next;

        while(!open.isEmpty() && best==null){

            now = open.pop();
            if(now.l!=null && now.d!=0) {
                for (Actor act : now.l.getActors()) {
                    if (seeked.contains(act.getClass()) &&
                            (genderDoesNotMatter || ((Animal) act).getGender()!=this.getGender())) {
                        best = now;
                    }
                }
            }

            if(now.d < stats.getStat(StatTypes.SIGHT) + getLocation().getWeather().getSightAffection()){
                for(Location loc : super.getField().adjacentLocations(now.l)){
                    if(!closed.contains(loc)){
                        next = new sLoc(loc,now.d+1,now);
                        closed.add(loc);
                        open.push(next);
                    }
                }
            }
        }

        if(best != null) {
            while(best.d>1)
                best = best.bef;
            if(best.l!=null)
                return best.l;
        }
        return null;
    }

    /**
     * Increase the age.
     * This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > stats.getStat(StatTypes.MAX_AGE)) {
            setDead();
        }
    }

    /**
     * @return The animal's stats.
     */
    protected Stats getStats(){
        return stats;
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextInt(100) <= stats.getStat(StatTypes.BREEDING_PROBABILITY)) {
            births = rand.nextInt(stats.getStat(StatTypes.MAX_LITTER_SIZE)) + 1;
        }
        if(births>0)
            breedCooldown=15;
        return births;
    }

    /**
     * @return The age of the animal.
     */
    public int getAge(){
        return age;
    }

    /**
     * A rabbit can breed if it has reached the breeding age.
     * @return true if the rabbit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= stats.getStat(StatTypes.BREEDING_AGE) && breedCooldown<=0;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    private void giveBirth(List<Actor> newAnimals)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        boolean foundMate=false;
        for(Actor act : getLocation().getActors())
            if(act.getClass()==this.getClass() && ((Animal) act).getGender()!=this.getGender())
                foundMate=true;
        if(foundMate) {
            int births = breed();
            for (int b = 0; b < births; b++)
                newAnimals.add(spawnAnimal(getLocation()));
        }
    }

    /**
     * Shortens the amount of time left for the animal to be infected.
     * Also infects animals of the same species in the vicinity.
     */
    private void disease(){
        if(infCounter==0) {
            infected = false;
            //treated
            //revert infection
        }
        else {
            infCounter--;
            foodLevel--;
            for(Location loc : getField().adjacentLocations(getLocation())){
                for(Actor act : loc.getActors()){
                    if(act.getClass().equals(this.getClass()) && ((Animal) act).canInfect()){
                        ((Animal) act).infect();
                    }
                }
            }
        }
    }

    /**
     * Infects this animal.
     */
    public void infect(){
        infected = true;
        infCounter = DISEASE_DURATION;
        wasInfected = true;
    }

    /**
     * @return True if the animal can be infected.
     */
    public boolean canInfect(){
        return !wasInfected;
    }

    /**
     * @return True if the animal is infected.
     */
    public boolean isInfected(){
        return infected;
    }

    /**
     * An abstract method which needs to be implemented by all subclasses.
     * Spawn an animal in a location (when giving birth for example).
     * @param loc The location in which to spawn the animal.
     * @return The animal that was spawned.
     */
    abstract protected Animal spawnAnimal(Location loc);

    /**
     * A node for the see method implementation, used in the search.
     * Holds a location, distance and the previous location.
     */
    private static class sLoc {
        private final Location l;
        private final int d;
        private final sLoc bef;

        /**
         * Constructor for this class, initializes fields.
         * @param l The location.
         * @param d The distance.
         * @param bef The previous location.
         */
        private sLoc(Location l,int d,sLoc bef){
            this.l = l;
            this.d = d;
            this.bef = bef;
        }
    }
}


