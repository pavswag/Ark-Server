package io.kyros.script.event.impl;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.script.event.NpcEvent;

public class NpcOptionThree extends NpcEvent {
    public NpcOptionThree(NPC npc, Player player) {
        super(npc);
        this.player = player;
    }

    private final Player player;
    public int getNpcId() {
        return getNpc().getNpcId();
    }
    public Player getPlayer() {
        return player;
    }
    public int getOptionIndex() {
        return 3;
    }
}