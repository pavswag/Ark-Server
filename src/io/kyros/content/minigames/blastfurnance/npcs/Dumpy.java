package io.kyros.content.minigames.blastfurnance.npcs;

import io.kyros.content.minigames.blastfurnance.BlastFurnace;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Position;
import io.kyros.util.task.TaskManager;

public class Dumpy extends BlastFurnaceNpc {

    private final BlastFurnaceNpc secondNPC;
    private boolean busy;

    public Dumpy(int npcId, Position position) {
        super(npcId, position);
        secondNPC = new BlastFurnaceNpc(7387, getPosition());
        NPCSpawning.spawnNpc(secondNPC.getNpcId(), getPosition().getX(), getPosition().getY(), getPosition().getHeight(), 0, 0);
    }

    public void addCoke() {
        if (busy) {
            return;
        }
        busy = true;
        facePosition(1950, 4964);
        startAnimation(2441);
        startAnimation(2441);
        TaskManager.submit(2, () -> {
            teleport(getPosition().getX(), getPosition().getY(), 4);
            if (!getPosition().equals(getPosition())) {
                teleport(getPosition());
            }
            facePosition(1950, 4964);
        });

        TaskManager.submit(4, () -> {
            facePosition(1949, 4963);
            startAnimation(2442);
        });

        TaskManager.submit(6, () -> {
            teleport(getPosition());
            facePosition(1950, 4964);
            teleport(getPosition().getX(), getPosition().getY(), 4);
            startAnimation(2443);
            BlastFurnace.getStove().addCoal(75);
        });

        TaskManager.submit(10, () -> busy = false);
    }
}
