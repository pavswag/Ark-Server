package io.kyros.content.staff_skilling_bots;

import io.kyros.annotate.PostInit;
import io.kyros.model.entity.EntityProperties;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillingBots {
    @Getter
    private static List<StaffBot> staffBots = new ArrayList<>();
    private static Map<String, EntityProperties> staffMap = new HashMap<>();

    static {
        staffMap.put("Prophet", EntityProperties.PROPHET);
        staffMap.put("Luke", EntityProperties.LUKE);
        staffMap.put("Sponge", EntityProperties.SPONGE);
        staffMap.put("The Guru", EntityProperties.THE_GURU);
        staffMap.put("BBQ", EntityProperties.BBQ);
        staffMap.put("Ceredoris", EntityProperties.CEREDORIS);
        staffMap.put("Thrill", EntityProperties.THRILL);
        staffMap.put("13th Reason", EntityProperties.THIRTEENTH_REASON);
        staffMap.put("Adrian", EntityProperties.ADRIAN);
        staffMap.put("Burnsy", EntityProperties.BURNSY);
        staffMap.put("Alpha", EntityProperties.ALPHA);
        staffMap.put("Aparigraha", EntityProperties.APARIGRAHA);
        staffMap.put("Heimdall", EntityProperties.HEIMDALL);
        staffMap.put("Novachrono", EntityProperties.NOVACHRONO);
    }

    //@PostInit
    public static void onStartUp() {
        staffMap.forEach((name, property) -> {
            StaffBot bot = new StaffBot(1358, new Position(3135, 3628));
            bot.staffName = name;
            bot.addEntityProperty(property);
            bot.getBehaviour().setAggressive(false);
            bot.getCombatDefinition().setAggressive(false);
            staffBots.add(bot);
            NPCSpawning.spawn(bot);
        });
    }

    public static boolean onClick(Player player, NPC npc, int option) {
        staffBots.forEach(staffBot -> {
            if(staffBot.getIndex() == npc.getIndex()) {
                switch (option) {
                    /**
                     * Follow
                     */
                    case 1 -> {
                        player.npcFollowingIndex = npc.getIndex();
                        player.getPA().followNpc();
                    }
                    /**
                     * Trade With
                     */
                    case 2 -> {
                        npc.facePlayer(player.getIndex());
                        npc.forceChat("I have nothing to give you " + player.getDisplayName() + "!");
                    }
                }
            }
        });
        return false;
    }
}
