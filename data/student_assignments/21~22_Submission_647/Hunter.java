import java.util.Random;
import java.util.List;
/**
 *  A model of human hunter.
 */
public class Hunter implements Actor
{
    private Field field;
    private Location location;
    private static final Random rand = Randomizer.getRandom();
    private boolean alive;
    /**
     *
     */
    public Hunter(Field field, Location location)
    {
        this.field = field;
        this.location = location;
        alive = true;
        setLocation(location);
    }

    private void shoot(Location location)
    {
        if (location == null){
            return;
        }

        Object obj = field.getObjectAt(location);

        if(obj instanceof Animal && !(obj instanceof Grass)){
            Animal animal = (Animal)obj;
            animal.setDead();
        }
    }

    public boolean isAlive()
    {
        return alive;
    }

    public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    private void randomMove()
    {
        Location newLocation;
        do {
            newLocation = new Location(rand.nextInt(field.getDepth()), rand.nextInt(field.getWidth()));
        } while (field.getObjectAt(newLocation) instanceof Hunter);
        Object obj = field.getObjectAt(newLocation);
        if (obj instanceof Animal) {
            Animal animal = (Animal)obj;
            animal.setDead();
        }
        setLocation(newLocation);
    }

    private void setLocation(Location newLocation)
    {
        if (newLocation == null){
            return;
        } else {
            field.clear(location);
            this.location = newLocation;
            field.place(this, newLocation);
        }
    }

    private void shootAnimals()
    {
        List<Location> locations = field.adjacentLocations(location);
        for (int index = 0; index < locations.size(); index++){
            shoot(locations.get(index));
        }
    }

    /**
     */
    public void actDay(List<Actor> newActors)
    {
        if (isAlive()){
            if(field.isSunny()){
                randomMove();
                shootAnimals();
            }
        } else {
            setDead();
        }
    }

    public void actNight(List<Actor> newActors)
    {

    }
}