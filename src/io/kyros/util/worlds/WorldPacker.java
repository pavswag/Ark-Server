package io.kyros.util.worlds;

import io.kyros.util.Buffer;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class WorldPacker {
    public static void main(String[] args) {
        // Example list of World objects
        List<World> worldList = List.of(
                new World(1, "Live", 1, "162.218.52.190", 52778, "Coding Kyros", 1, (short) 1, "162.218.52.190", 43596),

                new World(2, "Beta", 65536, "213.171.212.95", 52778, "Not active", 1, (short) 0, "213.171.212.95", 43596),

                new World(3, "Development", 33554432, "127.0.0.1", 52778, "Coding Kyros", 1, (short) 1, "127.0.0.1", 43596)
        );

        // Pack the worlds into a file
        try {
            packWorldsIntoFile(worldList, "worlds.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void packWorldsIntoFile(List<World> worlds, String fileName) throws IOException {
        Buffer buffer = new Buffer();
        buffer.writeInt(0);
        buffer.writeUnsignedShort(worlds.size());

        for (int i = 0; i < worlds.size(); i++) {
            World world = worlds.get(i);
            buffer.writeUnsignedShort(world.id);
            buffer.writeStringCp1252NullTerminated(world.name);
            buffer.writeInt(world.properties);
            buffer.writeStringCp1252NullTerminated(world.host);
            buffer.writeInt(world.port);
            buffer.writeStringCp1252NullTerminated(world.activity);
            buffer.writeUnsignedByte(world.location);
            buffer.writeUnsignedShort(world.population);
            buffer.writeStringCp1252NullTerminated(world.js5Host);
            buffer.writeInt(world.js5Port);
        }

        // Write the buffer to a file
        try (FileOutputStream fos = new FileOutputStream(new File(fileName))) {
            fos.write(buffer.toByteArray());
        }
    }
}

@AllArgsConstructor
class World {
    public int id;
    public String name;
    public int properties;
    public String host;
    int port;
    public String activity;
    public int location;
    public short population;
    public String js5Host;
    public int js5Port;
}
