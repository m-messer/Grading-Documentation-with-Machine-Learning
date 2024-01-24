import java.util.List;
import java.util.ArrayList;

/**
 * The 3x3 blocks that make up a Weather cloud. These can be of four types, 
 * depending on how rarified the tiles are.
 *
 * @version 22/02/2019
 */
public class WeatherBlock
{
    // Position of WeatherBlock.
    private int row, col;
    // Determines how rarified this block is: 1..4
    private int type;
    // Stores its possible 4 neighbours.
    private WeatherBlock top, right, bottom, left;

    
    // Constructor for objects of class WeatherBlock that includes type.
    public WeatherBlock(int row, int col, int type)
    {
        this.row = row;
        this.col = col;
        this.type = type;
    }
    
    // Constructor for objects of class WeatherBlock that only specifies position.
    public WeatherBlock(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    
    // Moves the block by a certain amount.
    public void move(int x, int y)
    {
        setPosition(row + x, col + y);
        
        if (top != null) top.move(x, y);
        if (right != null) right.move(x, y);
        if (bottom != null) bottom.move(x, y);
        if (left != null) left.move(x, y);
    }
    
    // Returns a list of this blocks neighbours.
    public List<WeatherBlock> neighbours()
    {
        List<WeatherBlock> neighbours = new ArrayList<WeatherBlock>();
        
        if (top != null) neighbours.add(top);
        if (right != null) neighbours.add(right);
        if (bottom != null) neighbours.add(bottom);
        if (left != null) neighbours.add(left);
        
        return neighbours;
    }
    
    public void setPosition(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public void setTop(WeatherBlock newBlock){
        top = newBlock;
    }
    
    public void setRight(WeatherBlock newBlock){
        right = newBlock;
    }
    
    public void setBottom(WeatherBlock newBlock){
        bottom = newBlock;
    }
    
    public void setLeft(WeatherBlock newBlock){
        left = newBlock;
    }
    
    public WeatherBlock getRight(){
        return right;
    }
    
    public WeatherBlock getLeft(){
        return left;
    }
    
    public int getType()
    {
        return type;
    }
    
    public int getRow()
    {
        return row;
    }
    
    public int getCol()
    {
        return col;
    }
}
