package GUI;

import javax.swing.*;
import java.awt.*;

import Environment.Habitat;

/**
     * Provide a graphical view of a rectangular habitat. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the habitat.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    public class HabitatPanel extends JPanel
    {
        
        private final int GRID_SCALE = 2;
        
        private final int GRID_VIEW_SCALING_FACTOR = 6;
        private int gridScale;
        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image habitatImage;

        /**
         * Create a new HabitatViewer component.
         */
        public HabitatPanel(int width, int height)
        {
            gridWidth = width/GRID_SCALE;
            gridHeight = height/GRID_SCALE;
            size = new Dimension(0, 0);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                habitatImage = createImage(size.width, size.height);
                g = habitatImage.getGraphics();

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
         * Paint on grid location on this habitat in a given colour.
         */
        public void drawMark(int x, int y, Color colour)
        {
            g.setColor(colour);
            g.fillRect((x * xScale)/GRID_SCALE, (y * yScale)/GRID_SCALE, (xScale/GRID_SCALE), (yScale/GRID_SCALE));
        }

        /**
         * The habitat view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(habitatImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(habitatImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(habitatImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
        
        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }
    }