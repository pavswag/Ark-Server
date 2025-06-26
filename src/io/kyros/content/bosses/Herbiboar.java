package io.kyros.content.bosses;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.PlayerHandler;

import java.util.concurrent.TimeUnit;

public class Herbiboar {

    public int herbiboarID = 7785;

    public boolean spawned = false;
    public boolean alive = false;
    public int Health = 5000;
    public long delay = 0;



    public void Tick() {
        if (delay == 0) {
            delay = (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5));
            return;
        }

        if (delay < System.currentTimeMillis()) {
            PlayerHandler.executeGlobalMessage("");
            spawned = true;
            alive = true;
            NPC herbi = NPCSpawning.spawnNpc(herbiboarID, 0,0,0,0,0);
            herbi.getBehaviour().setAggressive(false);
            herbi.getBehaviour().setRespawn(false);

        }


    }


}
