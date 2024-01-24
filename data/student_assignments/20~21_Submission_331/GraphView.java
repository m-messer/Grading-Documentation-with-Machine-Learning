import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;

/**
 * The GraphView provides a view of all populations of actors in the field as a line graph
 * over time.
 *
 * @version 2021.03.01
 */
public class GraphView implements SimulatorView
{
    // The frame used to create the window.
    private static JFrame frame;
    // The component used to display the graph.
    private static GraphPanel graph;
    // The label used to show what step number the simulation is on.
    private static JLabel stepLabel;
    // The label used to show the count of each population.
    private static JLabel countLabel;
    // The actor species being tracked by this view.
    private Set<String> speciesSet;
    // A map for storing the colours for participants in the simulation.
    private final Map<String, Color> colors;
    // A statistics object computing and storing simulation information.
    private final FieldStats stats;

    /**
     * Constructor for the Graph view.
     *
     * @param width The width of the plotter window (in pixels).
     * @param height The height of the plotter window (in pixels).
     * @param startMax The initial maximum value for the y-axis.
     */
    public GraphView(int width, int height, int startMax)
    {
        stats = new FieldStats();
        speciesSet = new HashSet<>();
        colors = new HashMap<>();

        if (frame == null) {
            frame = makeFrame(width, height, startMax);
        }
        else {
            graph.newRun();
        }

        //showStatus(0, null);
    }

    /**
     * Define a colour to be used for a given actor on the graph.
     *
     * @param species The species of the actor.
     * @param color The colour to be used for the given actor.
     */
    public void setColor(String species, Color color)
    {
        colors.put(species, color);
        speciesSet = colors.keySet();
    }

    /**
     * Implementation of setEmptyColor from SimulatorView interface.
     */
    public void setEmptyColor(Color color) {}

    /**
     * Show the current status of the field, by displaying a line graph for all actors of the field.
     * 
     * @param step Which iteration step it is.
     * @param day The current day.
     * @param time The current time.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, int day, String time, Field field)
    {
        graph.update(step, field, stats);
    }

    /**
     * Determine whether the simulation is still viable, so if it should continue to run.
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
        graph.newRun();
    }
    
    /**
     * Prepare the frame for the graph display.
     *
     * @param width The width of the plotter window (in pixels).
     * @param height The height of the plotter window (in pixels).
     * @param startMax The initial maximum value for the y-axis.
     * @return The JFrame used to create the window.
     */
    private JFrame makeFrame(int width, int height, int startMax)
    {
        JFrame frame = new JFrame("Graph View");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        Container contentPane = frame.getContentPane();

        graph = new GraphPanel(width, height, startMax);
        contentPane.add(graph, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Step:"));
        stepLabel = new JLabel("");
        bottom.add(stepLabel);
        countLabel = new JLabel(" ");
        bottom.add(countLabel);
        contentPane.add(bottom, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocation(20, 650);  // Place window below the main grid and information windows

        frame.setVisible(true);

        return frame;
    }

    // ============================================================================

    /**
     * Nested class: a component to display the graph.
     */
    class GraphPanel extends JComponent
    {
        // How much to scale the graph by initially, and how much to scale it up by when more space is needed.
        private static final double SCALE_FACTOR = 0.9;
        // An internal image buffer that is used for painting.
        // For actual display, this image buffer is then copied to screen.
        private final BufferedImage graphImage;
        // The last values displayed.
        private int lastVal1, lastVal2;
        // The current highest value on the y-axis.
        private int yMax;

        /**
         * Create a new, empty GraphPanel.
         *
         * @param width The width of the plotter window (in pixels).
         * @param height The height of the plotter window (in pixels).
         * @param startMax The initial maximum value for the y-axis.
         */
        public GraphPanel(int width, int height, int startMax)
        {
            graphImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            clearImage();
            lastVal1 = height;
            lastVal2 = height;
            yMax = startMax;
        }

        /**
         * Indicate a new simulation run on this panel.
         */
        public void newRun()
        {
            int height = graphImage.getHeight();
            lastVal1 = height;
            lastVal2 = height;
            repaint();
        }

        /**
         * Display a new point of data on the graph.
         *
         * @param step Which iteration step it is.
         * @param field The field whose status is to be displayed.
         * @param stats A statistics object computing and storing simulation information.
         */
        public void update(int step, Field field, FieldStats stats)
        {
            stats.reset();
            Graphics g = graphImage.getGraphics();
            int height = graphImage.getHeight();
            int width = graphImage.getWidth();
            // Move graph one pixel to left.
            g.copyArea(1, 0, width-1, height, -1, 0);
            for (String species : speciesSet) {
                int count = stats.getPopulationCount(field, species);

                // Calculate y, check whether it's out of screen. Scale down if necessary.
                int y = height - ((height * count) / yMax) - 1;
                while (y < 0) {
                    scaleDown();
                    y = height - ((height * count) / yMax) - 1;
                }
                if (count > 0) {
                    g.setColor(colors.get(species));
                    g.drawLine(width - 2, y, width - 2, y + 2);
                }
                lastVal1 = y;
            }

            repaint();

            // Reset bottom information text.
            stepLabel.setText("" + step);
            countLabel.setText(stats.getPopulationDetails(field));
        }

        /**
         * Scale the current graph down vertically to make more room at the top.
         */
        public void scaleDown()
        {
            Graphics g = graphImage.getGraphics();
            int height = graphImage.getHeight();
            int width = graphImage.getWidth();

            BufferedImage tmpImage = new BufferedImage(width, (int)(height*SCALE_FACTOR), 
                                                       BufferedImage.TYPE_INT_RGB);
            Graphics2D gtmp = (Graphics2D) tmpImage.getGraphics();

            gtmp.scale(1.0, SCALE_FACTOR);
            gtmp.drawImage(graphImage, 0, 0, null);

            int oldTop = (int) (height * (1.0-SCALE_FACTOR));

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, oldTop);
            g.drawImage(tmpImage, 0, oldTop, null);

            yMax = (int) (yMax / SCALE_FACTOR);
            lastVal1 = oldTop + (int) (lastVal1 * SCALE_FACTOR);
            lastVal2 = oldTop + (int) (lastVal2 * SCALE_FACTOR);

            repaint();
        }

        /**
         * Clear the image on this panel.
         */
        final public void clearImage()
        {
            Graphics g = graphImage.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, graphImage.getWidth(), graphImage.getHeight());
            repaint();
        }

        // The following methods are redefinitions of methods
        // inherited from superclasses.

        /**
         * Tell the layout manager how big we would like to be.
         * (This method gets called by layout managers for placing
         * the components.)
         * 
         * @return The preferred dimension for this component.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(graphImage.getWidth(), graphImage.getHeight());
        }

        /**
         * Return whether this component is opaque.
         *
         * @return true
         */
        public boolean isOpaque()
        {
            return true;
        }

        /**
         * This component needs to be redisplayed. Copy the internal image 
         * to screen. (This method gets called by the Swing screen painter 
         * every time it want this component displayed.)
         * 
         * @param g The graphics context that can be used to draw on this component.
         */
        public void paintComponent(Graphics g)
        {
            if(graphImage != null) {
                g.drawImage(graphImage, 0, 0, null);
            }
        }
    }
}
