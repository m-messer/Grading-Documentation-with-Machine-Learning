package Environment.EntityGenerators;

import java.util.Random;

import Utils.Noise2D;
import Environment.Tile;
import Entities.Algae;

/**
 * A class that influences and controls the generation of Algae tiles.
 * Algae is generated on all water tiles. There are no constants to adjust
 *
 * @version 2022.02.08
 */
public class AlgaeGenerator extends EntityGenerator
{
    /**
     * Constructor for AlgaeGenerator
     * @param randomiser The randomiser to generate the elevations from
     */
    public AlgaeGenerator(Random randomiser) {
    }
    
    /**
     * Attempt to place an algae colony onto a tile, if the tile is a water tile, AND
     * the elevation of the tile is above a threshold value.
     * @param tile      the tile an algae will attempt to be placed on.
     */
    public void populateTile(Tile tile) {
        if(tile.getWaterLevel() > 0) {
            new Algae(tile);
        }
    }
}
