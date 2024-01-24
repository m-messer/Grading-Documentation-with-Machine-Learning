
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 21.02.2020 (2)
 */
public class SimulatorView extends JFrame
{
    // Color white used for empty locations during the day 
    private static final Color DAY_COLOR = Color.white; 

    // Color black used for empty locations during the night
    private static final Color NIGHT_COLOR = new Color(0,0,0,255);  

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.magenta;
    
    //string text used in the GUI to describe current state of the simulation
    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String CURRENT_WEATHER_PREFIX = "Weather: ";
    private final String CURRENT_TIME_PREFIX = "Time: ";
    
    //Calling the Disease class
    private Disease disease;
    
    //Initialising the frame and bottons
    private JLabel stepLabel, population, infoLabel,time,weatherLoc;    
    private FieldView fieldView;
    private JButton button;
    
    //Button for cloudy weather
    private JButton bCloudy;
    
    //Button for clear weather
    private JButton bClear;
    
    //Button for daytime
    private JButton bDay;
    
    //Button for nightTime
    private JButton bNight;
    
    //Button for Disease
    private JButton bDisease;
    
    //Button for the key
    private JButton bKey;
    
    //Button for automatic day and night
    private JButton bDayNight;
    
    //Button for automatic weather
    private JButton bWeather;
    
    //Button to delete disease
    private JButton bNotDisease;
    
    //Button to stop the simulation
    private JButton bStop;
    
    // Calling the simulator class
    private Simulator sim;
    
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    //Calling the DayNight Class
    private DayNight dayNight;
    
    //Calling the Weather Class
    private Weather weather;
    
    //a variable which stores the time of the day
    private boolean isDay;
    
    //a variable which stores the number of steps
    private int step;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */

    public SimulatorView(int height, int width, Simulator sim)
    {
        dayNight = new DayNight();
        disease = new Disease();
        stats = new FieldStats();
        weather = new Weather();
        colors = new LinkedHashMap<>();

        step = 0;
        //setting the name of the game to the top of the panel
        setTitle("INTO THE DEEP OCEAN");
        
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        time = new JLabel(CURRENT_TIME_PREFIX, JLabel.CENTER);
        weatherLoc = new JLabel(CURRENT_WEATHER_PREFIX, JLabel.CENTER);
        
        //Initialising the buttons
        //this button changes the current time of day to day.
        bDay = new JButton("Day");
        bDay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { dayNight.day(); }
            });
        //this button changes the current time of day to night.
        bNight = new JButton("Night");
        bNight.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { dayNight.night(); }
            });    
        //this button changes the current time of day to automatic mode.
        bDayNight = new JButton("Auto Day/Night");
        bDayNight.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { dayNight.auto(); }
            });    
        //this button changes the current weather to cloudy.
        bCloudy = new JButton("Cloudy");
        bCloudy.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { weather.cloudy();setCloudyWeather(); }
            });
        //this button changes the current weather to clear .
        bClear = new JButton("Clear weather");
        bClear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { weather.clear();setNotCloudyWeather(); }
            });    

        //this button changes the current weather to automatic mode .
        bWeather = new JButton("Auto Weather");
        bWeather.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { weather.auto(); setAutoCloudyWeather();}
            });    
        //this button impliments the disease among the animals.
        bDisease = new JButton("Disease");
        bDisease.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { disease.hasDisease(); }
            });
        //this button cure the animals .
        bNotDisease = new JButton("Disable disease");
        bNotDisease.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {disease.hasNotDisease() ; }
            });        
        //this button prints out the key  
        bKey = new JButton("Key");
        bKey.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { displayKey(); }
            });    
        //this button will stop the simulation 
        bStop = new JButton("Stop !!!");
        bStop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {sim.stop(); }
            });  
      

         
        
        setLocation(100, 50);

        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        //creating a new panel
        JPanel infoPane = new JPanel(new BorderLayout());
        
        //adding the steps to the left corner of the new top label
        infoPane.add(stepLabel, BorderLayout.WEST);
        
        //adding the info label to the center of the new top label
        infoPane.add(infoLabel, BorderLayout.CENTER);
        
        //adding the time to the right corner of the new top label
        infoPane.add(time, BorderLayout.EAST);
        
        //adding the weather to the center of the new top label
        infoPane.add(weatherLoc , BorderLayout.CENTER);
        
        //adding the new label to the top of the main label
        contents.add(infoPane, BorderLayout.NORTH);
        
        //adding the fieldView to the center of the main label 
        contents.add(fieldView, BorderLayout.CENTER);
        
        //adding the population to the bottom of the main label
        contents.add(population, BorderLayout.SOUTH);
        
        //creating a new label for buttons
        JPanel buttonLabel = new JPanel(new BorderLayout());
        
        //seting the size of each button
        buttonLabel.setLayout(new GridLayout(0, 1));

        
        //adding the buttons to the new label created
        //same order appears
        buttonLabel.add(bDay);
        buttonLabel.add(bNight);
        buttonLabel.add(bDayNight);
        buttonLabel.add(bCloudy);
        buttonLabel.add(bClear);
        buttonLabel.add(bWeather);
        buttonLabel.add(bDisease);
        buttonLabel.add(bNotDisease);
        buttonLabel.add(bStop);
        buttonLabel.add(bKey);
        
        
        //adding the new label created for the buttons to the right hand side of the main label
        contents.add(buttonLabel, BorderLayout.EAST);
        pack();

        //seting the visibility to true
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
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // returns the defined unknown colour
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
        //changes the visibility to true if its not true
        if(!isVisible()) {
            setVisible(true);
        }
        
        //returning the step variable to the DayNight Class
        dayNight.computeStep(step);
        
        //returning the step variable and current time to the Weather Class
        weather.computeStep(step, dayNight.isDay());
        
        //displays the current step on the top left corner of the frame 
        stepLabel.setText(STEP_PREFIX + step);
        
        //display the current weather and current weather mode in the top center of the frame
        weatherLoc.setText(CURRENT_WEATHER_PREFIX + weather.showWeather() + ",  " + weather.showMode());
        
        //display the current time of the day and the current time mode in the top right corner of the frame 
        time.setText(CURRENT_TIME_PREFIX + dayNight.showTime() + ",  " + dayNight.showMode());
        
        
        stats.reset();

        
        //checkes the day and weather at the same time
        //then decides which colour should the fields be
        
        fieldView.preparePaint();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                //if its day and the weather is cloudy:
                else if(dayNight.isDay() && weather.isCloudy()){
                    //seting the colour to day
                    fieldView.drawMark(col, row, DAY_COLOR);
                    //seting the colour of the cells to cloudy weather
                    setCloudyWeather();

                }
                //if its day and the weather is not cloudy
                else if(dayNight.isDay() && !weather.isCloudy()){
                    //seting the colour to day
                    fieldView.drawMark(col, row, DAY_COLOR);
                    //seting the colour of the cells to not cloudy weather
                    setNotCloudyWeather();
                }
                //if its night and the weather is cloudy
                else if(!dayNight.isDay() && weather.isCloudy()){
                    //seting the colour to night
                    fieldView.drawMark(col, row, NIGHT_COLOR);
                    //seting the coulour of the cells to cloudy weather
                    setCloudyWeather();

                }
                //if its night and the weather is not cloudy
                else if(!dayNight.isDay() && !weather.isCloudy()){
                    //seting the colour to night
                    fieldView.drawMark(col, row, NIGHT_COLOR);
                    //seting the colour of the cells to not cloudy weather
                    setNotCloudyWeather();
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

    /**
     * Print out the opening message for the player.
     * Prints the key when the user calls the method
     * this method is called when the user presses the 'Key' button
     */
    public void displayKey()
    {
        System.out.println(" Ã¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂ");
        System.out.println(" Ã¢ÂÂ\t Welcome to the 'INTO THE DEEP OCEAN' simulator          Ã¢ÂÂ");
        System.out.println(" Ã¢ÂÂ\t This is the Key:                                        Ã¢ÂÂ");
        System.out.println(" Ã¢ÂÂ\t Shark is Blue                                           Ã¢ÂÂ");
        System.out.println(" Ã¢ÂÂ\t Whale is Pink                                           Ã¢ÂÂ");
        System.out.println(" Ã¢ÂÂ\t Fish is Orange                                          Ã¢ÂÂ");
        System.out.println(" Ã¢ÂÂ\t Shrimp is Red                                           Ã¢ÂÂ");
        System.out.println(" Ã¢ÂÂ\t Plankton is Yellow                                      Ã¢ÂÂ");
        System.out.println(" Ã¢ÂÂ\t Seaweed is Green                                        Ã¢ÂÂ");
        System.out.println(" Ã¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂÃ¢ÂÂ");
    }

    /**
     * this method sets the colour of the cells to the cloudy colour
     * by decreasing its opacity
     */
    private void setCloudyWeather()
    {
        
        setColor(Fish.class, new Color(255,165,0,100)); //set the colour of fish to orange
        setColor(Shark.class, new Color(0,0,255,100));  //set the colour of shark to blue
        setColor(Shrimp.class, new Color(255,0,0,100));  //set the colour of shrimp to red
        setColor(Whale.class, new Color(255,192,203,100)); //set the colour of whale to pink
        setColor(Plankton.class, new Color(255,255,0,100)); //set the colour of plankton to yellow
        setColor(SeaWeed.class, new Color(0,255,0,100)); //set the colour of seaweed to green
    }
    
     /**
     * this method sets the colour of the cells to the not cloudy colour
     * by increasing its opacity
     */
    private void setNotCloudyWeather()
    {
        setColor(Fish.class, new Color(255,165,0,255)); //set the colour of fish to orange
        setColor(Shark.class, new Color(0,0,255,255));  //set the colour of shark to blue
        setColor(Shrimp.class, new Color(255,0,0,255));  //set the colour of shrimp to red
        setColor(Whale.class, new Color(255,192,203,255)); //set the colour of whale to pink
        setColor(Plankton.class, new Color(255,255,0,255)); //set the colour of plankton to yellow
        setColor(SeaWeed.class, new Color(0,255,0,255)); //set the colour of seaweed to green
    }

    /**
     * this method sets the colour of the cells to cloudy/not cloudy colour
     * by checking the weather condition 
     */
    private void setAutoCloudyWeather()
    {
        //if the weather is cloudy         
        if (weather.isCloudy())
        {
            //seting the coulour of the cells to cloudy weather
            setCloudyWeather();         
        }
        
        //if the weather is not cloudy   
        if (!weather.isCloudy())
        {
            //seting the coulour of the cells to not cloudy weather
            setNotCloudyWeather();
        }
    }

}
