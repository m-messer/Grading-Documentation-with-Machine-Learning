package Environment.EntityGenerators;

import Utils.Noise2D;
import Environment.Tile;

import java.util.Random;

/**
 * A NoiseMap used to generate entities that live in the habitat, both flora and fauna.
 *
 * @version 2022.02.19
 */
public abstract class EntityGenerator
{
    protected Noise2D entityNoise;
    protected Noise2D xOffsetNoise;
    protected Noise2D yOffsetNoise;
    
    /**
     * Constructor for objects of class EntityMap
     */
    public EntityGenerator()
    {
    }
    
    public void populate(Tile[] tiles) {
        for(Tile tile : tiles) {
            populateTile(tile);
        }
    }
    
    protected abstract void populateTile(Tile tile);
}
