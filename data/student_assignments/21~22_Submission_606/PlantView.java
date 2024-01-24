
/**
 * Write a description of class PlantView here.
 *
 * @version (a version number or a date)
 */
public class PlantView extends GridView
{
    public PlantView(int height, int width){
        super(height,width);
        setTitle("Plants Simulator");
        setLocation(1000, 50);
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
                Object thing=field.getPlantAt(row, col);
                if(thing != null) {
                    stats.incrementCount(thing.getClass());
                    fieldView.drawMark(col, row, getColor(thing.getClass()));
                }
                else if ((thing = field.getWaterAt(row, col))!= null) {
                    fieldView.drawMark(col, row, getColor(thing.getClass()));
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
}
