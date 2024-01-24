import java.awt.*;
/**
 * Write a description of class WeatherView here.
 *
 * @version (a version number or a date)
 */
public class WeatherView extends GridView
{
    public WeatherView(int height, int width){
        super(height,width);
        setTitle("Weather Simulator");
        setLocation(500, 100);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(DateTime dateTime, Field field)
    {
        super.showStatus(dateTime,field);

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                WeatherTile thing=field.getWeatherAt(row, col);
                if(thing != null) {
                    stats.incrementCount(thing.getClass());
                    fieldView.drawMark(col, row, getColor(thing));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }

            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    private Color getColor(WeatherTile weather)
    {
        int rainFallValue = weather.getRainFallValue();

        if(rainFallValue>=150){
            return Color.RED;
        }
        else if(rainFallValue>=100){
            return Color.ORANGE;
        }
        else if(rainFallValue>=50){
            return Color.YELLOW;
        }
        else if(rainFallValue>0){
            return Color.BLUE;
        }
        return EMPTY_COLOR;
    }
}
