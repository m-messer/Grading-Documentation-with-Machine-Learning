import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A small statistics window that displays details about the currently selected grid cell.
 *
 * @version 2021.03.01
 */
public class ActorView extends JFrame {
    JTextArea statsTextArea;

    /**
     * Create an actor statistics window.
     */
    public ActorView() {
        statsTextArea = new JTextArea();
        add(statsTextArea);

        statsTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        pack();
        setSize(250, 270);
        setLocation(20, 50);
        setVisible(true);
    }


    /**
     * Change the information window to show a certain actor's statistics.
     *
     * @param actor The actor whose statistics will be shown.
     */
    public void showStats(Actor actor) {
        if (actor != null) {
            statsTextArea.setText(actor.getStats());
        } else {
            statsTextArea.setText("Click on an active grid cell to view\nits statistics.");
        }
    }
}
