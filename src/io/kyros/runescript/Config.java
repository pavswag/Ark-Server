package io.kyros.runescript;

public class Config {
    private String name;
    private int id;

    public Config(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}

