import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2020.02.23
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String TIME_PREFIX = "TIME: ";
    private final String WEATHER_PREFIX = "WEATHER: ";
    private final String INFECTED_PREFIX = "Infected: ";
    private JLabel stepLabel, population, infoLabel, timeLabel, infectedLabel;
    private FieldView fieldView;
    
    private JButton resetButton;

    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Create a view of the given width and height.
     * Also display the stats of simulation
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("African Savanna");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel(WEATHER_PREFIX, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX, JLabel.CENTER);
        infectedLabel = new JLabel(INFECTED_PREFIX, JLabel.CENTER);
        resetButton = new JButton("RESET");

        setLocation(100, 50);

        fieldView = new FieldView(height, width);

        Container contents = getContentPane();

        JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.add(stepLabel, BorderLayout.WEST);
            infoPanel.add(infoLabel, BorderLayout.CENTER);
            infoPanel.add(timeLabel, BorderLayout.EAST);

        JPanel populationStatsPanel = new JPanel(new BorderLayout());
            populationStatsPanel.add(population, BorderLayout.NORTH);
            populationStatsPanel.add(infectedLabel, BorderLayout.CENTER);
            populationStatsPanel.add(resetButton, BorderLayout.SOUTH);

        contents.add(infoPanel, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(populationStatsPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    /**
     * Update the infectedLabel
     * @param number The number of infected animals.
     * @param infectedSpecies The HashSet of Strings that contains the name of infected species.
     */
    public void setInfectedLabel(int number, HashSet<String> infectedSpecies) 
    {
        infectedLabel.setText(INFECTED_PREFIX + number + " animals, infected species: " + infectedSpecies);
        if(number == 0) {
            infectedLabel.setText(INFECTED_PREFIX + number + " animals, infected species: 0");
        }
    }

    /**
     * When pressed, the resetButton should reset the simulation.
     * @param simulator The simulator makes the button works in our current simulation.
     * @return resetButton Returns the resetButton.
     */
    public JButton resetButton(Simulator simulator) 
    {
            resetButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) 
                {
                    simulator.reset();
                }
            });
            return resetButton;
    }

    /**
     * Define a color to be used for a given class of animal.
     * @param actorClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class actorClass, Color color)
    {
        colors.put(actorClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class actorClass)
    {
        Color col = colors.get(actorClass);
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
     * @param weather The current weather to be displayed.
     */
    public void showStatus(int step, String time, Field field, Weather weather)
    {
        if(!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);

        if(weather.isRaining()) {
            infoLabel.setText(WEATHER_PREFIX + "Raining");
        }else if(weather.isSunny()) {
            infoLabel.setText(WEATHER_PREFIX + "Sunny");
        }else if(weather.isWindy()) {
            infoLabel.setText(WEATHER_PREFIX + "Windy");
        }

        //infoLabel.setText(WEATHER_PREFIX + );
        timeLabel.setText(TIME_PREFIX + time);
        stats.reset();

        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object actor = field.getObjectAt(row, col);
                if(actor != null) {
                    stats.incrementCount(actor.getClass());
                    fieldView.drawMark(col, row, getColor(actor.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
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
