package Entities;

import Environment.Tile;
import Environment.Habitat;


/**
 * The Entity class is the base class which all creatures and flora derive from. Every
 * Entity has an EntityType, as well as the tile that it is in, and its maximum and current
 * body energies. The meaning of body energy varies slightly depending on the type of entity,
 * but in general it is the energy that it cannot itself access. For plants, it may be
 * the energy stored in its fruit, while in animals it may be the animal's flesh.
 *
 * @version 2022.02.08
 */
public abstract class Entity
{
    // The entity type of this entity
    protected EntityType type;
    // The tile that the entity occupies
    protected Tile tile;
    
    // The amount of energy that the entity possesses, in calories
    // When an animal is eaten, some of its nutrients go to the animal that ate it
    // The rest go towards feeding the plants. When a plant is eaten, its energy goes
    // to the animal that ate it.
    protected double bodyEnergy;
    protected double maxBodyEnergy;
    
    /**
     * Constructor for objects of class Entity
     */
    public Entity(Tile tile)
    {
        setTile(tile);
        bodyEnergy = maxBodyEnergy;
    }
    
    /**
     * Destroy this entity - removing it from the habitat
     */
    protected void selfDestruct() {
        tile.removeEntity(this);
    }
    
    /**
     * Make this entity update - that is: make it do
     * whatever it wants/needs to do.
     */
    public abstract void update(int secondsPassed);
    
    /**
     * Reduce the energy of this entity, and return the amount of energy that was reduced.
     * Also kill this entity if it is a creature.
     * @param max The maximum amount of energy to take
     * @return The amount of energy that the creature lost.
     */
    public double beEaten(double max) {
        if(this instanceof Creature) { ((Creature)this).setDead(); }
        double caloriesEaten = Math.min(bodyEnergy,max);
        bodyEnergy -= caloriesEaten;
        return caloriesEaten;
    }
    
    /**
     * @return the tile that this entity is in
     */
    public Tile getTile() {
        return tile;
    }
    
    /**
     * Move from one tile to another.
     * @param tile The next tile to move to.
     */
    protected void setTile(Tile nextTile)
    {
        if(tile != null) tile.removeEntity(this);
        nextTile.addEntity(this);
        tile = nextTile;
    }
    
    /**
     * @return The type of this entity
     */
    public EntityType getType() {
        return type;
    }
    
    /**
     * Get the amount of body energy for this entity. For plants, this may mean the amount
     * of energy in their fruit. For creatures, this may mean the amount of energy in their
     * flesh (that is, the amount that they cannot access themselves).
     * @return The amount of body energy for this entity
     */
    public double getBodyEnergy() {
        return bodyEnergy;
    }
    
    /**
     * @return the maximum body energy for this entity
     */
    public double getMaxBodyEnergy() {
        return maxBodyEnergy;
    }
}
