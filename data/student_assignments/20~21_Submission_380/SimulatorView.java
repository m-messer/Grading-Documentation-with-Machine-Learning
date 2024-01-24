import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;
    private final String TIME_OF_DAY = "Time of day: ";
    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String WEATHER = "Weather: ";
    private JLabel stepLabel, population, weatherLabel,timeLabel;
    private FieldView fieldView;
    private boolean day= true;
    private boolean isSunny;
    private boolean itRains;
    private boolean isStorm;
    private boolean isFoggy;
    private static final double RAIN_PROBABILITY = 0.5;

    private static final double SUN_PROBABILITY = 0.5;

    private static final double STORM_PROBABILITY = 0.1;

    private static final double FOG_PROBABILITY = 0.3;
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private int step;
    private String weatherInfo;
    private double weatherProbability;
    Random rand;
    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        timeLabel = new JLabel(TIME_OF_DAY, JLabel.CENTER);
        weatherLabel = new JLabel(WEATHER, JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        
        JPanel infoPane = new JPanel(new BorderLayout());
            infoPane.add(stepLabel, BorderLayout.WEST);
            infoPane.add(weatherLabel, BorderLayout.CENTER);
            infoPane.add(timeLabel, BorderLayout.EAST);
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }
    
    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
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
     */
    public void showStatus(int step, Field field)
    {
        this.step=step;
        if(!isVisible()) {
            setVisible(true);
        }
        if(step%50==0 && step!=0)
        {
            changeTime();
        }
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        timeLabel.setText(TIME_OF_DAY + getTime());
        stats.reset();
        weatherLabel.setText(WEATHER + showWeather());
        stats.reset();
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
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
     * Assigns boolean values to the weather conditons.
     */
    public void getWeather()
    {
      if(weatherInfo=="Stormy")
      {
        isSunny=false;
        itRains=false;
        isStorm=true;
        isFoggy=false;   
      }
      if(weatherInfo=="Foggy")
      {
        isSunny=false;
        itRains=false;
        isStorm=false;
        isFoggy=true;   
      }
      if(weatherInfo=="Rainy")
      {
        isSunny=false;
        itRains=true;
        isStorm=false;
        isFoggy=false;   
      }
      if(weatherInfo=="Sunny")
      {
        isSunny=true;
        itRains=false;
        isStorm=false;
        isFoggy=false;   
      }
    }
    
    /**
     * A weather has a certain probability and every 25 steps a new weather is called.
     */
    public String showWeather()
    {
        if( day && step%25 == 0 ) 
        {
            Random rand = new Random();
            weatherProbability = rand.nextDouble();
            if(weatherProbability<=STORM_PROBABILITY)
            {
                weatherInfo= "Stormy";
                EMPTY_COLOR= Color.white;
            }
            if(weatherProbability>STORM_PROBABILITY && weatherProbability<=FOG_PROBABILITY)
            {
                weatherInfo="Foggy";
                EMPTY_COLOR= Color.gray;
            }
            if(weatherProbability>FOG_PROBABILITY && weatherProbability<=RAIN_PROBABILITY)
            {
                weatherInfo="Rainy";
                EMPTY_COLOR= Color.white;
            }
            if(weatherProbability>RAIN_PROBABILITY)
            {
                weatherInfo="Sunny";
                EMPTY_COLOR= Color.white;
            }
        }
        if(!day)
        {
            weatherInfo="Dark";
            EMPTY_COLOR= Color.black;
        }
        return weatherInfo;
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
    public void changeTime()
    {
      day=!day;
    }
    
    public String getTime()
    {
      if(day){return "Day";}
      else return "Night";
    
    }
}
