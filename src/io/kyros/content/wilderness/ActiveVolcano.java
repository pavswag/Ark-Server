package io.kyros.content.wilderness;

import io.kyros.Server;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class ActiveVolcano {

    private static boolean DISABLED = true;


    private static final int BOULDER = 31037;
    public static int BOULDER_STABILITY = 500;

    private static ActiveVolcano ACTIVE;
    private static long timeRemaining = 0;
    private static GlobalObject boulder;

    private static final ActiveVolcano[] SPAWNS = {
            new ActiveVolcano(new Position(3366, 3936, 0)),
            new ActiveVolcano(new Position(3353, 3934, 0)),
            new ActiveVolcano(new Position(3374, 3937, 0)),
            new ActiveVolcano(new Position(3361, 3924, 0)),
    };

    public static boolean progress = false;
    private final Position boulderSpawn;

    public ActiveVolcano(Position boulderSpawn) {
        this.boulderSpawn = boulderSpawn;
    }

    public static long delay = 0;

    public static void Tick() {
        if (delay == 0) {
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);
        }
        if (timeRemaining > 0 && timeRemaining < System.currentTimeMillis() && progress) {
            removeBoulder(false);
            progress = false;
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
        }

        if (progress) {
            return;
        }

        if (delay > System.currentTimeMillis()) {
            return;
        }

        ActiveVolcano next = Misc.get(SPAWNS);
        if (next == ACTIVE) {
            return;
        }

        ACTIVE = next;
        progress = true;
        Discord.writeBugMessage("[WILDY] There's been a disturbance reported at the Volcano! ::volcano <@&1121030169767985172>");
        new Broadcast("[WILDY] There's been a disturbance reported at the Volcano! ::volcano").submit();
        addBoulder();
        timeRemaining = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15);
    }

/*    public static void fallingLava(Player player, Position position) {
            for (Player p : Server.getPlayers().stream().filter(pz -> player.getPosition().withinDistance(pz.getPosition(), 1)).collect(Collectors.toList())) {
                if (p.getPosition().equals(position)) {
                    p.sendMessage("@red@You hear something falling...");
                    p.startGraphic(new Graphic(1406,1,0));
                    if (p.getPosition().equals(position)) {
                        p.appendDamage(Misc.random(10,30), Hitmark.HIT);
                        p.sendMessage("@red@A piece of flying lava hits you.");
                    } else {
                        p.sendMessage("@red@You dodge the flying lava.. close one.");
                    }
                }
            }
    }*/

    private static void addBoulder() {
        GlobalObject go = new GlobalObject(BOULDER, ACTIVE.boulderSpawn.getX(), ACTIVE.boulderSpawn.getY(), 0, 0, 10);
        Server.getGlobalObjects().add(go);
        boulder = go;
        BOULDER_STABILITY = 500;
    }

    public static void removeBoulder(boolean success) {
        if (boulder != null) {
            Server.getGlobalObjects().remove(boulder);
            Server.getGlobalObjects().add(new GlobalObject(-1, ACTIVE.boulderSpawn.getX(), ACTIVE.boulderSpawn.getY(), 0, 0, 10));
            boulder.setId(-1);
            boulder = null;
            progress = false;
            ACTIVE = null;
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
            if (success) {
                new Broadcast("@gre@The volcano has been subdued! Well done everyone!").submit();
            } else {
                new Broadcast("@red@The Volcano has erupted! Help subdue it next time for blood money!").submit();
            }
        }
    }

    public static void removeShards(int amt) {
        BOULDER_STABILITY -= amt;
        if (BOULDER_STABILITY <= 0)
            BOULDER_STABILITY = 0;
    }

}
