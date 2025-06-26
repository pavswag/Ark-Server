package io.kyros.runescript;

public class AnimationConfig {
    private final int id;
    private final int time;

    public AnimationConfig(int id, int time) {
        this.id = id;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }
}
