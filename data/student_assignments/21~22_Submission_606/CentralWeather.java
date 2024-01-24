import java.util.List;
/**
 * A class representing the mid point of the weather.
 * Assume the centre of the cloud has the largest rain fall.
 * @version 2022.03.01 (15)
 */
public class CentralWeather extends ActingThing
{
    private int rainFallValue;
    private int fogValue;
    private int maximum_size;
    private int minimum_size;
    //a class counter to let the simulator know how many clouds are there
    private static int counter;

    /**
     * Create the mid point and place it into the field.
     */
    public CentralWeather(Field field, Location location, DateTime dateTime, int rainFallValue, int fogValue,int maximum_size,int minimum_size)
    {
        super(field,location,dateTime);
        this.rainFallValue=rainFallValue;
        this.maximum_size=maximum_size;
        this.minimum_size=minimum_size;
        counter++;
    }

    /**
     * Simulate the movement of the cloud.
     * Only the mid point will move and every time it moves to a new location the mid point will spread.
     * @param things to have the same signature to override the act method from the ActingThing class.
     */
    public void act(List<LivingThing> things)
    throws java.lang.reflect.InvocationTargetException,IllegalAccessException,InstantiationException,NoSuchMethodException
    {  
        decrementRainFallValue();
        if(canAct()){
            Location newLocation = getField().freeAdjacentWeatherLocation(getLocation());
            if(newLocation!=null){
                setLocation(newLocation);
            }
            spread();
        }
    }

    /**
     * The mid point will set the weather with a decaying rain fall value in a random shape(a circle like shape).
     */
    public void spread()
    {
        Location mid = getLocation();
        Field field = getField();
        field.setWeather(mid,rainFallValue,fogValue);
        //basically drawing a circle but with random radii
        for(int i=0;i<=360;i+=5){
            double angle = Math.toRadians(i);
            int r = rand.nextInt(maximum_size-minimum_size+1)+minimum_size;
            int spreadRainFallValue = rainFallValue;
            //set the weather from inside to the outside
            for(int k=1;k<=r;k++){
                //decay the rain fall value
                spreadRainFallValue = rand.nextInt(spreadRainFallValue/2+1)+spreadRainFallValue/2;
                //cos(angle)=x/r
                //x is how many grids to the right
                int x = (int)(Math.round(k*Math.cos(angle)));
                //sin(angle)=y/r
                //y is how many grids to the bottom
                int y = (int)(Math.round(k*Math.sin(angle)));
                //check if the location is outside the field
                if(!(mid.getRow()+y<0||mid.getCol()+x<0||mid.getRow()+y>=field.getDepth()||mid.getCol()+x>=field.getWidth())){
                    Location location = new Location(mid.getRow()+y,mid.getCol()+x);
                    if(!field.hasWeather(mid.getRow()+y,mid.getCol()+x)){
                        field.setWeather(location, spreadRainFallValue,fogValue);
                    }

                }
            }
        }
    }

    /**
     * Decay the rain fall value of the mid point.
     * This can result in the removal of the cloud.
     */
    private void decrementRainFallValue()
    {
        rainFallValue *=0.98;
        if(rainFallValue<20){
            getField().clearCentralWeather(getLocation());
            remove();
            counter--;
        }
    }

    /**
     * Allow the mid point to move from original location to a new location
     */
    protected void setLocation(Location newLocation)
    {
        if(getLocation() != null) {
            getField().clearCentralWeather(getLocation());
        }
        super.setLocation(newLocation);
    }

    /**
     * @return the number of clouds remaining in the field.
     */
    public static int getCounter()
    {
        return counter;
    }
}
