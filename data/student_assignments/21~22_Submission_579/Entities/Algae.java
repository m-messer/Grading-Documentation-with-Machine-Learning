package Entities;

import Environment.Tile;
import Environment.Habitat;

/**
 * Algae class
 * 
 * Algae colonies (shown in murky green)  are generated in water tiles and only in water tiles. As their energy increases, the tile containing them becomes more green in colour (represents their growth). Algae are eaten fish, which will decrease their energy.
 *
 * @version 2022.02.08
 */
public class Algae extends Flora
{
    
    /**
     * Constructor for objects of class Tree
     */
    public Algae(Tile tile)
    {
        super(tile);
        type = EntityType.ALGAE;
        energyRegenRate = 0.0001;
        maxBodyEnergy = 50;
    }
}

