package io.kyros.model.entity.npc;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import io.kyros.Server;
import io.kyros.model.entity.npc.data.NpcMaxHit;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

public class NpcSpawnLoaderOSRS {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(NpcSpawnLoaderOSRS.class.getName());
    private static final String DIRECTORY = Server.getDataDirectory() + "/cfg/npc/osrsspawns/";
    public static void initOsrsSpawns() {
        File[] files = Objects.requireNonNull(new File(DIRECTORY).listFiles());
        int loaded = 0;
        for (File file : files) {
            loaded += loadOsrsSpawns(file);
        }
        log.info("Spawned " + loaded + " OSRS npcs.");
    }

    public static int loadOsrsSpawns(File file) {
        try (FileReader fr = new FileReader(new File(file.getPath()))) {
            List<Spawn> list = new Gson().fromJson(fr, new TypeToken<List<Spawn>>() {}.getType());
            list.forEach(spawn -> {
                if (spawn.walkRange > 0) {
                    if (spawn.direction.equalsIgnoreCase("N")) {
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.WALK.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                    } else if (spawn.direction.equalsIgnoreCase("E")) {
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.WALK.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                    } else if (spawn.direction.equalsIgnoreCase("S")) {
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.WALK.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                    } else if (spawn.direction.equalsIgnoreCase("W")) {
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.WALK.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                    }
                } else {
                    if (spawn.direction.equalsIgnoreCase("N")) {
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.FACE_NORTH.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                    } else if (spawn.direction.equalsIgnoreCase("E")) {
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.FACE_EAST.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                    } else if (spawn.direction.equalsIgnoreCase("S")) {
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.FACE_SOUTH.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                    } else if (spawn.direction.equalsIgnoreCase("W")) {
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.FACE_WEST.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                    }
                }
            });
            return list.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static final class Spawn {
        @Expose
        public String name;
        @Expose public int id;
        @Expose public int x, y, z;
        @Expose public String direction = "S";
        @Expose public int walkRange;
        @Expose public String world;
    }
}
