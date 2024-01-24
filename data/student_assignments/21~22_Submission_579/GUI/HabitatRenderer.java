package GUI;

import java.awt.Color;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;

import Environment.Habitat;
import Environment.CloudMap;
import Environment.Tile;

import Entities.*;

/**
 * Defines the colours, saturation, elevation, display brightness and allows for 
 * the mixing of different tile colours which overlap in the simulation, alongside
 * many other functions that allow the habitat to be displayed as a grid of pixels.
 *
 * @version 2022.02.19
 */
public class HabitatRenderer
{
    private final static Map<TileState, Color> COLOURS = new HashMap<>();
    static {
        COLOURS.put(TileState.BARREN_LAND,  new Color(216,177,108));
        COLOURS.put(TileState.FERTILE_LAND, new Color(110,150,99));
        COLOURS.put(TileState.TREE,         new Color(0,120,0));
        COLOURS.put(TileState.BUSH,         new Color(144,238,144));
        COLOURS.put(TileState.WATER,        new Color(40,102,150));
        COLOURS.put(TileState.ALGAE,        new Color(75,162, 91));
        COLOURS.put(TileState.CLOUD,        new Color(220,220,220));
        COLOURS.put(TileState.BIRD,         new Color(201,165,21));
        COLOURS.put(TileState.FISH,         new Color(69,18,87));
        COLOURS.put(TileState.DEER,         new Color(200,62,30));
        COLOURS.put(TileState.WOLF,         new Color(255,255,255));
        COLOURS.put(TileState.BEAR,         new Color(0,0,0));
    }
    
    private final static double WATER_DISPLAY_THRESHOLD = 0;
    
    private final static float LOWEST_ELEVATION_HUE = 0;
    private final static float HIGHEST_ELEVATION_HUE = 0.7f;
    private final static float ELEVATION_SAT = 0.8f;
    private final static float ELEVATION_BRIGHTNESS = 0.8f;
    
    private final static float LOWEST_SATURATION_HUE = 0;
    private final static float HIGHEST_SATURATION_HUE = 0.7f;
    private final static float SATURATION_SAT = 0.8f;
    private final static float SATURATION_BRIGHTNESS = 0.8f;
    
    private HabitatPanel habitatPanel;
    private RenderMode renderMode;
    private boolean cloudsVisible;
    
    /**
     * Constructor for objects of class HabitatRenderer
     * @parameter habitatPanel the HabitatPanel to render to
     */
    public HabitatRenderer(HabitatPanel habitatPanel)
    {
        this.habitatPanel = habitatPanel;
    }

    /**
    * Controls the rendering of the image on the display.
    * For each different case in the switch statement, render a different view of the habitat
    * @param habitat        the habitat that will be displayed
    */
    public void render(Habitat habitat) {
        habitatPanel.preparePaint();
        switch(renderMode) {
            case NORMAL:
                renderNormal(habitat);
                break;
            case ELEVATION:
                renderElevation(habitat);
                break;
            case SATURATION:
                renderSaturation(habitat);
                break;
        }
        habitatPanel.repaint();
    }
    
    /**
     * Renders the normal view of the simulation.
     * @param habitat        the habitat that will be displayed
     */
    private void renderNormal(Habitat habitat) {
        for(Tile tile : habitat.getTiles()) {
            int col = tile.getCol(); int row = tile.getRow();
            Color colour = getTileColour(tile);
            habitatPanel.drawMark(col, row, colour);
        }
    }
    
    /**
     * Returns the colour of any tile based on the flora and fauna, and any clouds, above it.
     * @param tile          the tile we want the colour of
     * @return tileColour       the colour of the tile
     */
    private Color getTileColour(Tile tile) {
        Habitat habitat = tile.getHabitat();
        
        Color tileColour = COLOURS.get(TileState.FERTILE_LAND);
        
        float waterCover = (tile.getWaterLevel() > WATER_DISPLAY_THRESHOLD ? 1 : 0);
        tileColour = mixColours(tileColour, COLOURS.get(TileState.WATER), waterCover);
        
        Algae algae = (Algae) tile.searchForEntity(EntityType.ALGAE);
        if(algae != null) {
            tileColour = mixColours(tileColour, COLOURS.get(TileState.ALGAE), (float) (algae.getBodyEnergy()/algae.getMaxBodyEnergy()));
        }
        
        if(tile.searchForEntity(EntityType.THICKET) != null) {
            tileColour = COLOURS.get(TileState.BUSH);
        }
        
        if(tile.searchForEntity(EntityType.WOODS) != null) {
            tileColour = COLOURS.get(TileState.TREE);
        }
        
        if(tile.searchForEntity(EntityType.FISH) != null) {
            tileColour = COLOURS.get(TileState.FISH);
        }
        
        if(tile.searchForEntity(EntityType.BIRD) != null) {
            tileColour = COLOURS.get(TileState.BIRD);
        }
        
        if(tile.searchForEntity(EntityType.DEER) != null) {
            tileColour = COLOURS.get(TileState.DEER);
        }
        
        if(tile.searchForEntity(EntityType.WOLF) != null) {
            tileColour = COLOURS.get(TileState.WOLF);
        }
        
        if(tile.searchForEntity(EntityType.BEAR) != null) {
            tileColour = COLOURS.get(TileState.BEAR);
        }
        
        CloudMap cloudMap = habitat.getCloudMap();
        if(cloudMap != null && cloudsVisible) {
            float cloudCover = (float) cloudMap.eval(tile.getCol(), tile.getRow());
            tileColour = mixColours(tileColour, COLOURS.get(TileState.CLOUD), cloudCover);
        }
        
        return tileColour;
    }
    
    /**
     * Renders the elevation view of the simulation.
     * @param habitat        the habitat that will be displayed
     */
    private void renderElevation(Habitat habitat) {
        double minElevation = habitat.getMinElevation();
        double maxElevation = habitat.getMaxElevation();
        double elevationRange = maxElevation - minElevation;
        for(Tile tile : habitat.getTiles()) {
            double differenceFromMin = tile.getElevation() - minElevation;
            float elevationPercentage = (float) (differenceFromMin/elevationRange);
            Color colour = elevationPercentageToColour(elevationPercentage);
            habitatPanel.drawMark(tile.getCol(),tile.getRow(), colour);
        }
    }
    
    /**
     * Converts the floating point value elevationPercentage to an RGB Color.
     * @param elevationPercentage       float value to be converted to Color
     * @return colour       the colour based on elevationPercentage
     */
    private Color elevationPercentageToColour(float elevationPercentage) {
        float hue = HIGHEST_ELEVATION_HUE - elevationPercentage*(HIGHEST_ELEVATION_HUE-LOWEST_ELEVATION_HUE);
        Color colour =  Color.getHSBColor(hue, ELEVATION_SAT, ELEVATION_BRIGHTNESS);
        return colour;
    }
    
    /**
     * For all tiles in the habitat, render their colour on the display.
     * @param habitat       the Habitat to be displayed
     */
    private void renderSaturation(Habitat habitat) {
        float minSaturation = habitat.getMinSaturation();
        float maxSaturation = habitat.getMaxSaturation();
        float saturationRange = maxSaturation - minSaturation;
        for(Tile tile : habitat.getTiles()) {
            float differenceFromMin = tile.getSaturation() - minSaturation;
            float saturationPercentage = differenceFromMin/saturationRange;
            Color colour = saturationPercentageToColour(saturationPercentage); // convert saturation percentage of tiles to colour
            habitatPanel.drawMark(tile.getCol(),tile.getRow(), colour);
        }
    }
    
    /**
     * Converts the floating point value saturationPercentage to an RGB Color.
     * @param saturationPercentage       float value to be converted to Color
     * @return colour           colour based on saturation percentage
     */
    private Color saturationPercentageToColour(float saturationPercentage) {
        float hue = HIGHEST_SATURATION_HUE - saturationPercentage*(HIGHEST_SATURATION_HUE-LOWEST_SATURATION_HUE);
        Color colour = Color.getHSBColor(hue, SATURATION_SAT, SATURATION_BRIGHTNESS);
        return colour;
    }
    
    /**
     * Mixes and blends 2 different colours based on a ratio floating point value
     * @param firstColour       the first colour to be mixed
     * @param secondColour      the second colour to be mixed
     * @param ratio         a constant value that affects how much blending occurs
     * @return      a new colour based on the above 3 values
     */
    private Color mixColours(Color firstColour, Color secondColour, float ratio) {
        int r = (int) ((1-ratio)*(firstColour.getRed()) + (ratio)*(secondColour.getRed()));
        int g = (int) ((1-ratio)*(firstColour.getGreen()) + (ratio)*(secondColour.getGreen()));
        int b = (int) ((1-ratio)*(firstColour.getBlue()) + (ratio)*(secondColour.getBlue()));
        return new Color(r,g,b);
    }
    
    /**
     * @return true if clouds are visible, else false
     */
    public boolean getCloudVisibility() {
        return cloudsVisible;
    }
    
    /**
     * Sets cloud visibility to be true or not
     * @param visible       true if clouds visible, else false
     */
    public void setCloudVisibility(boolean visible) {
        cloudsVisible = visible;
    }
    
    /**
     * @return the current mode of the simulation
     */
    public RenderMode getRenderMode() {
        return renderMode;
    }
    
    /**
     * Sets the mode the simulation is rendering to another mode.
     * @param renderMode        the mode we want the simulation to display
     */
    public void setRenderMode(RenderMode renderMode) {
        this.renderMode = renderMode;
    }
}
