import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location representing its contents.
 * The statistics of the field are displayed on the right of the grid.
 * Colors for each type of species can be defined using the setColor method.
 *
 * @version 16.03.2022
 */
public class GridView extends JFrame implements SimulatorView
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String DAYS_PREFIX = "Days Passed: ";
    private final String INFECTIONS_PREFIX = "Currently infected: ";
    private JLabel stepLabel, population, days, timeOfDay, infections, whalePop, sealPop, sharkPop, seahorsePop, algaePop, fishPop;
    private FieldView fieldView;
    private Simulator simulator;
    // A map for storing colors for participants in the simulation
    private Map<Class<?>, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    
    private JPanel statsPanel;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem simOneStepItem, resetItem, quitItem;
    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public GridView(int height, int width, Simulator simulator)
    {
        stats = new FieldStats();
        colors = new HashMap<>();
        this.simulator = simulator;


        setTitle("Aquatic Habitat Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        population = new JLabel("Populations:", JLabel.CENTER);
        whalePop= new JLabel ("", JLabel.CENTER);
        sealPop= new JLabel ("", JLabel.CENTER);
        sharkPop= new JLabel ("", JLabel.CENTER);
        fishPop= new JLabel ("", JLabel.CENTER);
        seahorsePop= new JLabel ("", JLabel.CENTER);
        algaePop= new JLabel ("", JLabel.CENTER);
        days = new JLabel(DAYS_PREFIX, JLabel.CENTER);
        timeOfDay = new JLabel("", JLabel.CENTER);
        infections = new JLabel(INFECTIONS_PREFIX,JLabel.CENTER);
        
        
        menuBar = new JMenuBar();
        menu = new JMenu("Run Simulation");
        menuBar.add(menu);
             
        simOneStepItem = menu.add(menuItem("Simulate One Step",'S', KeyEvent.VK_S));
        simOneStepItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { simOneStepButton(); }});
        
        menu.addSeparator();
        resetItem = menu.add(menuItem("Reset",'R', KeyEvent.VK_R));
        resetItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { resetButton(); }});
        
        menu.addSeparator();
        quitItem = menu.add(menuItem("End Simulation",'E', KeyEvent.VK_E));
        quitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { System.exit(0); }});
        
        
        statsPanel = new JPanel();
        
        BoxLayout boxlayout = new BoxLayout(statsPanel, BoxLayout.Y_AXIS);
        statsPanel.setLayout(boxlayout);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 5)));
        statsPanel.add(stepLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(days);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(infections);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(timeOfDay);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(population);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(whalePop);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(sharkPop);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(sealPop);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(fishPop);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(algaePop);
        statsPanel.add(Box.createRigidArea(new Dimension(30, 20)));
        statsPanel.add(seahorsePop);
        
        setLocation(0, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        contents.add(menuBar, BorderLayout.NORTH);
        contents.add(statsPanel, BorderLayout.CENTER);
        contents.add(fieldView, BorderLayout.WEST);
        
        pack();
        setVisible(true);
    }
    
    public void simOneStepButton()
    {
        simulator.simulateOneStep();
    }
    
    public void resetButton()
    {
        simulator.reset();
    }
    
    public static JMenuItem menuItem(String label, int mnemonic, int acceleratorKey) 
    {
        JMenuItem item = new JMenuItem(label);
        if (mnemonic != 0) {
            item.setMnemonic((char) mnemonic);
        }
        if (acceleratorKey != 0) {
              item.setAccelerator(KeyStroke.getKeyStroke(acceleratorKey, java.awt.Event.CTRL_MASK));
        }
        return item;
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class<?> animalClass)
    {
        Color col = colors.get(animalClass);
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
     * @param day How many days have passed.
     * @param night If it is daytime or nighttime.
     */
    public void showStatus(int step, Field field, int day,int infection, boolean night)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        days.setText(DAYS_PREFIX + day);
        infections.setText(INFECTIONS_PREFIX + infection);
        
        
        if(night){
            timeOfDay.setText("Night");
        }
        else{
            timeOfDay.setText("Day");
        }
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    Class<?> cls = animal.getClass();
                    stats.incrementCount(cls);
                    fieldView.drawMark(col, row, getColor(cls));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();

        //population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        whalePop.setText("Whale: " + stats.getPopulationCount(field, Whale.class));
        sharkPop.setText("Shark: " + stats.getPopulationCount(field, Shark.class));
        sealPop.setText("Seal: " + stats.getPopulationCount(field, Seal.class));
        seahorsePop.setText("Seahorse: " + stats.getPopulationCount(field, Seahorse.class));
        algaePop.setText("Algae: " + stats.getPopulationCount(field, Algae.class));
        fishPop.setText("Fish: " + stats.getPopulationCount(field, Fish.class));
        
        
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
     * Prepare for a new run.
     */
    public void reset()
    {
        stats.reset();
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
