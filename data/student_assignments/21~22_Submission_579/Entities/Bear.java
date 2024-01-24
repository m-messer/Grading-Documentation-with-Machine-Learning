package Entities;

import Environment.Tile;

import java.util.Set;

/**
 * Bear class.
 * 
 * Bears (Shown in black) are diurnal - sleeping for long periods during nighttime. They are apex predators which eat deer and fish. They require more food than any other animal to stay alive, but also can go for longer without eating.
 *
 * @version 2022.02.08
 */
public class Bear extends Creature
{
    
    /**
     * Constructor for objects of class Fish
     */
    public Bear(Tile tile)
    {
        super(tile);
        type = EntityType.BEAR;
        
        sightRange = 5;
        speed = 10.5/12;
        pregnancyTime = 3000;
        matingCooldownPeriod = 3600*96;
        
        minWaterLevel = null;
        maxWaterLevel = 0.6;
        
        fleeFromTypes = Set.of();
        preyTypes = Set.of(EntityType.FISH, EntityType.DEER);
        
        maxBodyEnergy = 20000;
        decompositionRate = 0.00001;
        maxHydration = 12;
        hydrationLossRate = maxHydration/(3600*36);
        maxNourishment = 8000;
        nourishmentLossRate = maxNourishment/(3600*48);
        maxSleepTime = 7*60*60;
        timeBetweenSleeps = 15*60*60;
        
        initialiseSpecifics();
    }
    
    protected boolean canSleepIn(Tile tile) {
        return (tile.getWaterLevel() <= 0);
    }
    
    protected boolean isSleepTime() {
        int timeOfDay = tile.getHabitat().getTimeOfDay();
        return (timeOfDay < 60*60*6 || timeOfDay > 60*60*21); 
    }
    
    protected void giveBirth() {
        new Bear(tile);
    }
}
