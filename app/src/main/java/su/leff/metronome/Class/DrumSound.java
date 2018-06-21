package su.leff.metronome.Class;

/**
 * Created by SASHA on 2/5/2018.
 */

public class DrumSound {

    String name;
    String description;
    int sound;

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

    public int getSound() {
        return sound;
    }
}
