package Entities;

import Environment.Tile;

/**
 * Flora are a type of entity which does little other than increment its own body energy
 * over time.
 *
 * @version 2022.02.08
 */
public abstract class Flora extends Entity
{
    public double energyRegenRate;
    
    public Flora(Tile tile) {
        super(tile);
    }
    
    public void update(int secondsPassed) {
        bodyEnergy = Math.min(maxBodyEnergy, bodyEnergy + energyRegenRate*secondsPassed);
    }
}
