import java.util.List;
/**
 */

public interface Actor
{
    void actDay(List<Actor> actors);

    void actNight(List<Actor> actors);

    boolean isAlive();

    void setDead();
}
