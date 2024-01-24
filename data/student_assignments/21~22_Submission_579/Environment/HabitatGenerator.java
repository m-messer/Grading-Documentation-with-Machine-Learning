package Environment;

import java.util.Random;

import Utils.Noise2D;
import Environment.EntityGenerators.*;

/**
 * A class which allows for the generation of a habitat in stages. There are a number of
 * stages, each of which is documented beside the corresponding function.
 *
 * @version 2022.02.08
 */
public class HabitatGenerator
{
    // Default values for seed, width, and height
    // These will be used if the HabitatGenerator is provided with no other values
    private static final long DEFAULT_SEED = 1234;
    private static final int DEFAULT_WIDTH = 240;
    private static final int DEFAULT_HEIGHT = 160;
    
    // The scale of habitats that are generated
    // The simulation is designed with these values in mind. It's probably better
    // Not to change them unless you really know what you're doing.
    private static final int SECONDS_PER_STEP = 60;
    private static final int METERS_PER_TILE = 100;
    
    // The minimum and maximum elevations (in meters)
    // All habitats that are generated will be normalised into this range.
    // This allows any  habitat to be fully representative of the sample space
    private static final double MIN_ELEVATION = 1700;
    private static final double MAX_ELEVATION = 2500;
    
    // The average water level per tile (in meters). This will drain into low points
    // during the elevation generation stage
    private static final double WATER_LEVEL_PER_TILE = 2;
    // How many times the generate water function is called
    public static final double WATER_GENERATION_STEPS = 200;
    // How far away water is checked for.
    public static final int SATURATION_SEARCH_RADIUS = 15; 
    
    /**
     * Private constructor: HabitatGenerator cannot be instantiated
     */
    private HabitatGenerator() {}
    
    /**
     * Generate a habitat from a width, height, and seed. This does not include the
     * contents of the habitat: those are provided in other methods of this class.
     * If null values are given, the default values are used.
     * @param seed The seed to generate the world from, or null
     * @param width The width of the habitat to generate, or null
     * @param height The height of the habitat to generate, or null
     * @return A habitat generated from the parameters
     */
    public static Habitat generateHabitat(Long seed, Integer width, Integer height) {
        // Set all the data to the defaults if no values are provided.
        if(seed == null) seed = DEFAULT_SEED;
        if(width == null) width = DEFAULT_WIDTH;
        if(height == null) height = DEFAULT_HEIGHT;
        
        // Create the habitat, providing it the randomiser for later usage
        Habitat habitat = new Habitat(seed, width, height, SECONDS_PER_STEP, METERS_PER_TILE);
        // Clear the habitat
        habitat.reset();
        
        // Return the habitat
        return habitat;
    }
    
    /**
     * Generate elevation for a given habitat. Elevation is normalised between the constants
     * MIN_ELEVATION and MAX_ELEVATION (both in meters). The elevations are generated by
     * the ElevationMap class, which automatically includes grooves to represent river lines.
     * @param habitat The habitat to generate elevation for
     */
    public static void generateElevation(Habitat habitat) {
        // Generate an elevation map to set elevations from
        ElevationMap elevationMap = new ElevationMap(habitat.getRandomiser());
        // Get the habitat's tiles, so that their elevations can be set
        Tile[] tiles = habitat.getTiles();
        // Set the elevation of the tiles
        setTileElevations(tiles, elevationMap);
        // Let the habitat know that its max and min heights need updating
        habitat.recalculateMinAndMaxElevations();
        // Normalise the minimum and maximum elevations for the habitat
        normaliseTileElevations(tiles);
        // Let the habitat know that its max and min heights need updating
        habitat.recalculateMinAndMaxElevations();
    }
    
    /**
     * Set the elevation of an array of tiles according to a given ElevationMap.
     * @param tiles An array containing all tiles to set the elevation of.
     * @param elevationMap The elevation map to get elevation values from 
     */
    private static void setTileElevations(Tile[] tiles, ElevationMap elevationMap)
    {
        for(Tile tile : tiles) {
            int row = tile.getRow();
            int col = tile.getCol();
            tile.setElevation(elevationMap.eval(col,row));
        }
    }
    
    /**
     * Takes the tile elevations of a series of tiles and upscales the values for all tiles.
     * The new elevations represent real-world values of 1700-2500 metres.
     * @param tiles     The tiles which will have their elevations normalised
     */
    private static void normaliseTileElevations(Tile[] tiles) {
        double minElevation; 
        double maxElevation;
        minElevation = maxElevation = tiles[0].getElevation();
        for(Tile tile : tiles) {
            minElevation = Math.min(minElevation, tile.getElevation());
            maxElevation = Math.max(maxElevation, tile.getElevation());
        }
        double multiplier = (MAX_ELEVATION - MIN_ELEVATION) / (maxElevation - minElevation);
        double increment = MIN_ELEVATION - minElevation*multiplier;
        
        for(Tile tile : tiles) {
            double elevation = tile.getElevation();
            tile.setElevation(elevation * multiplier + increment);
        }
    }
    
    /**
     * Generate weather for a given habitat. Weather is currently represented by a CloudMap,
     * which handles its own windspeed. The presence or absence of a cloud at any point
     * in time is determined by the CloudMap.
     * @param habitat The habitat to generate weather for.
     */
    public static void generateWeather(Habitat habitat) {
        CloudMap cloudMap = new CloudMap(habitat.getRandomiser());
        habitat.setCloudMap(cloudMap);
    }
    
    /**
     * Generate the water for a given habitat. This is done by spreading water evenly
     * across all tiles, and then using a fluid simulation to drain water from areas of
     * high elevation to areas of low elevation. This does not mean that all water will
     * end up at the lowest points, as it may collect in local minima as well. Due to
     * the nature of elevation generation, this is also likely to create rivers.
     * @param habitat The habitat to generate water for
     */
    public static void generateWater(Habitat habitat) {
        for(Tile tile : habitat.getTiles()) {
            tile.addWaterLevel(WATER_LEVEL_PER_TILE);
        }
        for(int i = 0; i < WATER_GENERATION_STEPS; i++) {
            for(Tile tile : habitat.getTiles()) {
                tile.spreadWater();
            }
        }
        habitat.recalculateMinAndMaxWaterLevels();
    }
    
    /**
     * Sets the saturation floating point value for every tile in the habitat.
     * This is done by taking a tile and getting all the tiles at a particular Manhattan
     * distance from it. We then check the water level values of all these tiles. 
     * If it is above 0, saturation is increased based on distance from the original tile.
     * This is then repeated for all tiles in the habitat.
     * @param habitat   The habitat we want to set tile saturations for.
     */
    public static void generateSaturations(Habitat habitat) {
        for(Tile tile : habitat.getTiles()) {
            float saturation = 0;
            for(int distance = 0; distance <= SATURATION_SEARCH_RADIUS; distance++) {
                Tile[] tilesAtCurrentDistance = habitat.getTilesAtDistance(tile, distance);
                for(Tile currentTile : tilesAtCurrentDistance) {
                    if(currentTile.getWaterLevel() > 0) {
                        saturation += 1.0/(SATURATION_SEARCH_RADIUS*(tilesAtCurrentDistance.length));
                    }
                }
            }
            tile.setSaturation(saturation);
        }
        habitat.recalculateMinAndMaxSaturations();
    }
    
    /**
     * Generates all plant life (algae, woods and thickets) in the habitat. This is done
     * using an EntityGenerator that corresponds to each type of plant.
     * @param habitat   The habitat we wish to generate all the plant life in.
     */
    public static void generateFlora(Habitat habitat) {
        WoodsGenerator woodsGenerator = new WoodsGenerator(habitat.getRandomiser());
        woodsGenerator.populate(habitat.getTiles());
        ThicketGenerator thicketGenerator = new ThicketGenerator(habitat.getRandomiser());
        thicketGenerator.populate(habitat.getTiles());
        AlgaeGenerator algaeGenerator = new AlgaeGenerator(habitat.getRandomiser());
        algaeGenerator.populate(habitat.getTiles());
    }
    
    /**
     * Generates all animal life (fish, birds, deer, bears, wolves) in the habitat. This
     * is done by using an EntityGenerator that corresponds to each type of animal.
     * @param habitat   The habitat we wish to generate all the animal life in.
     */
    public static void generateFauna(Habitat habitat) {
        FishGenerator fishGenerator = new FishGenerator(habitat.getRandomiser());
        fishGenerator.populate(habitat.getTiles());
        
        BirdGenerator birdGenerator = new BirdGenerator(habitat.getRandomiser());
        birdGenerator.populate(habitat.getTiles());
        
        DeerGenerator deerGenerator = new DeerGenerator(habitat.getRandomiser());
        deerGenerator.populate(habitat.getTiles());
        
        WolfGenerator wolfGenerator = new WolfGenerator(habitat.getRandomiser());
        wolfGenerator.populate(habitat.getTiles());
        
        BearGenerator bearGenerator = new BearGenerator(habitat.getRandomiser());
        bearGenerator.populate(habitat.getTiles());
    }
}
