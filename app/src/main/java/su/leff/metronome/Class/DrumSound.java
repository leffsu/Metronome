package su.leff.metronome.Class;

/**
 * Created by SASHA on 2/5/2018.
 */

public class DrumSound {

    int id;
    boolean set;
    String name;
    String description;
    int sound;

    public DrumSound(int id, boolean set, String name, int sound, String description) {
        this.id = id;
        this.set = set;
        this.name = name;
        this.description = description;
        this.sound = sound;
    }

    public int getId() {
        return id;
    }

    public boolean getSet() {
        return set;
    }

    public DrumSound(String name, int sound, String description){
        this.name = name;
        this.sound = sound;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public int getSound() {
        return sound;
    }
}
