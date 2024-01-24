package Entities;

import Environment.Tile;
import Environment.Habitat;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class representing the shared characteristics of creatures. The values for these
 * characteristics are implemented in the derived types.
 *
 * @version 2022.02.08
 */
public abstract class Creature extends Entity
{
    private static final double wanderRandomlyChance = 0.02;
    
    // Variables dependent on the creature type
    // The range that the creature can see in tiles
    protected int sightRange;
    // The speed that the creature moves in meters per second
    protected double speed;
    // How many seconds the pregnancy of this creature type takes (drastically reduced for the purposes of the simulation)
    protected long pregnancyTime;
    
    // The minimum and maximum water levels (in litres) that this creature can stand in
    protected Double minWaterLevel;
    protected Double maxWaterLevel;
    
    // The predator and prey types of this enemy (prey includes 
    protected Set<EntityType> fleeFromTypes;
    protected Set<EntityType> preyTypes;
    
    // The rate at which the creature decomposes.
    protected double decompositionRate;
    // The maximum hydration (in litres) that the creature may have in its body
    protected double maxHydration;
    // The rate (in litres/second) at which the creature loses hydration
    protected double hydrationLossRate;
    // The maximum nourishment (in calories) that the creature may have in its body
    protected double maxNourishment;
    // The rate (in calories/second) at which the creature loses nourishment
    protected double nourishmentLossRate;
    // The maximum amount of time an creature will sleep in one sitting
    protected double maxSleepTime;
    // The number of times the creature must sleep in a day
    protected double timeBetweenSleeps;
    // The time between animal matings
    protected double matingCooldownPeriod;
    // Whether or not this creature will stop sleeping to run from predators
    protected boolean sleepsWithEyesOpen;
    
    // Variables dependont on the specific creature
    // The x and y position of this entity
    private double x;
    private double y;
    
    // The state of the creature (alive, dead, decomposed)
    private CreatureState state;
    // The activity of the creature (eating, sleeping, etc)
    private CreatureActivity activity;
    // The biological sex of the creature
    private Sex sex;
    // How long until the creature gives birth
    private Long pregnancyCountdown;
    
    // The amount of hydration that the animal has in litres
    private double hydration;
    // The amount of nourishment that the creature has in calories
    private double nourishment;
    // The amount (0-1) that the animal's body has decomposed
    private double decomposition;
    // The amount (0-1) that the animal is tired
    private double tiredness;
    // The amount of time until the animal will begin looking for a mate again
    private double matingCooldownTimer;
    // The direction that the creature will wander if it has nothing else to do
    private Double wanderDirection;
    
    /**
     * Constructor for objects of class Creature
     */
    public Creature(Tile tile)
    {
        super(tile);
        
        // Pick a random sex for this creature
        Sex[] sexes = Sex.values();
        sex = sexes[(tile.getHabitat().getRandomiser().nextInt(2))];
        
        // Set the creature's current position
        Habitat habitat = tile.getHabitat();
        x = tile.getCol()*habitat.getMetersPerTile();
        y = tile.getRow()*habitat.getMetersPerTile();
    }
    
    /**
     * Initialise the animal. This is the same for all entities, but must be called at the
     * end of the constructor for the subclass.
     */
    protected void initialiseSpecifics() {
        // Set the creature's current state
        pregnancyCountdown = null;
        wanderDirection = null;
        state = CreatureState.ALIVE;
        activity = CreatureActivity.WANDERING;
        hydration = maxHydration;
        nourishment = maxNourishment;
        bodyEnergy = maxBodyEnergy;
        matingCooldownTimer = matingCooldownPeriod;
        decomposition = 0;
        tiredness = 1;
    }
    
    /**
     * @return whether the current time in the creature's habitat is the sleep time for this entity
     */
    protected abstract boolean isSleepTime();
    
    /**
     * Have this creature perform one step. This means incrementing its needs, and performing
     * an activity for the step
     * @param secondsPassed The number of seconds since the last step
     */
    public void update(int secondsPassed) {
        // Determine the creature's activities for this tick
        if(state == CreatureState.ALIVE) { // As long as the creature is alive
            incrementNeeds(secondsPassed);
            updatePregnancy(secondsPassed);
            chooseActivity();
            performActivity(secondsPassed);
        } else { // Decompose if the creature is a corpse
            decompose(secondsPassed);
        }
    }
    
    /**
     * Increment the needs of this entity- such as nourishment, hydration, and tiredness
     */
    private void incrementNeeds(int secondsPassed) {
        // Reduce the hydration and nourishment for the creature
        hydration -= hydrationLossRate * secondsPassed;
        nourishment -= nourishmentLossRate * secondsPassed;
        
        // Die if food or water runs out.
        if(hydration < 0 || nourishment < 0) setDead();
        
        // Make creature less tired if it is sleeping, otherwise make it more tired
        if(activity == CreatureActivity.SLEEPING) tiredness = Math.max(0, tiredness - secondsPassed/maxSleepTime);
        else {
            tiredness = tiredness = Math.min(1, tiredness + secondsPassed/timeBetweenSleeps);
            matingCooldownTimer = Math.max(0, matingCooldownTimer - secondsPassed);
        }
    }
    
    /**
     * Count down towardspregnancy if this creature is pregnant - giving birth if the
     * counter reaches 0
     * @param secondsPassed The number of seconds since the last step
     */
    private void updatePregnancy(int secondsPassed) {
        // Decrement pregnant countdown and give birth, even if sleeping
        if(pregnancyCountdown != null) {
            pregnancyCountdown -= secondsPassed;
            if(pregnancyCountdown <= 0) {
                activity = CreatureActivity.WANDERING;
                giveBirth();
                pregnancyCountdown = null;
            }
        }
    }
    
    /**
     * Decompose this entity - destroying it once fully decomposed
     * @param secondsPassed The number of seconds since the last step.
     */
    private void decompose(int secondsPassed) {
        bodyEnergy -= maxBodyEnergy*decompositionRate*secondsPassed;
        decomposition += decompositionRate * secondsPassed;
        if(decomposition > 0.1) state = CreatureState.DECOMPOSED;
        if(bodyEnergy <= 0) selfDestruct();
    }
    
    /**
     * Choose an activity for this step, based on the current needs of the creature
     */
    private void chooseActivity() {
        // Flee predators if there are any (this overrides all other activities except sleeping)
        Creature predator = lookForPredators();
        if(predator != null && (sleepsWithEyesOpen || activity != CreatureActivity.SLEEPING)) {
            activity = CreatureActivity.FLEEING_PREDATOR;
        // Wake up if sleeping and no longer tired
        } else if(activity == CreatureActivity.SLEEPING) {
            if(tiredness == 0) activity = CreatureActivity.WANDERING;
        // If no important activity is in progress, choose one
        } else if (activity == CreatureActivity.WANDERING || activity == CreatureActivity.LOOKING_FOR_MATE);
            // Fall asleep if tired and it is time to sleep
            if (isSleepTime() && tiredness == 1) {
                activity = CreatureActivity.LOOKING_FOR_SLEEPING_PLACE;
            // Eat if hungry
            } else if (isHungry()) {
                activity = CreatureActivity.LOOKING_FOR_FOOD;
            // Drink if thirsty
            } else if (isThirsty()) {
                activity = CreatureActivity.LOOKING_FOR_WATER;
            // Mate if all needs are met and ready to mate
            } else if (matingCooldownTimer <= 0) {
                activity = CreatureActivity.LOOKING_FOR_MATE;
            }
        }
    
    /**
     * Perform whatever the current activity this entity is pursuing is
     * @param secondsPassed The number of seconds since the last step.
     */
    private void performActivity(int secondsPassed) {
        // Act on the current activity
        switch(activity) {
            case FLEEING_PREDATOR:
                Creature predator = lookForPredators();
                if(predator != null) flee(predator, secondsPassed);
                break;
            
            case LOOKING_FOR_SLEEPING_PLACE:
                Tile sleepingPlace = lookForSleepingPlace();
                if(sleepingPlace != null) {
                    sleepIn(sleepingPlace, secondsPassed);
                } else wander(secondsPassed);
                break;
            
            case LOOKING_FOR_WATER: 
                Tile water = lookForWater();
                if(water != null) {
                    drink(water, secondsPassed);
                } else wander(secondsPassed);
                break;
                
            case LOOKING_FOR_FOOD:
                Entity prey = lookForPrey();
                if(prey != null) {
                    hunt(prey, secondsPassed);
                } else wander(secondsPassed);
                break;
            
            case LOOKING_FOR_MATE:
                Creature potentialMate = lookForMate();
                if(potentialMate != null) {
                    mateWith(potentialMate, secondsPassed);
                } else wander(secondsPassed);
                break;
                
            case WANDERING:
                wander(secondsPassed);
                break;
        }
    }
    
    /**
     * Wander. This means walking in a straight line and changing directions occasionally
     * @param secondsPassed The number of seconds since the last step
     */
    private void wander(int secondsPassed) {
        // Pick a new direction if no direction is currently available
        boolean needsNewDirection = (wanderDirection == null);
        // Also pick a new direction if moving in the current direction is unsuccessful
        if(!needsNewDirection) needsNewDirection = !move(wanderDirection, speed*secondsPassed);
        // Also pick a new direction on random chance
        if(!needsNewDirection) needsNewDirection = tile.getHabitat().getRandomiser().nextFloat() < wanderRandomlyChance;
        
        // Pick a new direction if a new direction is needed
        if(needsNewDirection) {
            Tile[] tiles = getEnterableTilesInRange(1);
            if (tiles.length >= 1) {
                int index = tile.getHabitat().getRandomiser().nextInt(tiles.length);
                moveToward(tiles[index], secondsPassed);
            }
        }
    }
    
    /**
     * Look for predators within sight range
     * @return The nearest predator within sight range
     */
    private Creature lookForPredators() {
        Tile[] tiles = getEnterableTilesInRange(sightRange);
        for(Tile tile : tiles) {
            for(Entity entity : tile.getEntities()) {
                if(entity instanceof Creature) {
                    Creature creature = (Creature) entity;
                    if(fleeFromTypes.contains(creature.getType()) && creature.getState() == CreatureState.ALIVE && creature.getActivity() != CreatureActivity.SLEEPING) {
                        return creature;
                    }
                }
            }
        }
        return null;
    }
    /**
     * Flee a creature
     * @param predator The creature to flee
     * @param secondsPassed the number of seconds since the last step
     */
    private void flee(Creature predator, int secondsPassed) {
        Tile target = pathFrom(predator);
        moveToward(target, secondsPassed);
    }
    
    
    /**
     * Look for a suitable sleeping place
     * @return The nearest suitable sleeping place
     */
    private Tile lookForSleepingPlace() {
        Tile[] tiles = getEnterableTilesInRange(sightRange);
        for(Tile tile : tiles) {
            if(canSleepIn(tile)) return tile;
        }
        return null;
    }
    /**
     * Check if a given tile can be slept in
     * @param tile The tile to check
     * @return Whether the tile is a suitable sleeping spot
     */
    protected abstract boolean canSleepIn(Tile tile);
    /**
     * Move to and sleep in a given sleeping place
     * @param sleepingPlace The tile to sleep in
     * @param secondsPassed The number of seconds since the last step
     */
    private void sleepIn(Tile sleepingPlace, int secondsPassed) {
        Tile target = pathTo(sleepingPlace);
        moveToward(target, secondsPassed);
        if(tile == sleepingPlace) {
            activity = CreatureActivity.SLEEPING;
        }
    }
    
    
    /**
     * Look for water
     * @return The closest water tile within the sight range of this creature
     */
    private Tile lookForWater() {
        Tile[] tiles = getEnterableTilesInRange(sightRange);
        for(Tile tile : tiles) {
            if(tile.getWaterLevel() > 0) {
                return tile;
            }
        }
        return null;
    }
    /**
     * Move to and drink from a given water tile
     * @param waterLocation A tile containing water
     * @param secondsPassed The number of seconds since the last step
     */
    private void drink(Tile waterLocation, int secondsPassed) {
        Tile target = pathTo(waterLocation);
        moveToward(target, secondsPassed);
        for(Tile tile : tile.getTilesWithinDistance(1)) {
            if(tile == waterLocation) {
                hydration = maxHydration;
                if(!isThirsty()) activity = CreatureActivity.WANDERING;
            }
        }
    }
    
    
    /**
     * Look for prey
     * @return The entity that this creature can see which has the highest body energy
     */
    private Entity lookForPrey() {
        Tile[] tiles = getEnterableTilesInRange(sightRange);
        
        double maxEnergy = 0;
        Entity maxEnergyPrey = null;
        
        for(Tile tile : tiles) {
            for(Entity entity : tile.getEntities()) {
                if(preyTypes.contains(entity.getType()) && entity.getBodyEnergy() > maxEnergy) {
                    if(!(entity instanceof Creature && ((Creature) entity).getState() == CreatureState.DECOMPOSED)) {
                        maxEnergy = entity.getBodyEnergy();
                        maxEnergyPrey = entity;
                    }
                }
            }
        }
        return maxEnergyPrey;
    }
    /**
     * Move to and eat a given entity
     * @param prey The animal to hunt
     * @param secondsPassed The number of seconds since the last step
     */
    private void hunt(Entity prey, int secondsPassed) {
        Tile target = pathTo(prey);
        moveToward(target, secondsPassed);
        for(Tile searchTile : tile.getTilesWithinDistance(1)) {
            if(searchTile == prey.getTile()) {
                nourishment += prey.beEaten(maxNourishment - nourishment);
                if(!isHungry()) activity = CreatureActivity.WANDERING;
            }
        }
    }
    
    /**
     * Look for a mate
     * @return The closest creature of the same type but a different sex
     */
    private Creature lookForMate() {
        Tile[] tiles = getEnterableTilesInRange(sightRange);
        for(Tile tile : tiles) {
            for(Entity entity : tile.getEntities()) {
                if(entity instanceof Creature) {
                    Creature creature = (Creature) entity;
                    if(getType() == creature.getType() && creature.sex != sex) {
                        return creature;
                    }
                }
            }
        }
        return null;
    }
    /**
     * Move to and mate with a given creature
     * @param potentialMate A creature to mate with
     * @param secondsPassed The number of seconds since the last step
     */
    private void mateWith(Creature potentialMate, int secondsPassed) {
        Tile target = pathTo(potentialMate);
        moveToward(target, secondsPassed);
        if(tile == potentialMate.getTile()) {
            sex(potentialMate);
            potentialMate.sex(this);
            matingCooldownTimer = matingCooldownPeriod;
            activity = CreatureActivity.WANDERING;
        }
    }
    
    /**
     * Make self pregnant if mating pair is compatible
     */
    private void sex(Creature mate) {
        if(sex == Sex.FEMALE && mate.sex == Sex.MALE && getType() == mate.getType()) { 
            if(pregnancyCountdown == null) { pregnancyCountdown = pregnancyTime; }
        }
    }
    
    /**
     * Give birth to a new creature of this type
     */
    protected abstract void giveBirth();
    
    /**
     * Get all the tiles which can be entered within a given range
     * @param range The distance (in tiles) to check
     * @return All tiles which are within the provided distance that this creature can enter
     */
    private Tile[] getEnterableTilesInRange(int range) {
        List<Tile> tiles = Arrays.asList(tile.getTilesWithinDistance(range));
        return tiles.stream()
            .filter(nextTile -> canEnter(nextTile))
            .toArray(Tile[]::new);
    }
    
    /**
     * Move towards a given tile
     * @param target The tile to move towards
     * @param secondsPassed The number of seconds that have passed since the last step
     */
    private void moveToward(Tile target, int secondsPassed) {
        int xDif = target.getCol() - tile.getCol();
        int yDif = target.getRow() - tile.getRow();
        if(!(xDif == 0 && yDif == 0)) {
            double direction = Math.atan2(yDif,xDif);
            boolean success = move(direction, speed*secondsPassed);
            if(success) wanderDirection = direction;
            else wanderDirection = null;
        }
    }
    
    /**
     * Move this creature
     * @param direction The direction (radians) to move in
     * @param distance The amount of distance to move by
     * @return Whether or not the movement was successful
     */
    private boolean move(double direction, double distance) {
        int metersPerTile = tile.getHabitat().getMetersPerTile();
        
        double nextX = x + distance*Math.cos(direction);
        double nextY = y + distance*Math.sin(direction);
        
        Tile nextTile = tile.getHabitat().getTile((int)(nextX/metersPerTile),(int)(nextY/metersPerTile));
        if(nextTile != null && canEnter(nextTile)) {
            setTile(nextTile);
            x = nextX; y = nextY;
            return true;
        }
        return false;
    }
    
    /**
     * Get the next tile to move to on the path from a particular entity
     * @param entityToRunFrom The entity to path away from
     * @return The next tile on the journey away from that creature
     */
    private Tile pathFrom(Entity entityToRunFrom) {
        return pathFrom(entityToRunFrom.getTile());
    }
    
    /**
     * Get the next tile to move to on the path to a particular entity
     * @param entityToRunFrom The entity to path away from
     * @return The next tile on the journey away from that creature
     */
    private Tile pathTo(Entity entityToRunTo) {
        return pathTo(entityToRunTo.getTile());
    }
    
    
    /**
     * Get the next tile to move to on the path from a particular tile
     * @param tileToRunFrom The tile to path away from
     * @return The next tile on the journey away from that tile
     */
    private Tile pathFrom(Tile tileToRunFrom) {
        int runFromCol = tileToRunFrom.getCol();
        int runFromRow = tileToRunFrom.getRow();
        
        Tile furthestTile = null;
        float furthestDistance = 0;
        for(Tile searchTile : tile.getTilesWithinDistance(1)) {
            if(canEnter(searchTile)) {
                int xDistance = searchTile.getCol() - runFromCol;
                int yDistance = searchTile.getRow() - runFromRow;
                float distance = (float) Math.pow(Math.pow(xDistance, 2) + Math.pow(yDistance, 2), 0.5);
                if(furthestTile == null || distance > furthestDistance) {
                    furthestTile = searchTile;
                    furthestDistance = distance;
                }
            }
        }
        return furthestTile;
    }
    
    /**
     * Get the next tile to move to on the path to a particular tile
     * @param tileToRunFrom The tile to path towards
     * @return The next tile on the journey away from that tile
     */
    private Tile pathTo(Tile tileToRunTo) {
        int runToCol = tileToRunTo.getCol();
        int runToRow = tileToRunTo.getRow();
        
        Tile closestTile = null;
        double closestDistance = 0;
        for(Tile searchTile : tile.getTilesWithinDistance(1)) {
            if(canEnter(searchTile)) {
                int xDistance = searchTile.getCol() - runToCol;
                int yDistance = searchTile.getRow() - runToRow;
                double distance = Math.pow(Math.pow(xDistance, 2) + Math.pow(yDistance, 2), 0.5);
                if(closestTile == null || distance < closestDistance) {
                    closestTile = searchTile;
                    closestDistance = distance;
                }
            }
        }
        return closestTile;
    }
    
    /**
     * @return Whether this creature is hungry
     */
    private boolean isHungry() {
        return nourishment/maxNourishment < 0.7;
    }
    
    /**
     * @return Whether this creature is thirsty
     */
    private boolean isThirsty() {
        return hydration/maxHydration < 0.7;
    }
    
    /**
     * Check if this creature can enter a given tile
     * @param checkTile The tile to check
     * @return Whether this creature can enter that tile
     */
    private boolean canEnter(Tile checkTile) {
        return ((minWaterLevel == null || checkTile.getWaterLevel() > minWaterLevel) && (maxWaterLevel == null || checkTile.getWaterLevel() <= maxWaterLevel));
    }
    
    /**
     * @return This creature's current state
     */
    public CreatureState getState() { return state; }
    /**
     * @return This creature's current activity
     */
    public CreatureActivity getActivity() { return activity; }
    
    /**
     * Set the creature as dead. It will begin to decompose.
     */
    protected void setDead() {
        state = CreatureState.DEAD;
        activity = null;
        bodyEnergy += nourishment;
        nourishment = 0;
    }
}
