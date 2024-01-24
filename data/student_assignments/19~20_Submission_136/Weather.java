/**
 * @version 2020.02.23
 */

public class Weather {
    private boolean isSunny;
    private boolean isRaining;
    private boolean isWindy;

    public Weather() {
        this.isSunny = false;
        this.isRaining = false;
        this.isWindy = false;
    }

    public void setSunny(boolean sunny) {
        isSunny = sunny;
    }

    public void setRaining(boolean raining) {
        isRaining = raining;
    }

    public void setWindy(boolean windy) {
        isWindy = windy;
    }

    public boolean isSunny() {
        return isSunny;
    }

    public boolean isRaining() {
        return isRaining;
    }

    public boolean isWindy() {
        return isWindy;
    }
}
