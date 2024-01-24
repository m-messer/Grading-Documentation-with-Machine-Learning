package src;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;

import java.awt.Color;
import javax.swing.JPanel;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @version 2021.03.03
 */
public class SimulatorView extends JFrame implements ActionListener {
    private static final Color EMPTY_COLOR;
    private static final Color UNKNOWN_COLOR;
    private final JLabel stepLabel;
    private final JLabel population;
    private final JLabel speedLabel;
    private final HashMap<Class<?>, Button> actorButtonMap;
    private final ArrayList<Class<?>> actorTypes;
    private final SimulatorView.FieldView fieldView;
    private final Map<Class<?>, Color> colors = new LinkedHashMap<>();
    private final Map<Class<?>, Color> defaultColors = new LinkedHashMap<>();
    private final Map<WeatherTypes, Color> weatherColors = new LinkedHashMap<>();
    private final FieldStats stats = new FieldStats();
    private boolean weatherStatus = false;
    private int lastStep=0;
    private Field lastField=null;

    /**
     * Create an instance of SimulatorView which displays the simulation field -> Animals, Plants and Weather information
     * @param height - height of the SimulatorView
     * @param width - width of the SimulatorView
     */
    public SimulatorView(int height, int width) {
        this.setTitle("Fox and Rabbit Simulation");
        this.stepLabel = new JLabel("Step: ", SwingConstants.CENTER);
        JLabel infoLabel = new JLabel("Simulation  ", SwingConstants.CENTER);
        this.population = new JLabel("Population: ", SwingConstants.CENTER);
        this.setLocation(10, 5);
        this.fieldView = new SimulatorView.FieldView(height, width);
        Container contents = this.getContentPane();
        JPanel infoPane = new JPanel(new BorderLayout());
        infoPane.add(this.stepLabel, "West");
        infoPane.add(infoLabel, "Center");
        contents.add(infoPane, "North");
        contents.add(this.fieldView, "Center");
        contents.add(this.population, "South");
        JPanel buttonsPane = new JPanel(new BorderLayout());
        JPanel playPane = new JPanel(new BorderLayout());
        buttonsPane.add(playPane, "North");

        Button startBut = new Button("Start");
        startBut.addActionListener(this);
        playPane.add(startBut, "North");
        Button stopBut = new Button("Stop");
        stopBut.addActionListener(this);
        playPane.add(stopBut, "South");

        JPanel speedPane = new JPanel();
        speedPane.setLayout(new BoxLayout(speedPane,BoxLayout.Y_AXIS));
        JLabel auxSpeedLabel = new JLabel("Speed:");
        auxSpeedLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
        speedPane.add(auxSpeedLabel);
        speedLabel = new JLabel("x"+delayTime.getMult()+"");
        speedLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
        speedPane.add(speedLabel);
        Button slowBut = new Button("Slower");
        slowBut.addActionListener(this);
        speedPane.add(slowBut);
        Button fastBut = new Button("Faster");
        fastBut.addActionListener(this);
        speedPane.add(fastBut);
        contents.add(buttonsPane, "West");
        buttonsPane.add(speedPane, "South");
        this.pack();
        this.setVisible(true);

        JPanel animalButtons = new JPanel();
        animalButtons.setLayout(new BoxLayout(animalButtons,BoxLayout.X_AXIS));
        JPanel showAll = new JPanel();
        showAll.setLayout(new BoxLayout(showAll,BoxLayout.Y_AXIS));
        animalButtons.add(showAll);
        JPanel showInd = new JPanel();
        showInd.setLayout(new BoxLayout(showInd,BoxLayout.Y_AXIS));
        animalButtons.add(showInd);

        Button showAnimals = new Button("Animals ON");
        showAnimals.setBackground(Color.white);
        Button hideAnimals = new Button("Animals OFF");
        hideAnimals.setBackground(Color.white);
        Button showPlants = new Button("Plants ON");
        showPlants.setBackground(Color.white);
        Button hidePlants = new Button("Plants OFF");
        hidePlants.setBackground(Color.white);
        Button showWeather = new Button("Show Weather");
        showWeather.setBackground(Color.white);
        showAnimals.addActionListener(this);
        hideAnimals.addActionListener(this);
        showPlants.addActionListener(this);
        hidePlants.addActionListener(this);
        showWeather.addActionListener(this);
        showAll.add(showAnimals);
        showAll.add(hideAnimals);
        showAll.add(showPlants);
        showAll.add(hidePlants);
        showAll.add(showWeather);

        actorTypes = new ArrayList<>();
        actorButtonMap = new HashMap<>();
        initializeAnimalTypes();
        Button newButton;
        for(Class<?> clazz : actorTypes) {
            newButton = new Button(clazz.getName());
            newButton.addActionListener(this);
            newButton.setBackground(Color.white);
            actorButtonMap.put(clazz, newButton);
            showInd.add(newButton);
        }
        contents.add(animalButtons,"East");
    }

    /**
     * Override of the actionPerformed method -> sets the actions of the java.awt buttons
     * @param ae - the ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        String acName = ae.getActionCommand();
        boolean show = false;
        switch(acName) {
            case "Faster":
                delayTime.modifyDelay(-1);
                speedLabel.setText("x"+delayTime.getMult());
                break;
            case "Slower":
                delayTime.modifyDelay(1);
                speedLabel.setText("x"+delayTime.getMult());
                break;
            case "Stop":
                delayTime.setPlay(false);
                break;
            case "Start":
                delayTime.setPlay(true);
                break;
            case "Animals ON":
                toggleActors(Animal.class, true);
                show = true;
                break;
            case "Animals OFF":
                toggleActors(Animal.class,false);
                show = true;
                break;
            case "Plants ON":
                toggleActors(Plant.class,true);
                show = true;
                break;
            case "Plants OFF":
                toggleActors(Plant.class,false);
                show = true;
                break;
            case "Show Weather":
                weatherStatus = !weatherStatus;
                show = true;
                break;
            default:
                try {
                    toggleAct(getClass(acName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                show = true;
                break;
        }
        if(show)
            buttonShow(lastField);
    }

    /**
     * get a Class from a name (String)
     * @param s - the name
     * @return Class associated with the name
     * @throws Exception Exception
     */
    private Class<?> getClass(String s) throws Exception {
        return Class.forName(s);
    }

    /**
     * toggle all the actors (and subclasses) of a specified class on or off in the SimulatorView (display/not display specified actors)
     * @param cl - Class of the actors
     * @param onOff boolean - true to turn on, false to turn off
     */
    private void toggleActors(Class<?> cl, boolean onOff){
        for(Class<?> cls : actorTypes){
            if(cl.isAssignableFrom(cls)){
                if(onOff)
                    showColor(cls);
                else removeColor(cls);
            }
        }
    }

    /**
     * toggle a single actor on/off in the SimulatorView (display/not display)
     * @param cl - Class of the actor
     */
    private void toggleAct(Class<?> cl){
        if(colors.get(cl) == null)
            showColor(cl);
        else removeColor(cl);
    }

    /**
     * set the display color of a weather type in the SimulatorView
     * @param type - weather type
     * @param color - the color
     */
    public void setWeatherColor(WeatherTypes type, Color color) {
        weatherColors.put(type, color);
    }

    /**
     * set the color of an actor in the SimulatorView
     * @param cl - class of the actor
     * @param color - the color
     */
    public void setColor(Class<?> cl, Color color) {
        this.colors.put(cl, color);
        this.defaultColors.put(cl, color);
        actorButtonMap.get(cl).setForeground(color);
    }

    /**
     * toggle on an actor in the SimulatorView (display)
     * @param cl - class of the actor
     */
    public void showColor(Class<?> cl){
        this.colors.put(cl,defaultColors.get(cl));
    }

    /**
     * toggle off an actor in the SimulatorView (not display)
     * @param cl - class of the actor
     */
    public void removeColor(Class<?> cl) {
        this.colors.remove(cl);
    }

    /**
     * get the color associated with an actor
     * @param cl - class of the actor
     * @return The color corresponding to the given class, default if not available
     */
    private Color getColor(Class<?> cl) {
        Color col = this.colors.get(cl);
        return col == null ? UNKNOWN_COLOR : col;
    }

    /**
     * update the SimulatorView accordingly and show the updated version
     * @param step - step number
     * @param field - the field
     */
    public void showStatus(int step, Field field) {
        lastStep = step;
        lastField = field;
        if (!this.isVisible()) {
            this.setVisible(true);
        }

        this.stepLabel.setText("Step: " + step);
        this.stats.reset();
        this.fieldView.preparePaint();

        Actor act;
        for(int row = 0; row < field.getDepth(); ++row) {
            for(int col = 0; col < field.getWidth(); ++col) {
                this.fieldView.drawMark(col, row, EMPTY_COLOR);
                for (Iterator<Actor> var5 = field.getActorsAt(row, col).iterator(); var5.hasNext(); this.fieldView.drawMark(col, row, this.getColor(act.getClass()))) {
                    act = var5.next();
                    this.stats.incrementCount(act.getClass());
                }
                if(weatherStatus)
                    this.fieldView.drawMark(col, row, weatherColors.get(field.getLocation(row, col).getWeather()));
            }
        }

        this.stats.countFinished();
        String var10001 = this.stats.getPopulationDetails();
        this.population.setText("Population: " + var10001);
        this.fieldView.repaint();
    }

    private void buttonShow(Field field){
        this.fieldView.preparePaint();
        Actor act;
        for(int row = 0; row < field.getDepth(); ++row) {
            for (int col = 0; col < field.getWidth(); ++col) {
                this.fieldView.drawMark(col, row, EMPTY_COLOR);
                for (Iterator<Actor> var5 = field.getActorsAt(row, col).iterator(); var5.hasNext(); this.fieldView.drawMark(col, row, this.getColor(act.getClass()))){
                    act=var5.next();
                }
                if(weatherStatus)
                    this.fieldView.drawMark(col, row, weatherColors.get(field.getLocation(row, col).getWeather()));
            }
        }
        this.fieldView.repaint();
    }

    /**
     * check if the fieldStats are viable
     * @param field - the field
     * @return boolean - true if viable
     */
    public boolean isViable(Field field) {
        return this.stats.isViable(field);
    }

    static { //define empty_color and unknown_color
        EMPTY_COLOR = Color.white;
        UNKNOWN_COLOR = Color.white;
    }

    /**
     * initialize the actorTypes list -> used to generate buttons
     */
    private void initializeAnimalTypes() {
        actorTypes.add(FruitFly.class);
        actorTypes.add(Butterfly.class);
        actorTypes.add(DragonFly.class);
        actorTypes.add(Frog.class);
        actorTypes.add(Thrush.class);
        actorTypes.add(Wolf.class);
        actorTypes.add(Python.class);
        actorTypes.add(Eagle.class);
        actorTypes.add(Flower.class);
        actorTypes.add(Lavender.class);
        actorTypes.add(Mango.class);
    }

    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR=5;

        private final int gridWidth;
        private final int gridHeight;
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
        @Override
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
