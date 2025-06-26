package io.kyros.content.minigames;

import io.kyros.model.Attributes;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

import java.util.List;

public abstract class Raid {
    public abstract boolean handleObject(Player player, int object);
    public abstract boolean onNpcKilled(NPC npc);
    public abstract List<Player> getPlayers();
    public abstract void onPlayerDeath(Player player);


    protected final Attributes raidAttributes = new Attributes();
}
