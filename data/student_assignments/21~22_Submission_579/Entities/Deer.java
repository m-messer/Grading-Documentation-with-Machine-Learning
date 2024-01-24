package Entities;

import Environment.Tile;

import java.util.Set;

/**
 * Deer class
 * 
 * Deer (shown in red): are very light sleepers, sleeping for many short periods (of about 1 hour) during daytime hours. They even sleep with their eyes open - allowing them to notice predators and wake up in time to escape whenever the threat arises. They are herbivores which eat the fruits of thickets and trees.
 *
 * @version 2022.02.08
 */
public class Deer extends Creature
{
    /**
     * Constructor for objects of class Fish
     */
    public Deer(Tile tile)
    {
        super(tile);
        type = EntityType.DEER;
        
        sightRange = 3;
        speed = 9.8/12;
        pregnancyTime = 2500;
        matingCooldownPeriod = 3600*42;
        
        minWaterLevel = null;
        maxWaterLevel = 0.6;
        
        fleeFromTypes = Set.of(EntityType.WOLF, EntityType.BEAR);
        preyTypes = Set.of(EntityType.WOODS, EntityType.THICKET);
        
        maxBodyEnergy = 8000;
        decompositionRate = 0.00001;
        maxHydration = 12;
        hydrationLossRate = maxHydration/(3600*36);
        maxNourishment = 1750;
        nourishmentLossRate = maxNourishment/(3600*36);
        maxSleepTime = 1*60*60;
        timeBetweenSleeps = 2*60*60;
        sleepsWithEyesOpen = true;
        
        initialiseSpecifics();
    }
    
    protected boolean canSleepIn(Tile tile) {
        return (tile.getWaterLevel() <= 0);
    }
    
    protected boolean isSleepTime() {
        int timeOfDay = tile.getHabitat().getTimeOfDay();
        return (timeOfDay > 60*60*6 && timeOfDay < 60*60*18); 
    }
    
    protected void giveBirth() {
        new Deer(tile);
    }
}
