package Entities;

import Environment.Tile;

import java.util.Set;

/**
 * Wolf class
 * 
 * Wolves (shown in white) are crepuscular, sleeping between from dusk to dawn. They are apex predators which eat fish, birds and deer.
 *
 * @version 2022.02.08
 */
public class Wolf extends Creature
{
    /**
     * Constructor for objects of class Fish
     */
    public Wolf(Tile tile)
    {
        super(tile);
        type = EntityType.WOLF;
        
        sightRange = 4;
        speed = 12.3/12;
        pregnancyTime = 2000;
        matingCooldownPeriod = 3600*36;
        
        minWaterLevel = null;
        maxWaterLevel = 0.4;
        
        fleeFromTypes = Set.of();
        preyTypes = Set.of(EntityType.FISH, EntityType.BIRD, EntityType.DEER);
        
        maxBodyEnergy = 10000;
        decompositionRate = 0.00001;
        maxHydration = 2.275;
        hydrationLossRate = maxHydration/(3600*36);
        maxNourishment = 4000;
        nourishmentLossRate = maxNourishment/(3600*36);
        timeBetweenSleeps = 14*60*60;
        maxSleepTime = 10*60*60;
        
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
        new Wolf(tile);
    }
}
