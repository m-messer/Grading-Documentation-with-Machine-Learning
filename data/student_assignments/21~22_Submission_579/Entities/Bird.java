package Entities;
import Environment.Tile;

import java.util.Set;

/**
 * Bird class.
 * 
 * Birds (shown in orange) are diurnal, but wake up early in the day. They are omnivorous, eating fish and the fruits of thickets. Birds may enter tiles with any level of water (due to their flight capabilities), and will only sleep in tree tiles.
 *
 * @version 2022.02.08
 */
public class Bird extends Creature
{
    /**
     * Constructor for objects of class Fish
     */
    public Bird(Tile tile)
    {
        super(tile);
        type = EntityType.BIRD;
        
        sightRange = 6;
        speed = 9.0/12;
        pregnancyTime = 1500;
        matingCooldownPeriod = 3600*42;
        
        minWaterLevel = null;
        maxWaterLevel = null;
        
        fleeFromTypes = Set.of(EntityType.WOLF);
        preyTypes = Set.of(EntityType.FISH, EntityType.THICKET);
        
        maxBodyEnergy = 600;
        decompositionRate = 0.00001;
        maxHydration = 0.0025;
        hydrationLossRate = maxHydration/(3600*36);
        maxNourishment = 264;
        nourishmentLossRate = maxNourishment/(3600*36);
        maxSleepTime = 6*60*60;
        timeBetweenSleeps = 18*60*60;
        
        initialiseSpecifics();
    }
    
    protected boolean canSleepIn(Tile tile) {
        for(Entity entity : tile.getEntities()) {
            if(entity.getType() == EntityType.WOODS) return true;
        }
        return false;
    }
    
    protected boolean isSleepTime() {
        int timeOfDay = tile.getHabitat().getTimeOfDay();
        return (timeOfDay < 60*60*4 || timeOfDay > 60*60*22); 
    }
    
    protected void giveBirth() {
        new Bird(tile);
    }
}
