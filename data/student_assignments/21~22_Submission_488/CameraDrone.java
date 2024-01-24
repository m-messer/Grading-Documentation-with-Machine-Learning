import java.awt.*;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * A camera panel that paints the image of the habitat from a bird-eye view.
 *
 */
public class CameraDrone extends JPanel {
    // Auto-generated ID;
    private static final long serialVersionUID = -5971042352536874683L;
    // The scale factor between coordinates on the habitat and pixels on the image
    private final int SCALE = 10;
    // The habitat the drone is filming.
    private Habitat habitat;

    /**
     * Construct a simulatior.
     * 
     * @param width  The width of this panel in pixels;
     * @param height The height of this panel in pixels;
     */
    public CameraDrone(int width, int height) {
        this.habitat = new Habitat(width / SCALE, height / SCALE);
        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Paints the image of the habitat.
     * 
     */
    public void captureNewImage() {
        this.habitat.passHalfDay();
        this.repaint();
    }

    /**
     * Provide the statistics of the habitat.
     * 
     * @return a string of statistics;
     */
    public String stats() {
        StringBuffer buffer = new StringBuffer();

        HashMap<String, Integer> data = habitat.speciesData;
        for (String type : data.keySet()) {
            String[] speciesColor = type.split(",");
            buffer.append(String.format("<font color='%s'>", speciesColor[1]));
            buffer.append(speciesColor[0]);
            buffer.append("</font>");
            buffer.append(": ");
            buffer.append(data.get(type));
            buffer.append(' ');
        }

        return buffer.toString();
    }

    /**
     * Gets the current day.
     * 
     * @return simulated day of this habitat.
     */
    public int day() {
        return this.habitat.time() / 24;
    }
    
    /**
     * Gets the current weather of this habitat.
     * 
     * @return simulated weather of this habitat.
     */
    public String weather() {
        return habitat.isRaining() ? "Raining" : "Drought";
    }

    /**
     * Provides whether the habitat is still active.
     * 
     * @return true if habitat is changing, false if not;
     */
    public boolean isDetectingChange() {
        return habitat != null && habitat.isAlive();
    }

    /**
     * Draws a gray outline around each rectangle on the image.
     * 
     * @param g     The Graphics object;
     * @param point The Coordinate of the rectangle;
     */
    private void drawOutline(Graphics g, Point point) {
        g.setColor(Color.GRAY);
        g.drawRect(point.x * SCALE, point.y * SCALE, SCALE, SCALE);
    }

    /**
     * Draw the image of the habitat.
     * 
     * * @param g The Graphics object;
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.habitat 
                != null) {
            HashMap<Point, Color> view = habitat.topView;
            
            for (Point point : view.keySet()) {
                g.setColor(view.get(point));
                g.fillRect(point.x * SCALE, point.y * SCALE, SCALE, SCALE);
                drawOutline(g, point);
            }
        }
    }
    
    /**
     * Gets the current time.
     * 
     * @return simulated time of this habitat.
     */
    public String time() {
        return habitat.nightTime() ? "Mid-Night" : "NoonTime";
    }
}
