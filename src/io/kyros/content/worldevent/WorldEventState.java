package io.kyros.content.worldevent;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.kyros.Server;
import io.kyros.util.JsonUtil;
import org.apache.commons.io.FileUtils;

public class WorldEventState {

    private static String getSaveFile() {
        return Server.getSaveDirectory() + "world_event_state.json";
    }

    public static WorldEventState load() throws IOException {
        File file = new File(getSaveFile());
        if (file.exists()) {
            try {
                return new Gson().fromJson(FileUtils.readFileToString(file), new TypeToken<WorldEventState>() {}.getType());
            } catch (JsonSyntaxException e) {
                // Log the error and potentially handle the corrupted file
                System.out.println("Failed to load world event state!");
                // Optionally, return a new instance or handle it accordingly
            }
        }

        return new WorldEventState(-1, WorldEventContainer.getInstance().getCyclesBetweenEvents());
    }

    private int worldEventIndex;
    private int ticksUntilNextEvent;

    public WorldEventState(int worldEventIndex, int ticksUntilNextEvent) {
        this.worldEventIndex = worldEventIndex;
        this.ticksUntilNextEvent = ticksUntilNextEvent;
    }

    private void serialize() {
        Server.getIoExecutorService().submit(() -> {
            JsonUtil.toJson(this, getSaveFile());
        });
    }

    public int getWorldEventIndex() {
        return worldEventIndex;
    }

    public void setWorldEventIndex(int worldEventIndex) {
        this.worldEventIndex = worldEventIndex;
        serialize();
    }

    public int getTicksUntilNextEvent() {
        return ticksUntilNextEvent;
    }

    public void setTicksUntilNextEvent(int ticksUntilNextEvent) {
        this.ticksUntilNextEvent = ticksUntilNextEvent;
        serialize();
    }
}
