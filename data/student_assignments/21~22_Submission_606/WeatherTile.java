import java.util.List;
/**
 * A class represent a weatherTile which will rain
 *
 * @version 2022.03.01 (15)
 */
public class WeatherTile
{
    //how much water it gives to the plant on the same grid
    private int rainFallValue;
    private int fogValue;
    /**
     * Create a weather tile
     * @param rainFallValue How much water it gives to the plant on the same grid. Must be greater than zero.
     */
    public WeatherTile(int rainFallValue,int fogValue)
    {
        this.rainFallValue=rainFallValue;
        this.fogValue=fogValue;
    }
    /**
     * @return the amount of rain in the grid.
     */
    public int getRainFallValue()
    {
        return rainFallValue;
    }
    public int getFogValue()
    {
        return fogValue;
    }

}
