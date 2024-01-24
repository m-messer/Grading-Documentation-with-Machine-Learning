package src;

import java.util.Collections;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store multiple actors.
 *
 * @version 2021.03.03
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    // The depth and width of the field.
    private final int depth;
    private final int width;
    // Storage for the actors.
    private final Location[][] field;
    // The time of day (from 00:00 to 23:59)
    private int timeOfDay;
    // The amount of time that passes with each step
    private final int TIME_INCREMENT_PER_STEP_IN_HOURS = 6;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Location[depth][width];
        timeOfDay=0;
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = new Location(row,col);
            }
        }
    }

    /**
     * @param row The row of the location.
     * @param col The column of the location.
     * @return True if the specified location contains no actor.
     */
    public boolean isEmpty(int row,int col){
        return field[row][col].isEmpty();
    }

    /**
     * Removes an actor from a location.
     * @param location The location to remove the actor from.
     * @param actor The actor to be removed.
     */
    public void remove(Location location, Actor actor){
        location.remove(actor);
    }

    /**
     * Return the location at a specified row and column
     * @param row the row
     * @param col the column
     * @return location at specified row and column
     */
    public Location getLocation(int row,int col){
        return field[row][col];
    }

    /**
     * Place an actor at the given location.
     * @param actor The actor to be placed.
     * @param location Where to place the actor.
     */
    public void place(Actor actor, Location location)
    {
        location.add(actor);
    }

    /**
     * Return the actors at the given location, if any.
     * @param location Where in the field.
     * @return The actor at the given location, or an empty arrayList if there is none.
     */
    public HashSet<Actor> getActorsAt(Location location)
    {
        return location.getActors();
    }

    /**
     * Return the actors at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The actors at the given location, or null if there is none.
     */
    public HashSet<Actor> getActorsAt(int row, int col)
    {
        return field[row][col].getActors();
    }

    /**
     * Checks if a location is free
     * @param location the location
     * @return boolean - true if free
     */
    private boolean isFree(Location location){
        return !location.isFull();
    }

    /**
     * Advance the time of day by a set amount of hours.
     */
    public void advanceTimeOfDay(){
        timeOfDay+=TIME_INCREMENT_PER_STEP_IN_HOURS;
        timeOfDay%=24;
    }

    /**
     * Randomly generate the weather across the field.
     * This is done in squares of size 10 by 10.
     */
    public void generateWeather(){
        double randomNumber;
        WeatherTypes result=null;
        for(int i=0;i<depth;i+=10)
            for(int j=0;j<width;j+=10) {
                randomNumber = rand.nextDouble();
                for (WeatherTypes type : WeatherTypes.values())
                    if(randomNumber<=type.getProbability())
                        result = type;
                setWeather(i, j, result);
            }
    }

    /**
     * Attributes a specified type of weather to a 10 by 10 square
     * (with the left-upper corner in the specified position)
     * @param row The row
     * @param col The col
     * @param type The type of weather
     */
    private void setWeather(int row, int col, WeatherTypes type){
        for(int i=row;i<row+10 && i<depth;i++)
            for(int j=col;j<col+10 && j<width;j++)
                getLocation(i, j).setWeather(type);
    }

    /**
     * @return The time of day in hours (from 0 to 23)
     */
    public int getTimeOfDay(){
        return timeOfDay;
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(isFree(next)) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Try to find a free location that is adjacent to the
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
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        int row = location.getRow();
        int col = location.getCol();
        for(int roffset = -1; roffset <= 1; roffset++) {
            int nextRow = row + roffset;
            if(nextRow >= 0 && nextRow < depth) {
                for(int coffset = -1; coffset <= 1; coffset++) {
                    int nextCol = col + coffset;
                    // Exclude invalid locations and the original location.
                    if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                        locations.add(field[nextRow][nextCol]);
                    }
                }
            }
        }

        // Shuffle the list. Several other methods rely on the list
        // being in a random order.
        Collections.shuffle(locations, rand);
        return locations;
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
}
