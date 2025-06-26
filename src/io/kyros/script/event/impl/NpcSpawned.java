package io.kyros.script.event.impl;

import io.kyros.model.entity.npc.NPC;
import io.kyros.script.event.NpcEvent;

public class NpcSpawned extends NpcEvent {
    public NpcSpawned(NPC npc) {
        super(npc);
    }
}