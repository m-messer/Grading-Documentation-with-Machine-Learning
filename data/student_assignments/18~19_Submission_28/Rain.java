import java.awt.Color;
import java.util.Random;

/**
 * A class that improves the growth rate of plants.
 *
 * @version 22/02/2019
 */
public class Rain extends Weather
{
    private static final int TYPE = 1;

    /**
     * Constructor for objects of class Rain
     */
    public Rain(int row, int col, int radius, int lifespan, Field field)
    {
        super(row, col, radius, lifespan, field);
    }
    
    // Moves the cloud and adds or deletes information from the field.
    public void move(){
        Random rand = new Random();
        
        int choice = rand.nextInt(4);
        
        eject(TYPE);
        if (choice == 0){ // Moves up.
            setPosition(row-1, col);
            center.move(-1,0);
        }else if (choice == 1){ // Moves right.
            setPosition(row, col+1);
            center.move(0,1);
        }else if (choice == 2){ // Moves down.
            setPosition(row+1, col);
            center.move(1,0);
        }else if (choice == 3){ // Moves left.
            setPosition(row, col-1);
            center.move(0,-1);
        }
        decreaseLife();
        inject(TYPE);
    }
    
    // Returns whether cloud still exists.
    public boolean exists()
    {
        if (lifespan <=0) 
        eject(TYPE);
        return lifespan > 0? true:false;
    }
}