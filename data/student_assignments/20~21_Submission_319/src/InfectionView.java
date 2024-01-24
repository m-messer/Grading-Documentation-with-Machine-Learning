package src;

import javax.swing.*;
import java.awt.*;

/**
 * A graphical view showing the number of infected animals for each species.
 * Only one disease is currently implemented in the simulation.
 */
public class InfectionView extends JFrame {
    // The number of implemented animals in the simulation
    private static final int ANIMAL_NO = 8;
    // An instance of FieldStats for counting the number of infected animals
    private final FieldStats infectedStats;
    // An array of JLabels, one for each species
    private final JLabel[] labels;

    /**
     * Creates an instance of InfectionView, which displays the current
     * infection statistics (how many animals are infected for each species)
     * @param width The width of the SimulatorView - used for setLocation
     */
    public InfectionView(int width){
        this.setTitle("Infection Info");

        infectedStats = new FieldStats();

        Container container = this.getContentPane();

        JPanel animalInf = new JPanel();
        animalInf.setLayout(new BoxLayout(animalInf,BoxLayout.Y_AXIS));
        labels = new JLabel[ANIMAL_NO];
        for(int i=0;i< ANIMAL_NO;i++){
            labels[i] = new JLabel();
            animalInf.add(labels[i]);
        }
        container.add(animalInf);

        JPanel infoPan = new JPanel(new BorderLayout());
        JLabel infectedTitle = new JLabel("Infected Animals:");
        JLabel blankLabel = new JLabel("            ");
        infoPan.add(infectedTitle,"Center");
        infoPan.add(blankLabel,"West");

        container.add(infoPan,"North");

        this.setLocation(80+width*5,5);
        this.setPreferredSize(new Dimension(180,180));
        this.pack();
        this.setVisible(true);
    }

    /**
     * Update the infection view.
     * @param field The field which the infection view analyzes.
     */
    public void update(Field field){
        Actor act;

        this.infectedStats.reset();
        for(int row = 0; row < field.getDepth(); ++row) {
            for(int col = 0; col < field.getWidth(); ++col) {
                for (Actor actor : field.getActorsAt(row, col)) {
                    act = actor;
                    if (act instanceof Animal && ((Animal) act).isInfected()) {
                        this.infectedStats.incrementCount(act.getClass());
                    }
                }
            }
        }
        int i=0;
        for(String str : infectedStats.getPopulationDetailsSeparate()){
            labels[i].setText(str);
            i++;
        }
    }
}
