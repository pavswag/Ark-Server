package io.kyros.content.bosses;

import io.kyros.Server;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.util.Location3D;
import io.kyros.util.discord.Discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JusticarZachariah extends NPC {

    public static List<Player> targets = new ArrayList<>();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    private static NPC justicar;

    public JusticarZachariah(int npcId, Position position) {
        super(npcId, position);
    }

    private static int justicar_counter = 0;

    public static void addMantiCounter() {
        justicar_counter += 1;

        if (justicar_counter >= 5) {
            spawnBoss();
            justicar_counter = 0;
        }
    }

    public static void spawnBoss() {
        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
        justicar = NPCSpawning.spawnNpcOld(12449, 3736, 3975, 0, 0, 65000, 0, 0, 0);
        justicar.getBehaviour().setRespawn(false);
        justicar.getBehaviour().setAggressive(true);

        justicar.getBehaviour().setRunnable(true);
        justicar.getHealth().setMaximumHealth(65000);
        justicar.getHealth().reset();

        announce();
        Discord.writeBugMessage("[Justicar Zachariah] has spawned!, use ::vb or ::db <@&1121030200713551893>");
    }

    private static void announce() {
        new Broadcast("[Justicar Zachariah] has spawned!, use ::vb or ::db").addTeleport(new Position(3738,3967, 0)).copyMessageToChatbox().submit();
    }

    public static void handleRewards() {
        HashMap<String, Integer> map = new HashMap<>();
        damageCount.forEach((p, i) -> {
            if (map.containsKey(p.getUUID())) {
                map.put(p.getUUID(), map.get(p.getUUID()) + 1);
            } else {
                map.put(p.getUUID(), 1);
            }
        });

        for (String s : map.keySet()) {
            if (map.containsKey(s) && map.get(s) > 1) {
                for (Player player : Server.getPlayers().toPlayerArray()) {
                    if (player.getUUID().equalsIgnoreCase(s)) {
                        Discord.writeServerSyncMessage("[Justicar Zachariah] "+player.getDisplayName() + " has tried to take more than 2 account's there!");
                    }
                }
            }
        }

        map.values().removeIf(integer -> integer > 1);

        damageCount.forEach((player, integer) -> {
            if (integer > 1) {

                int amountOfDrops = 1;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                Pass.addExperience(player, 5);
                Server.getDropManager().create(player, justicar, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 12449);

                PetHandler.rollOnNpcDeath(player, justicar);
            }
        });
        PlayerHandler.executeGlobalMessage("@red@[Justicar Zachariah]@blu@ has been defeated!");



    }
}
