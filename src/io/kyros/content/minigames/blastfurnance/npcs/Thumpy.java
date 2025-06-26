package io.kyros.content.minigames.blastfurnance.npcs;

import io.kyros.model.entity.npc.NPCDumbPathFinder;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Position;
import io.kyros.util.task.TaskManager;

public class Thumpy extends BlastFurnaceNpc {
    public Thumpy(int npcId, Position position) {
        super(npcId, position);
        NPCSpawning.spawnNpc(npcId, position.getX(), position.getY(), position.getHeight(), 0, 0);
    }

    public void repair() {
        NPCDumbPathFinder.walkTowards(asNPC(), getX()+1, getY());
        TaskManager.submit(2, () -> startAnimation(898));
        TaskManager.submit(6, () -> NPCDumbPathFinder.walkTowards(asNPC(), getX() -1, getY()));
    }
}
