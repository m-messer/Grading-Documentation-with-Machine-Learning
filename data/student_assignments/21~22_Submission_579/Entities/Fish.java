package Entities;

import Environment.Tile;

import java.util.Set;

/**
 * Fish class.
 * 
 * Fish (shown in purple): May sleep at any time of the day, but only for short periods (of about 1 hour). Fish are the only creature to be spawned in water tiles, and cannot move onto land tiles. They eat only algae, but are unable to detect predators (due to being underwater and unable to see them).
 *
 * @version 2022.02.08
 */
public class Fish extends Creature
{
    
    /**
     * Constructor for objects of class Fish
     */
    public Fish(Tile tile)
    {
        super(tile);
        type = EntityType.FISH;
        
        sightRange = 2;
        speed = 4.5/12;
        pregnancyTime = 1250;
        matingCooldownPeriod = 3600*20;
        
        minWaterLevel = 0.0;
        maxWaterLevel = null;
        
        fleeFromTypes = Set.of();
        preyTypes = Set.of(EntityType.ALGAE);
        
        maxBodyEnergy = 100;
        decompositionRate = 0.00001;
        maxHydration = 0.05;
        hydrationLossRate = 0;
        maxNourishment = 40;
        nourishmentLossRate = maxNourishment/(3600*36);
        maxSleepTime = 1*60*60;
        timeBetweenSleeps = 2*60*60;
        sleepsWithEyesOpen = true;
        
        initialiseSpecifics();
    }
    
    protected boolean canSleepIn(Tile tile) {
        return (tile.getWaterLevel() > 0);
    }
    
    protected boolean isSleepTime() {
        return true;
    }
    
    protected void giveBirth() {
        new Fish(tile);
    }
}
