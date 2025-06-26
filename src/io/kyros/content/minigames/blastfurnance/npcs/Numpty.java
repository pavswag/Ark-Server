package io.kyros.content.minigames.blastfurnance.npcs;

import io.kyros.model.entity.npc.NPCDumbPathFinder;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Position;
import io.kyros.util.task.TaskManager;

public class Numpty extends BlastFurnaceNpc {

    private static Position first;
    private static Position second;
    public Numpty(int npcId, Position position) {
        super(npcId, position);
        first = position;
        second = new Position(first.getX()-4, first.getY(), first.getHeight());
        NPCSpawning.spawnNpc(npcId, position.getX(), position.getY(), position.getHeight(), 0, 0);
    }

    public void repair() {
        var newPos = getPosition().equals(first) ? second : first;
        NPCDumbPathFinder.walkTowards(this, newPos.getX(), newPos.getY());
        TaskManager.submit(4, () -> {
            startAnimation(2108);
        });
    }


}
