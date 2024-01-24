import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a coloured rectangle for each location representing its contents.
 * Colours for each type of species can be defined using the setColor method.
 *
 * @version 2021.03.01
 */
public class GridView extends JFrame implements SimulatorView
{
    // Colour used for objects that have no defined colour.
    private static final Color UNKNOWN_COLOR = Color.gray;
    // Text used for the information shown in the top bar.
    private final String STEP_PREFIX = "Step: ";
    private final String DAY_PREFIX = "Day: ";
    private final String TIME_PREFIX = "Time: ";
    // Labels used for information displayed in the window.
    private final JLabel stepLabel, dayTimeLabel, population;
    // The graphical view of the rectangular field.
    private final FieldView fieldView;
    // The actor information window.
    private final ActorView actorViewWindow;
    // The background colour used for empty locations.
    private Color emptyColor = Color.white;
    // A map for storing colours for participants in the simulation.
    private final Map<String, Color> colors;
    // A statistics object computing and storing simulation information.
    private final FieldStats stats;

    /**
     * Create a view of the given width and height.
     *
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public GridView(int height, int width)
    {
        stats = new FieldStats();
        colors = new HashMap<>();

        setTitle("Bamboo Forest Simulation");

        try {
            // Set the app icon.
            Taskbar taskbar = Taskbar.getTaskbar();
            ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
            setIconImage(icon.getImage());
            taskbar.setIconImage(icon.getImage());  // Display in Dock for Mac
        } catch (Exception ignored) {
        }

        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        dayTimeLabel = new JLabel(DAY_PREFIX + 1 + " " + TIME_PREFIX + "00:00", JLabel.CENTER);
        population = new JLabel("", JLabel.CENTER);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();

        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(stepLabel, BorderLayout.WEST);
        infoPane.add(dayTimeLabel, BorderLayout.EAST);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        //contents.add(parameterView, BorderLayout.EAST);
        pack();
        setVisible(true);
        setLocation(300, 50);  // Place window to the right of the information window
        // Try and make the grid fill the window without resizing needed:
        setSize(fieldView.getWidth(), (int)((double) fieldView.getHeight()*0.94));

        actorViewWindow = new ActorView();
    }
    
    /**
     * Define a colour to be used for a given species.
     *
     * @param species The name of the species.
     * @param color The colour to be used for the given species.
     */
    public void setColor(String species, Color color)
    {
        colors.put(species, color);
    }

    /**
     * Set the background colour used for empty locations.
     *
     * @param emptyColor The new background colour.
     */
    public void setEmptyColor(Color emptyColor) {
        this.emptyColor = emptyColor;
    }

    /**
     * Get a species' colour from its name.
     *
     * @param species The species to be checked.
     * @return The colour to be used for a given class of animal.
     */
    private Color getColor(String species)
    {
        Color col = colors.get(species);
        if(col == null) {
            // No colour defined for this species.
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     *
     * @param step Which iteration step it is.
     * @param day The current day.
     * @param time The current time.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, int day, String time, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        dayTimeLabel.setText(DAY_PREFIX + day + " " + TIME_PREFIX + time);
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Actor actor = (Actor) field.getObjectAt(row, col);
                if(actor != null) {
                    stats.incrementCount(actor.getSpecies());
                    fieldView.drawMark(col, row, getColor(actor.getSpecies()));
                }
                else {
                    fieldView.drawMark(col, row, emptyColor);
                }
            }
        }

        fieldView.drawSelection(field);

        stats.countFinished();

        population.setText(stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     *
     * @param field The field currently occupied.
     * @return true if there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Prepare for a new run.
     */
    public void reset()
    {
        stats.reset();
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is a nested class which defines
     * a custom component for the user interface. This component displays the field.
     */
    private class FieldView extends JPanel
    {
        // The scaling factor of the grid squares.
        private final int GRID_VIEW_SCALING_FACTOR = 6;
        // The width and height of the grid.
        private final int gridWidth, gridHeight;
        // Horizontal and vertical scale.
        private int xScale, yScale;
        // Size of the grid.
        Dimension size;
        // The graphics object being used.
        private Graphics g;
        // The image of the field being used.
        private Image fieldImage;
        // The actor that is currently selected.
        private Actor selectedActor;
        // Whether or not the actor selection has changed.
        private boolean selectionChanged = false;
        // The x and y coordinates of the selected actor.
        private int selectionXPos = 0;
        private int selectionYPos = 0;

        /**
         * Create a new FieldView component.
         *
         * @param height The height of the FieldView.
         * @param width The width of the FieldView.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectionXPos = e.getX() / xScale;
                    selectionYPos = e.getY() / yScale;
                    selectionChanged = true;
                }
            });
        }

        /**
         * Tell the GUI manager how big we would like to be.
         *
         * @return Desired dimension.
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
            if(! size.equals(getSize())) {  // If the size has changed
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
         * Paint on grid location on this field in a given colour.
         *
         * @param x The x-coordinate of the grid location.
         * @param y The y-coordinate of the grid location.
         * @param color The colour to paint this grid location.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * Draw a red-outlined square around the currently selected actor.
         *
         * @param field The field currently occupied.
         */
        public void drawSelection(Field field) {
            if (selectionChanged) {
                selectedActor = (Actor) field.getObjectAt(selectionYPos, selectionXPos);
                selectionChanged = false;
            }

            // Set new position of actor
            if (selectedActor != null) {
                if (selectedActor.getLocation() != null) {
                    selectionXPos = selectedActor.getLocation().getCol();
                    selectionYPos = selectedActor.getLocation().getRow();
                } else {
                    selectedActor = null;
                }
            }
            selectedActor = (Actor) field.getObjectAt(selectionYPos, selectionXPos);

            actorViewWindow.showStats(selectedActor);

            g.setColor(Color.RED);
            g.drawRect(selectionXPos * xScale, selectionYPos * yScale, xScale-2, yScale-2);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         *
         * @param g The graphics object being used.
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
