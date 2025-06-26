package io.kyros.content.bosses.whisperer;

import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class WhispererInstance extends LegacySoloPlayerInstance {

    public static final Boundary WHISPERER_ZONE = new Boundary(2624, 6336, 2687, 6399); // Define The Whisperer's zone boundary

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(false)
            .createInstanceConfiguration();

    public WhispererInstance(Player player) {
        super(CONFIGURATION, player, WHISPERER_ZONE);
    }

    @Getter
    private List<NPC> activeSouls = new ArrayList<>();

    @Setter
    @Getter
    private TheWhisperer whisperer;

    public void enter(Player player) {
        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        player.getPA().closeAllWindows();
        player.moveTo(new Position(2657, 6382, getHeight())); // Move player to the instance starting position
        add(player);

        player.setSanity(100);

        NPC whisperer = new TheWhisperer(new Position(2655, 6368, getHeight())); // Spawn The Whisperer at the desired position
        this.whisperer = (TheWhisperer) whisperer;
        add(whisperer);
    }

    // Add a soul to the active list
    public void addSoul(NPC soul) {
        activeSouls.add(soul);
    }

    // Remove a soul from the active list (when it dies)
    public void removeSoul(NPC soul) {
        activeSouls.remove(soul);
    }

    // Check if all souls are dead
    public boolean allSoulsDead() {
        return activeSouls.stream().allMatch(NPC::isDead);
    }

    // Handle soul death and trigger actions
    public void handleSoulDeath(NPC soul, Player killer) {
        for (NPC activeSoul : activeSouls) {
            if (activeSoul == soul && !activeSoul.isDead()) {
                activeSoul.setDead(true); // Mark the soul as dead

                // Apply the effect for this specific soul's death
                whisperer.applySoulEffects(activeSoul, killer);

                break; // Exit loop once we've handled the dead soul
            }
        }

        // After handling the death of this soul, check if all souls are now dead
        if (allSoulsDead() && whisperer != null) {
            whisperer.handleAllSoulsKilled(killer); // Trigger special effects when all souls are killed
        }
    }

    @Override
    public void onDispose() {
        System.out.println("Disposed of the Whisperer instance");
    }
}
