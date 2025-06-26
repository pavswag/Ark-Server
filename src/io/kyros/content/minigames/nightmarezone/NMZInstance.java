package io.kyros.content.minigames.nightmarezone;

import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NMZInstance extends InstancedArea {

    private static final int INITIAL_WAVE_DELAY = 10000; // 10 seconds in milliseconds
    private static final int WAVE_INCREASE_DIFFICULTY = 1; // Difficulty increment per wave (can be adjusted)
    private int currentWave = 1;

    public NMZInstance() {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, NMZBossing.NMZONE);
    }

    public static void enterInstance(Player player) {
        NMZInstance nmzInstance = new NMZInstance();
        int height = nmzInstance.getHeight() + 4;

        // Move the player into the instance
        player.getPA().movePlayer(2272, 4685, height);
        nmzInstance.add(player);

        // Start the first wave after the initial delay
        nmzInstance.startWave(player, INITIAL_WAVE_DELAY);
    }

    // Starts a wave after a given delay
    public void startWave(Player player, int delay) {
        // Ensure no NPCs are alive in the instance
        if (!areAllNpcsDead(player)) {
            return; // Do nothing until all NPCs from previous wave are dead
        }

        // Timer to start the wave after a delay
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                spawnNPCsForWave(player);
                increaseDifficulty();
            }
        }, delay);
    }

    // Spawn NPCs based on the player's NMZBosses list
    private void spawnNPCsForWave(Player player) {
        ArrayList<Integer> bosses = player.NMZBosses;

        // Spawn NPCs from the player's NMZBosses list
        for (int bossId : bosses) {
            int npcX = 2272; // NPC spawn coordinates (can be dynamic)
            int npcY = 4685;
            int height = getHeight();

            // Use your server's NPC spawning logic here
            spawnNPC(bossId, npcX, npcY, height, player);
        }

        player.sendMessage("Wave " + currentWave + " has started!");
    }

    // Handle NPC spawning (server-specific logic required)
    private void spawnNPC(int npcId, int x, int y, int height, Player player) {
        // Implement your server-specific logic for spawning NPCs
        // This can include the logic for making bosses harder (increased HP, accuracy, etc.)
        NPC npc = NPCSpawning.spawnNpc(npcId, x, y, height,1,50);
        // Apply increased difficulty here
        npc.getHealth().setMaximumHealth(npc.getHealth().getCurrentHealth() + (WAVE_INCREASE_DIFFICULTY * currentWave));
        npc.getHealth().setCurrentHealth(npc.getHealth().getCurrentHealth() + (WAVE_INCREASE_DIFFICULTY * currentWave));

        this.add(npc);
    }

    // Increase the difficulty of the bosses for the next wave
    private void increaseDifficulty() {
        currentWave++;
    }

    // Check if all NPCs in the instance are dead
    private boolean areAllNpcsDead(Player player) {
        // Implement logic to check if there are any active NPCs in the area
        return this.getNpcs().isEmpty(); // Assuming getNpcsInInstance returns NPCs in the instance
    }

    // Handle NPC death
    public static void handleNPCDeath(Player player, NPC npc) {
        // Check if the instance is clear and start the next wave
        NMZInstance instance = (NMZInstance) player.getInstance(); // Retrieve the player's NMZ instance
        if (instance != null && instance.areAllNpcsDead(player)) {
            // Start the next wave after a delay
            instance.startWave(player, INITIAL_WAVE_DELAY);
        }
    }

    @Override
    public void onDispose() {
        // Cleanup logic when the instance is closed or emptied
        // Example: remove all NPCs, reset player status, etc.
    }
}
