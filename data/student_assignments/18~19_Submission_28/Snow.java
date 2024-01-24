import java.awt.Color;
import java.util.Random;

/**
 * A class that hinders the movement of animals.
 *
 * @version 22/02/2019
 */
public class Snow extends Weather
{
    // Type of cloud: Snow = 2.
    private static final int TYPE = 2;
    
    /**
     * Constructor for objects of class Snow
     */
    public Snow(int row, int col, int radius, int lifespan, Field field)
    {
        super(row, col, radius, lifespan, field);
    }
    
    // Moves the cloud and also adds or deletes information to the field.
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
    
    // Returns whether cloud still exists or not.
    public boolean exists()
    {
        if (lifespan <=0) 
        eject(TYPE);
        return lifespan > 0? true:false;
    }
}