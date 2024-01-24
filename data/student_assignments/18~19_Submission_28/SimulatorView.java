import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 22/02/2019
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.WHITE;
    
    // Colors used for land locations.
    private static final Color LAND_COLOR = Color.GREEN.brighter().brighter();
    
    // Colors used for water locations.
    private static final Color WATER_COLOR = Color.BLUE;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.GRAY;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population, infoLabel;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Wildlife Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        
        JPanel infoPane = new JPanel(new BorderLayout());
            infoPane.add(stepLabel, BorderLayout.WEST);
            infoPane.add(infoLabel, BorderLayout.CENTER);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    
    /**
     * Define a color to be used for a given class.
     * @param aClass The Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class aClass, Color color)
    {
        colors.put(aClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class.
     */
    private Color getColor(Class aClass)
    {
        Color col = colors.get(aClass);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    if (animal.getClass() != Plant.class)
                    {
                        Animal a = (Animal) animal;
                        if (a.isHealthy()) fieldView.drawMark(col, row, getColor(animal.getClass())); // Healthy animals use their normal colour.
                        else fieldView.drawMark(col, row, Color.RED); // Sick animals are shown as RED.
                    }else fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else if(field.getLand(row, col)) {
                    fieldView.drawMark(col, row, LAND_COLOR); // Draw land tile
                }
                else if(!field.getLand(row, col)) {
                    fieldView.drawMark(col, row, WATER_COLOR); // Draw water tile.
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
                
                // Draws cloud tiles with a specific transparency already initialized in Simulator. Depeding on how far from center, ther eis a smaller chance to appear.
                Random rand = new Random();
                double chance = rand.nextDouble();
                if (chance <= field.getWeather(row, col, 0)*0.25) fieldView.drawMark(col, row, getColor(Fog.class));
                chance = rand.nextDouble();
                if (chance <= field.getWeather(row, col, 1)*0.25) fieldView.drawMark(col, row, getColor(Rain.class));
                chance = rand.nextDouble();
                if (chance <= field.getWeather(row, col, 2)*0.25) fieldView.drawMark(col, row, getColor(Snow.class));
                    
                fieldView.drawMark(col, row, new Color(0, 0, 0, Time.sunlight()));
            }
        }
        
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }
    
    

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
