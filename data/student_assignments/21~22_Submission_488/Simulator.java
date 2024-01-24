import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A graphical interface that simualtes a predator-prey habitat for a given
 * time.
 *
 */
public class Simulator {
    // The info label.
    private final JLabel infoLabel = new JLabel("Information", JLabel.CENTER);
    // The camera panel.
    private final CameraDrone cameraPanel = new CameraDrone(900, 900);
    // The stats label.
    private final JLabel statsLabel = new JLabel("Statistics", JLabel.CENTER);

    /**
     * Construct a simulatior.
     */
    public Simulator() {
        JFrame frame = new JFrame();
        Container container = frame.getContentPane();

        container.add(infoLabel, BorderLayout.NORTH);
        container.add(cameraPanel, BorderLayout.CENTER);
        container.add(statsLabel, BorderLayout.SOUTH);

        infoLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        statsLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setTitle("Simulation");
        frame.setVisible(true);
    }

    /**
     * Updates the screen.
     */
    private void updateScreen() {
        cameraPanel.captureNewImage();
        infoLabel.setText("Day: " + cameraPanel.day() + "     " + cameraPanel.time() +"     " + cameraPanel.weather());
        statsLabel.setText("<html>Population: " + cameraPanel.stats() + "</html>");
    }

    /**
     * Run the simulator for thirty seconds in real time.
     */
    public void simulateForThirtySeconds() {
        simulateFor(200);
    }

    /**
     * Run the simulator from its current state for the given number of seconds.
     * Stop before the given number of seconds if the camera doesn't detect any
     * change.
     * 
     * @param turn The number of turns to simulate for.
     */
    public void simulateFor(int turns) {
        for (int time = 0; time < turns; time ++) {
            updateScreen();

            if (!cameraPanel.isDetectingChange())
                return;

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Sleep exception.");
            }
        }
    }
}
