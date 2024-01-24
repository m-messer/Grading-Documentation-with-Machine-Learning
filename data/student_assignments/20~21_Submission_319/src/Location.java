package src;

import java.util.HashSet;

/**
 * Represent a location in a rectangular grid.
 * Contains the Weather and the Actors at this location
 *
 *  @version 2021.03.03
 */

public class Location
{
    // Row and column positions.
    private final int row;
    private final int col;
    // Number of actors in this location
    private int n=0;
    // Actors in this field
    private final HashSet<Actor> cont;
    // Max number of Actors in a location
    private final static int maxSize = 50;
    // The weather in this location
    private  WeatherTypes weather;

    /**
     * Represent a row and column.
     * @param row The row.
     * @param col The column.
     */
    public Location(int row, int col) {
        this.row = row;
        this.col = col;
        cont = new HashSet<>();
    }

    /**
     * Get the weather of this location
     * @return weather
     */
    public WeatherTypes getWeather() {
        return weather;
    }

    /**
     * Set the weather of this location
     * @param weather The weather type to set
     */
    public void setWeather(WeatherTypes weather) {
        this.weather = weather;
    }

    /**
     * remove a specific Actor from this location
     * @param act - the Actor
     */
    public void remove(Actor act){
        if(cont.contains(act)) {
            cont.remove(act);
            n--;
        }
    }

    /**
     * Check if this location is empty (no Actors)
     * @return boolean - true if empty
     */
    public boolean isEmpty(){
        return n==0;
    }

    /**
     * returns an arrayList of all the actors at this location
     * @return arrayList with all of the actors
     */
    public HashSet<Actor> getActors(){
        return cont;
    }

    /**
     * add an actor to this location
     * @param act the actor
     */

    public void add(Actor act){
        if(n<maxSize) {
            cont.add(act);
            n++;
        }
    }

    /**
     * Check if the maximum numbers of Actors in this location has been reached
     * @return boolean - true if full
     */
    public boolean isFull() {
        return n == maxSize;
    }

    /**
     * Implement content equality.
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Location) {
            Location other = (Location) obj;
            return row == other.getRow() && col == other.getCol();
        }
        else {
            return false;
        }
    }
    
    /**
     * Return a string of the form row,column
     * @return A string representation of the location.
     */
    public String toString()
    {
        return row + "," + col;
    }
    
    /**
     * Use the top 16 bits for the row value and the bottom for
     * the column. Except for very big grids, this should give a
     * unique hash code for each (row, col) pair.
     * @return A hashcode for the location.
     */
    public int hashCode()
    {
        return (row << 16) + col;
    }
    
    /**
     * @return The row.
     */
    public int getRow()
    {
        return row;
    }
    
    /**
     * @return The column.
     */
    public int getCol()
    {
        return col;
    }
}
