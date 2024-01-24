
/**
 * A grid that can hold a single animal,plant,watertile,centre point of weather, weathertile
 *
 * @version 2022.03.01 (15)
 */
public class Grid
{
    private Animal animal;
    private Plant plant;
    private CentralWeather cWeather;
    private WaterTile water;
    private WeatherTile weather;
    /**
     * Create an empty grid by default
     */
    public Grid()
    {
    }

    /**
     * Place an actor into the grid.
     */    
    public void place(ActingThing thing)
    {
        if(thing instanceof Animal){
            animal = (Animal)thing;
        }
        else if (thing instanceof Plant){
            plant = (Plant)thing;
        }
        else{
            cWeather = (CentralWeather)thing;
        }
    }

    /**
     * Place a watertile into the grid.
     */ 
    public void setWater()
    {
        water = new WaterTile();
    }

    /**
     * Place a weathertile into the grid.
     * @param rainFallValue, how much rain the tile will give to the plant.
     */ 
    public void setWeather(int rainFallValue,int fogValue)
    {
        weather = new WeatherTile(rainFallValue,fogValue);
    }

    /**
     * @return the living animal in the grid
     */ 

    public Animal getAnimal()
    {
        if(animal!=null&&animal.canAct()){
            return animal;
        }
        return null;
    }

    /**
     * @return the watertile
     */ 
    public WaterTile getWater()
    {
        return water;
    }

    /**
     * @return the weathertile
     */ 
    public WeatherTile getWeather()
    {
        return weather;
    }

    /**
     * @return the centre point of weather
     */ 
    public CentralWeather getCWeather()
    {
        return cWeather;
    }

    /**
     * @return the living plant in the grid
     */ 
    public Plant getPlant()
    {
        if(plant!=null&&plant.canAct()){
            return plant;
        }
        return null;
    }

    /**
     * Set to empty grid
     */ 
    public void clear()
    {
        animal = null;
        plant = null;
        water = null;
        weather = null;
        cWeather = null;
    }

    /**
     * No animal in this grid
     */ 
    public void clearAnimal()
    {
        animal = null;
    }

    /**
     * No plant in this grid
     */ 
    public void clearPlant()
    {
        plant = null;
    }

    /**
     * No weather in this grid
     */ 
    public void clearWeather()
    {
        weather = null;
    }

    /**
     * No central weather in this grid
     */ 
    public void clearCWeather()
    {
        cWeather=null;
    }
}
