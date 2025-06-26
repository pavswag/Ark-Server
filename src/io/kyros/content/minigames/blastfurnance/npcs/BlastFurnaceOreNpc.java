package io.kyros.content.minigames.blastfurnance.npcs;

import io.kyros.Server;
import io.kyros.content.minigames.blastfurnance.BlastFurnace;
import io.kyros.content.minigames.blastfurnance.BlastFurnaceOre;
import io.kyros.content.minigames.blastfurnance.dispenser.BarDispenser;
import io.kyros.model.Npcs;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;
import lombok.Getter;

public class BlastFurnaceOreNpc extends NPC {

    private static final Position ORE_STARTING_POSITION = new Position(1942, 4966, 0);
    private static final Position ORE_END_POSITION = new Position(1942, 4963, 0);

    private Task task;
    @Getter
    protected BlastFurnaceOre ore;
    public BlastFurnaceOreNpc(int npcId, Position position) {
        super(npcId, position);
    }

    public void start() {
        if (task != null) {
            return;
        }

        TaskManager.submit(1, () -> TaskManager.submit(task = new Task(1, this, false) {
            @Override
            protected void execute() {
                if (asNPC() == null) {
                    start();
                    return;
                }
                moveTowards(ORE_END_POSITION.getX(), ORE_END_POSITION.getY());
                if (getPosition().equals(ORE_END_POSITION)) {
                    onArrival();
                    BlastFurnaceOreNpc.this.stop();
                }
            }
        }));
    }

    public void stop() {
        if (task != null) {
            task.stop();
            task = null;
        }
    }

    public void onArrival() {
        startAnimation(2434);
        TaskManager.submit(1, () -> {
            Server.getPlayers().forEach(plr -> {
                if (this.asNPC().spawnedBy == plr.getIndex()) {
                    plr.getBlastFurnace().sendToBelt(ore);
                    BlastFurnace.removeNpc(plr, ore);
                    BarDispenser.melt(plr);
                }
            });
        });
    }

    protected void setup(Player player, BlastFurnaceOre ore) {
        spawnedBy = player.getIndex();
        this.ore = ore;
    }

    public static BlastFurnaceOreNpc create(BlastFurnaceOre ore, Player player) {
        var npc = new BlastFurnaceOreNpc(getNpcId(ore), ORE_STARTING_POSITION);
        npc.setup(player, ore);
        NPCSpawning.spawnNpc(player, npc.getNpcId(), ORE_STARTING_POSITION.getX(), ORE_STARTING_POSITION.getY(), 0, 0,0,false,false);
        return npc;
    }

    public static int getNpcId(BlastFurnaceOre ore) {
        switch (ore) {
            case TIN:
                return Npcs.TIN_ORE;
            case COPPER:
                return Npcs.COPPER_ORE;
            case IRON:
                return Npcs.IRON_ORE;
            case SILVER:
                return Npcs.SILVER_ORE;
            case COAL:
                return Npcs.COAL;
            case GOLD:
                return Npcs.GOLD_ORE;
            case MITHRIL:
                return Npcs.MITHRIL_ORE;
            case ADAMANTITE:
                return Npcs.ADAMANTITE_ORE;
            case RUNITE:
                return Npcs.RUNITE_ORE;
        }
        return -1;
    }
}
