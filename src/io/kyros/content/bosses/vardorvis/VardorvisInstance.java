package io.kyros.content.bosses.vardorvis;

import io.kyros.Server;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

public class VardorvisInstance extends LegacySoloPlayerInstance {

    // Define Vardorvis' zone boundary
    public static final Boundary VARDORVIS_ZONE = Boundary.VARDORVIS;

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public VardorvisInstance(Player player) {
        super(CONFIGURATION, player, VARDORVIS_ZONE);
    }

    public void enter(Player player) {

        if (InstancedArea.isPlayerInSameInstanceType(player, VardorvisInstance.class)) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a Vardorvis instance.");
            return; // Prevent entry
        }

        player.getPA().closeAllWindows();
        player.moveTo(new Position(1125, 3418, getHeight()));
        player.getAttributes().setBoolean("vardorvis-perfect-kill", true);
        add(player);

        NPC vardorvis = new Vardorvis(12223, new Position(1129, 3418, getHeight()));
        add(vardorvis);
    }

    @Override
    public void onDispose() {
        System.out.println("Disposed of the Vardorvis instance");
    }
}
