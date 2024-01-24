package GUI;


/**
 * Represents the possible states a tile may take.
 * Allows tiles to be represented as land, water, or having clouds overhead.
 * Also represents the entities present on each tile, both flora and fauna.
 *
 * @version 2022.02.19
 */
public enum TileState
{
    BARREN_LAND, FERTILE_LAND,
    
    WATER, CLOUD,
    
    TREE, BUSH, ALGAE,
    
    BEAR, WOLF,
    
    DEER, FISH, BIRD,
}
