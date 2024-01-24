import java.lang.Math;
import java.util.Random; 
import java.util.List;
import java.util.ArrayList;

/**
 * class Weather - Class that handles weather elements: fog, rain, snow...
 *
 * @version 22/02/2019
 */
public abstract class Weather
{
    // Field that the cloud is over.
    protected Field field;
    
    // Position of cloud.
    protected int row, col;
    // Radius of cloud.
    protected int radius;
    // Lifespan of cloud.
    protected int lifespan;
    // Center block of the cloud.
    protected WeatherBlock center;
    // All of the blocks.
    protected List<WeatherBlock> blocks;

    // Constructor for the Weather class.
    public Weather(int row, int col, int radius, int lifespan, Field field)
    {
        this.row = row;
        this.col = col;
        this.radius = radius;
        this.lifespan = lifespan;
        this.field = field;
        
        // Setting up the weather blocks that make up the cloud.
        blocks = new ArrayList<>();
        // Center block.
        center = new WeatherBlock(row, col, 4);
        blocks.add(center);
        // Filling in each quadrant of the square.
        fillQuadrant(1,1);
        fillQuadrant(-1,1);
        fillQuadrant(1,-1);
        fillQuadrant(-1,-1);
        
        // Filling in center pillar between hemispheres.
        int i, j;
        WeatherBlock currentTop = center, currentBottom = center;
        for (i = 1; i<= radius; i++){
                WeatherBlock aux = new WeatherBlock(row - 3*i, col);
                double distance = calculateDistance(aux.getRow()+1, aux.getCol()+1);
                if (distance <= 3*radius){
                    currentTop.setTop(aux);
                    currentTop = aux;
                    
                    if (distance < radius){
                        currentTop.setType(3);
                    }else if (distance <= 2*radius){
                        currentTop.setType(2);
                    }else{
                        currentTop.setType(1);
                    }
                }else break;
                blocks.add(currentTop);
                
                aux = new WeatherBlock(row + 3*i, col);
                distance = calculateDistance(aux.getRow()+1, aux.getCol()+1);
                if (distance <= 3*radius){
                    currentBottom.setBottom(aux);
                    currentBottom = aux;
                    
                    if (distance < radius){
                        currentBottom.setType(3);
                    }else if (distance <= 2*radius){
                        currentBottom.setType(2);
                    }else{
                        currentBottom.setType(1);
                    }
                }else break;
                blocks.add(currentBottom);
            }
        
    }
    
    
    /*
     * Moves the cloud and its associated weather blocks.
     * 
     * Abstract method, dealt with in Fog, Rain, and Snow respectively.
     */
    public abstract void move();
    
    
    
    // Removes every weather tile of a certain type from ther field.
    protected void eject(int index)
    {
        for (WeatherBlock block : blocks)
        {
            for(int i = block.getRow(); i < block.getRow()+3; i++)
                for (int j = block.getCol(); j < block.getCol()+3; j++)
                if (i>=0 && i<field.getDepth() && j>=0 && j<field.getWidth())
                    field.setWeather(i, j, 0, index);
        }
    }
    
    // Inserts a weather tile on every surface covered by this cloud.
    protected void inject(int index)
    {
        for (WeatherBlock block : blocks)
        {
            for(int i = block.getRow(); i < block.getRow()+3; i++)
                for (int j = block.getCol(); j < block.getCol()+3; j++)
                if (i>=0 && i<field.getDepth() && j>=0 && j<field.getWidth())
                    field.setWeather(i, j, Math.max(block.getType(), field.getWeather(i, j, index)), index);
        }
    }
    
    // Fills in quadrant with blocks.
    protected void fillQuadrant(int x, int y)
    {
        // Creating 3x3 WeatherBlocks that make up the cloud...
        int i, j;
        WeatherBlock current, main = center;
        
        for (j = 1; j <= radius; j++){
            if (y == 1)
            {
                if (main.getRight() != null)
                    main = main.getRight();
                    else{
                        current = new WeatherBlock(main.getRow(), main.getCol() + 3);
                        main.setRight(current);
                        blocks.add(current);
                        main = current;
                        main.setType(3-((main.getCol() - center.getCol())/radius));
                    }
            }else{
                if (main.getLeft() != null)
                    main = main.getLeft();
                    else{
                        current = new WeatherBlock(main.getRow(), main.getCol() - 3);
                        main.setLeft(current);
                        blocks.add(current);
                        main = current;
                        main.setType(3-((center.getCol() - main.getCol())/radius));
                    }
            }
            current = main;
            for (i = 1; i<= radius; i++){
                WeatherBlock aux = new WeatherBlock(row + 3*x*i, col + 3*y*j);
                double distance = calculateDistance(aux.getRow()+1, aux.getCol()+1);
                if (distance <= 3*radius){
                    if (x == 1) current.setTop(aux);
                    else current.setBottom(aux);
                    current = aux;
                    
                    if (distance < radius){
                        current.setType(3);
                    }else if (distance <= 2*radius){
                        current.setType(2);
                    }else{
                        current.setType(1);
                    }
                }else break;
                blocks.add(current);
            }
        }
    }
    
    // Returns list of blocks that make up cloud.
    public List<WeatherBlock> getBlocks()
    {
        return blocks;
    }
    
    protected void setPosition(int row, int col)
    {
        this.row = row;
        this.col = col;
    }
    
    protected void decreaseLife()
    {
        lifespan--;
    }
    
    public int getRow()
    {
        return row;
    }
    
    public int getCol()
    {
        return col;
    }
    
    // Return center of cloud.
    public WeatherBlock getCenter()
    {
        return center;
    }
    
    public int getRadius()
    {
        return radius;
    }
    
    // Returns the distance between the center of the cloud to some point.
    public double calculateDistance(int x, int y)
    {
        // Setting up the true center of the cloud
        int rx = row + 1;
        int ry = col + 1;
        
        return Math.sqrt(Math.pow(rx - x, 2) + Math.pow(ry - y, 2));
    }
    
    // Returns true if cloud still exists, false if it has dissipated.
    public abstract boolean exists();
    
}
