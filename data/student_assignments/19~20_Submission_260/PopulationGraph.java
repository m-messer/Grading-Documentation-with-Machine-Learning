import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;

/**
 * The PopulationGraph provides a view of the populations of actors in the field as a line graph
 * over time. 
 *
 * @version 2020.02
 */
public class PopulationGraph implements SimulatorView
{
    private static final Color LIGHT_GRAY = new Color(0, 0, 0, 40);
    
    private static JFrame frame;
    private static JPanel bottom;
    private static GraphPanel graph;
    private static JLabel stepLabel;
    private static HashMap<Class<?>, JLabel> countLabels;

    // The classes being tracked by this view
    private ArrayList<Class<?>> classes;
    // A map for storing colors for participants in the simulation
    private Map<Class<?>, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Constructor.
     * 
     * @param width The width of the plotter window (in pixels).
     * @param height The height of the plotter window (in pixels).
     * @param startMax The initial maximum value for the y axis.
     */
    public PopulationGraph(int width, int height, int startMax)
    {
        stats = new FieldStats();
        classes = new ArrayList<>();
        colors = new HashMap<>();
        
        if (frame == null) {
            frame = makeFrame(width, height, startMax);
        }
        else {
            graph.newRun();
        }
        
    }

    /**
     * Define a color to be used for a given class of animal, and initialise the animal's label.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
        classes = new ArrayList<>(colors.keySet());
        
        //add label for each class to display population count
        JLabel label = new JLabel(" ");
        label.setForeground(color);
        countLabels.put(animalClass, label);
        bottom.add(label);
    }

    /**
     * Show the current status of the field. The status is shown by displaying a line graph for
     * two classes in the field. This view currently does not work for more (or fewer) than exactly
     * two classes. If the field contains more than two different types of animal, only two of the classes
     * will be plotted.
     * 
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     * @param isDay, true=day, false=night. *Currently not used
     */
    public void showStatus(int step, Field field, boolean isDay, int currentDarkness)
    {
        graph.update(step, field, stats);
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
     * Prepare for a new run.
     */
    public void reset()
    {
        stats.reset();
        graph.newRun();
    }
    
    /**
     * Prepare the frame for the graph display.
     */
    private JFrame makeFrame(int width, int height, int startMax)
    {
        JFrame frame = new JFrame("Graph View");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        Container contentPane = frame.getContentPane();

        graph = new GraphPanel(width, height, startMax);
        contentPane.add(graph, BorderLayout.CENTER);

        bottom = new JPanel();
        bottom.add(new JLabel("Step:"));
        stepLabel = new JLabel("");
        bottom.add(stepLabel);
        countLabels = new HashMap<>();
        
        contentPane.add(bottom, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocation(20, 600);

        frame.setVisible(true);

        return frame;
    }

    // ============================================================================
    
    /**
     * Nested class: a component to display the graph.
     */
    class GraphPanel extends JComponent
    {
        private static final double SCALE_FACTOR = 0.8;

        // An internal image buffer that is used for painting. For
        // actual display, this image buffer is then copied to screen.
        private BufferedImage graphImage;
        private int[] lastVal = new int[5];
        private int yMax;

        /**
         * Create a new, empty GraphPanel.
         */
        public GraphPanel(int width, int height, int startMax)
        {
            graphImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            clearImage();
            lastVal[0] = height;
            lastVal[1] = height;
            lastVal[2] = height;
            lastVal[3] = height;
            lastVal[4] = height;
            
            yMax = startMax;
        }

        /**
         * Indicate a new simulation run on this panel.
         */
        public void newRun()
        {
            int height = graphImage.getHeight();
            int width = graphImage.getWidth();

            Graphics g = graphImage.getGraphics();
            g.copyArea(4, 0, width-4, height, -4, 0);            
            g.setColor(Color.WHITE);
            g.drawLine(width-4, 0, width-4, height);
            g.drawLine(width-2, 0, width-2, height);
            lastVal[0] = height;
            lastVal[1] = height;
            lastVal[2] = height;
            lastVal[3] = height;
            lastVal[4] = height;
            repaint();
        }

        /**
         * Dispay a new point of data.
         */
        public void update(int step, Field field, FieldStats stats)
        {
            if (classes.size() >= 2) {
                stats.reset();
                
                Graphics g = graphImage.getGraphics();

                int height = graphImage.getHeight();
                int width = graphImage.getWidth();

                // move graph one pixel to left
                g.copyArea(1, 0, width-1, height, -1, 0);
                
                // iterate over all animal classes
                for(int i =0; i < classes.size(); i++) {
                    Class<?> class1 = classes.get(i);
                    String classname = class1.toString().substring(5);
    
                    int count1 = stats.getPopulationCount(field, class1);
                    
                    
                    // calculate y, check whether it's out of screen. scale down if necessary.
                    int y = height - ((height * count1) / yMax) - 1;
                    while (y<0) {
                        scaleDown();
                        y = height - ((height * count1) / yMax) - 1;
                    }
                    g.setColor(LIGHT_GRAY);
                    g.drawLine(width-2, y, width-2, height);
                    g.setColor(colors.get(class1));
                    g.drawLine(width-3, lastVal[i], width-2, y);
                    lastVal[i] = y;
                    
                    countLabels.get(class1).setText(classname + " " + count1);
                }    
                
                repaint();
                
                stepLabel.setText("" + step);
            }
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
            lastVal[0] = oldTop + (int) (lastVal[0] * SCALE_FACTOR);
            lastVal[1] = oldTop + (int) (lastVal[1] * SCALE_FACTOR);
            lastVal[2] = oldTop + (int) (lastVal[2] * SCALE_FACTOR);
            lastVal[3] = oldTop + (int) (lastVal[3] * SCALE_FACTOR);
            lastVal[4] = oldTop + (int) (lastVal[4] * SCALE_FACTOR);
            
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
         * This component is opaque.
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
