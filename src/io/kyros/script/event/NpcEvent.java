package io.kyros.script.event;

import io.kyros.model.entity.npc.NPC;

public class NpcEvent extends Event {
    private final NPC npc;

    public NpcEvent(NPC npc) {
        this.npc = npc;
    }

    public NPC getNpc() {
        return npc;
    }
}