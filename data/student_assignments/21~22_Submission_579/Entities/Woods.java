package Entities;

import Environment.Tile;

/**
 * Woods class
 * 
 * Woods (shown in dark green) are highly dependent on water, and so will generally grow close to rivers and lakes. The nutrition that they produce is fruit, which is eaten by deer.
 *
 * @version 2022.02.08
 */
public class Woods extends Flora
{
    /**
     * Constructor for objects of class Tree
     */
    public Woods(Tile tile)
    {
        super(tile);
        type = EntityType.WOODS;
        energyRegenRate = 0.1;
        maxBodyEnergy = 50;
    }
}
