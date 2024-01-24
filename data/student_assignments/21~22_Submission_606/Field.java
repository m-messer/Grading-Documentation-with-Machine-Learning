import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Queue;
import java.util.HashSet;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a grid.
 *
 * @version 2022.03.01
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();

    // The depth and width of the field.
    private int depth, width;
    // Storage for the grids
    private Grid[][] field;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Grid[depth][width];
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = new Grid();
            }
        }
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col].clear();
            }
        }
    }

    /**
     * Clear the animal in given location.
     * @param location The location to clear.
     */
    public void clearAnimal(Location location)
    {
        field[location.getRow()][location.getCol()].clearAnimal();
    }

    /**
     * Clear the plant in given location.
     * @param location The location to clear.
     */
    public void clearPlant(Location location)
    {
        field[location.getRow()][location.getCol()].clearPlant();
    }

    /**
     * Clear the weather in given location.
     * @param location The location to clear.
     */
    public void clearWeather(Location location)
    {
        field[location.getRow()][location.getCol()].clearWeather();
    }

    /**
     * Clear the weather mid point  in given location.
     * @param location The location to clear.
     */
    public void clearCentralWeather(Location location)
    {
        field[location.getRow()][location.getCol()].clearCWeather();
    }

    /**
     * Place an actor at the given location.
     * @param thing The actor to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(ActingThing thing, int row, int col)
    {
        place(thing, new Location(row, col));
    }

    /**
     * Place an actor at the given location.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(ActingThing thing, Location location)
    {
        field[location.getRow()][location.getCol()].place(thing);
    }

    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getAnimalAt(Location location)
    {
        return getAnimalAt(location.getRow(), location.getCol());
    }

    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Animal getAnimalAt(int row, int col)
    {
        return field[row][col].getAnimal();
    }

    /**
     * Return the plant at the given location, if any.
     * @param location Where in the field.
     * @return The plant at the given location, or null if there is none.
     */
    public Plant getPlantAt(Location location)
    {
        return getPlantAt(location.getRow(), location.getCol());
    }

    /**
     * Return the water at the given location, if any.
     * @param location Where in the field.
     * @return The water at the given location, or null if there is none.
     */
    public WaterTile getWaterAt(Location location)
    {
        return getWaterAt(location.getRow(), location.getCol());
    }

    /**
     * Return the water at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The water at the given location, or null if there is none.
     */
    public WaterTile getWaterAt(int row, int col)
    {
        return field[row][col].getWater();
    }

    /**
     * Return the weather at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The weather at the given location, or null if there is none.
     */
    public WeatherTile getWeatherAt(int row, int col)
    {
        return field[row][col].getWeather();
    }

    /**
     * Return the weather at the given location, if any.
     * @param location Where in the field.
     * @return The weather at the given location, or null if there is none.
     */
    public WeatherTile getWeatherAt(Location location)
    {
        return getWeatherAt(location.getRow(), location.getCol());
    }

    /**
     * Return the centre weather at the given location, if any.
     * @param location Where in the field.
     * @return The centre weather at the given location, or null if there is none.
     */
    public CentralWeather getCWeatherAt(Location location)
    {
        return field[location.getRow()][ location.getCol()].getCWeather();
    }

    /**
     * Return the plant at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The plant at the given location, or null if there is none.
     */
    public Plant getPlantAt(int row, int col)
    {
        return field[row][col].getPlant();
    }

    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }

    /**
     * Get a shuffled list of the free adjacent locations for animal
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getAnimalAt(next) == null&&getWaterAt(next)==null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Get a shuffled list of the free adjacent locations for plants.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentPlantLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getPlantAt(next) == null&&getWaterAt(next)==null) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Get a shuffled list of the free adjacent locations for central weathers(only mid point will move)
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    private List<Location> getFreeAdjacentWeatherLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getCWeatherAt(next)==null ) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location for central weather that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentWeatherLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentWeatherLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Try to find a free location for animal that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Return a shuffled list of outer layer locations of the given distance of the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @param distance how many grids away from the location.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location,int distance)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -distance; roffset <= distance; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -distance; coffset <= distance; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            if(nextRow==row-distance || nextCol == col-distance || nextRow==row+distance|| nextCol == col+distance){
                                locations.add(new Location(nextRow, nextCol));
                            }
                        }
                    }
                }
            }

            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Return a shuffled list of adjacent locations of the given one
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        return adjacentLocations(location,1);
    }

    /**
     *@return true if the location is lie within the field.
     */
    public boolean isValid(Location location)
    {
        return location.getRow()>=0&&location.getRow()<depth&&location.getCol()>=0&&location.getCol()<width;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Find the shortest way from one location to one location.
     * By using the BFS algorithm, search until the search layer is equal to the size given or the destination is found.
     * Each grid that is free with in the size will be given a distance(how many grids away) from the source location.
     * The goal is to find a shortest path to the target.
     * But since the animal can only walk a grid away so only the first step is required.
     * Why not find a move with the shortest distance to the target just like the runAway method in the animal class?
     * This is because that method will not ensure a path to the target as the future movements of that method can be blocked.
     * 
     * Not a efficient algorithm so need to keep the size low.
     * @param slocation the source location
     * @param dlocation the destination
     * @param size the search size, that is how many layers to search.
     * E.g. The destination is two grids away amd the shortest path may require the animal to walk to 10 grids away,
     * if the size is 6, this method will stop searching at 7 grids away and say not possible to move to that location with this searching size.
     * @return The first step of the shortest path to the target, return null if the target is unreachable within the size.
     */
    public Location shortestWayFirstStep(Location slocation,Location dlocation,int size)
    {
        Queue<LocationNode> queue = new LinkedList<>();
        LocationNode [][] nodes= new LocationNode[depth][width];
        queue.add(new LocationNode(slocation.getRow(),slocation.getCol(),0));
        boolean[][] visited = new boolean[depth][width];
        visited[slocation.getRow()][slocation.getCol()]=true;
        Location newdlocation = freeAdjacentLocation(dlocation);//because the target occupied the dlocation
        while(newdlocation==null){
            dlocation =randomAdjacentLocation(dlocation);
            newdlocation = freeAdjacentLocation(dlocation);
        }

        while(!queue.isEmpty()){
            LocationNode currentNode= queue.remove();

            if (currentNode.equals(newdlocation)){
                return recursiveFindNode(nodes,currentNode);
            }
            //To avoid a bad path and searching through the entire field
            if(currentNode.getDistance()>size){
                return null;
            }
            for(Location l:getFreeAdjacentLocations(currentNode)){
                if(!visited[l.getRow()][l.getCol()]){
                    visited[l.getRow()][l.getCol()]=true;
                    LocationNode x = new LocationNode(l.getRow(),l.getCol(),currentNode.getDistance()+1);
                    queue.add(x);
                    nodes[l.getRow()][l.getCol()]=x;
                }
            }
        }
        //no possible path
        return null;
    }

    /**
     * Find the location that has distance of 1 after the destination node is found
     * @param nodes all visited nodes are in the array, the unvisited one will be null;
     * @param node the current node, at the start it will be the destination node
     * @return the location with distance is 1 from the starting node.
     */
    private Location recursiveFindNode(LocationNode[][] nodes,LocationNode node)
    {
        //stop when found
        if(node.getDistance()==1){
            return new Location(node.getRow(),node.getCol());
        }
        else{
            //find a node with one less distance and call the method again
            List<Location> freeLocs= getFreeAdjacentLocations(node);
            for(Location L: freeLocs){
                LocationNode n = nodes[L.getRow()][L.getCol()];
                if(n!=null&&n.getDistance()==(node.getDistance() -1)){
                    return recursiveFindNode(nodes,n );
                }
            }
            //should never reach here as there must be a node with one less distance
            return null;
        }
    }

    /**
     * Fill the field with random number of WaterTile of random shape for the simulation to start
     * @param maximum_size the maximum number of tiles of one water body
     * @param minimum_size the minimum number of tiles of one water body
     */
    public void fillWater(int number_of_waterbody,int maximum_size,int minimum_size)
    {
        for(int i=0;i<number_of_waterbody;i++){
            int size = rand.nextInt(maximum_size-minimum_size+1)+minimum_size;
            Location randomLocation = new Location(rand.nextInt(depth),rand.nextInt(width));
            recursiveSetWaterInGrid(randomLocation, size);
        }

    }

    /**
     * Fill the field with random centre of cloud
     * And then create cloud shape by calling the spread method int CentralWeather class.
     * @param maximum_size the maximum radius of a cloud
     * @param minimum_size the minimum radius of a cloud
     * @return a list of the centre of cloud for the simulotor to call act method.
     */
    public List<CentralWeather> fillWeather(int number_of_cloud,int maximum_size,int minimum_size,DateTime dateTime)
    {
        List<CentralWeather> weathers= new LinkedList<>();
        for(int j=0;j<number_of_cloud;j++){
            int midRainFallValue = rand.nextInt(200)+150;
            int fogValue = rand.nextInt(8)+1;
            Location mid = new Location(rand.nextInt(depth),rand.nextInt(width));

            CentralWeather midWeather = new CentralWeather(this,mid,dateTime,midRainFallValue,fogValue,maximum_size,minimum_size);
            field[mid.getRow()][mid.getCol()].place(midWeather);
            midWeather.spread();
            weathers.add(midWeather);

        }
        return weathers;
    }

    /**
     * @return true if the gird has cloud.
     */
    protected boolean hasWeather(int row,int col)
    {
        return field[row][col].getWeather()!=null;
    }

    /**
     * Perform a random walk and fill the walk path with water tiles
     * Stop when walk to a dead end or the size is meeted.
     * @param location the current location of the walk, if it is null a dead end is reach.
     * @param size the maximum size of the waterbody.
     */
    private void recursiveSetWaterInGrid(Location location,int size)
    {
        if(size == 0||location ==null){//end if meet the size or walk to a dead end
            return;
        }
        else{
            field[location.getRow()][location.getCol()].setWater();
            recursiveSetWaterInGrid(freeAdjacentLocation(location), size-1);
        }
    }

    /**
     * Set a weathertile in a grid.
     */
    public void setWeather(Location location,int rainFallValue,int fogValue)
    {
        field[location.getRow()][location.getCol()].setWeather(rainFallValue,fogValue);
    }

    /**
     * @return true if the location has water
     */
    public boolean hasWater(Location location)
    {
        return field[location.getRow()][location.getCol()].getWater()!=null;
    }

    /**
     * @return distance between two points
     */
    public double getDistance(Location a,Location b)
    {
        return Math.sqrt(Math.pow(a.getCol()- b.getCol(),2)+Math.pow(a.getRow()- b.getRow(),2));
    }

    /**
     * clear all weathertile in the field but not the centre weather
     */
    public void clearAllWeather()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col].clearWeather();
            }
        }
    }
    /**
     * A class for the shortest way algorithm
     * Basically a Location class with field that indicates a distance from a location 
     */
    private class LocationNode extends Location
    {
        //this is how many grids away not the actural distance
        private int distance;

        /**
         * create a node
         */
        public LocationNode(int row,int col,int distance)
        {
            super(row,col);
            this.distance =distance;
        }

        /**
         * @return the distance from a location
         */
        public int getDistance()
        {
            return distance;
        }

    }
}
