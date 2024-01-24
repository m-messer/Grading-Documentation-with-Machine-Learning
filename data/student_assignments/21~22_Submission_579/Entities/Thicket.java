package Entities;

import Environment.Tile;

/**
 * Thicket class
 * 
 * Thickets (shown in light green) represent bushes, shrubs and other small plants. These cannot spread, for the same reason that the woods cannot. Like the woods, they produce fruit (such as berries) which can be consumed by deer and birds.
 *
 * @version 2022.02.08
 */
public class Thicket extends Flora
{
    private static final double nourishRegenRate = 0.1;
    /**
     * Constructor for objects of class Tree
     */
    public Thicket(Tile tile)
    {
        super(tile);
        type = EntityType.THICKET;
        energyRegenRate = 0.1;
        maxBodyEnergy = 25;
    }
}
