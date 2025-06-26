package io.kyros.content.npchandling;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.model.entity.npc.NPC;
import io.kyros.util.Misc;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author ArkCane, Adam
 * @social Discord: ArkCane, thegururspsdev
 * @website: www.arkcane.net
 * @since 07/02/2024
 */
public class ForcedChat {
    private static final Int2ObjectOpenHashMap<String> forcedChats = new Int2ObjectOpenHashMap<>();

    @SneakyThrows
    @PostInit
    public static void loadChats() {
        if (!forcedChats.isEmpty()) {
            forcedChats.clear();
        }

        
        Configuration.TOWN_CRIER = new String(Files.readAllBytes(Paths.get(Server.getDataDirectory() + "/cfg/town_crier.txt")));

        forcedChats.put(5792, "flash1:Premium Donator Zone");
        forcedChats.put(2112, "flash3:Normal Donator Zone");
        forcedChats.put(11675, "flash2:Register Your Number For Free Rewards!");
        forcedChats.put(2309, "glow3:Register Your Discord For Free Rewards!");
        forcedChats.put(7041, "I'll exchange your vote crystals & pkp points!");
        forcedChats.put(6823, Configuration.TOWN_CRIER);
    }

    public static void tryForceChat(NPC npc) {
        String chat = forcedChats.getOrDefault(npc.getNpcId(), null);
        if(chat != null) {
            npc.lastForcedChat--;
            if(npc.lastForcedChat <= 0 && Misc.random(1, 15) < 3) {
                npc.forceChat(chat);
                npc.lastForcedChat = 10;
            }
        }
    }

    public static void saveTownCrier(String content) {
        try {
            Files.write(Paths.get(Server.getDataDirectory() + "/cfg/town_crier.txt"), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Configuration.TOWN_CRIER = content;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
