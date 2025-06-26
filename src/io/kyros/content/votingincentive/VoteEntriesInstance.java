package io.kyros.content.votingincentive;

import io.kyros.content.activityboss.impl.Groot;
import io.kyros.content.bosses.Durial321;
import io.kyros.content.commands.admin.dboss;
import io.kyros.content.commands.helper.vboss;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.hotdrops.HotDrops;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;
import io.kyros.util.discord.impl.VoteBoss;

public class VoteEntriesInstance extends LegacySoloPlayerInstance {

    public VoteEntriesInstance(Player player, Boundary... boundaries) {
        super(player, boundaries);
    }

    public void handleEntry(Player player, VoteEntriesInstance voteEntriesInstance, VoteEntryBosses voteEntryBoss) {
        int entries = player.VoteEntries;

        if (voteEntryBoss.getCost() > entries) {
            player.sendErrorMessage("Looks like you don't have enough entry points to do this!");
            return;
        }

        player.VoteEntries -= voteEntryBoss.getCost();
        int npcId = voteEntryBoss.getNpcId();
        NPC npc = NPCSpawning.spawnNpc(player, npcId, 1183, 9827, voteEntriesInstance.getHeight(), 1, 32, true, false);
        npc.getBehaviour().setRespawn(true);
        npc.getCombatDefinition().setAggressive(true);
        npc.getBehaviour().setAggressive(true);

        voteEntriesInstance.add(npc);
        player.moveTo(new Position(1184, 9816, voteEntriesInstance.getHeight()));
        voteEntriesInstance.add(player);
    }

    public static int HotDropActivation = 0;
    public static int DonationBossActivation = 0;
    public static int VoteBossActivation = 0;
    public static int DurialBossActivation = 0;
    public static int GrootBossActivation = 0;

    public static void handleGlobalSpawn(Player player) {
        player.start(new DialogueBuilder(player)
                .option("Which global would you like to contribute to?",
                        new DialogueOption("HotDrop", p -> handleGlobalOption(p, HotDrops.npc != null, "HotDrop",
                                amount -> HotDropActivation += amount,
                                () -> HotDrops.handleHotDrop(VoteEntriesRandomBosses.values()[Misc.random(VoteEntriesRandomBosses.values().length-1)].getNpcId(), true))),
                        new DialogueOption("Donation Boss", p -> handleGlobalOption(p, false, "Donation Boss",
                                amount -> DonationBossActivation += amount,
                                dboss::spawnBoss)),
                        new DialogueOption("Vote Boss", p -> handleGlobalOption(p, false, "Vote Boss",
                                amount -> VoteBossActivation += amount,
                                vboss::spawnBoss)),
                        new DialogueOption("Durial", p -> handleGlobalOption(p, Durial321.spawned || Durial321.alive, "Durial",
                                amount -> DurialBossActivation += amount,
                                Durial321::init)),
                        new DialogueOption("Groot", p -> handleGlobalOption(p, Groot.alive || Groot.spawned, "Groot",
                                amount -> GrootBossActivation += amount,
                                Groot::spawnGroot))));

        checkAndActivateBosses();
    }

    private static void handleGlobalOption(Player player, boolean alreadyActive, String bossName,
                                           java.util.function.IntConsumer addActivation, Runnable spawnBoss) {
        player.getPA().sendEnterAmount("How many entries would you like to consume?", (plr, amount) -> {
            plr.getPA().closeAllWindows();
            if (alreadyActive) {
                plr.sendErrorMessage(bossName + " is already active!");
                return;
            }
            if (amount == 0 || amount > plr.VoteEntries) {
                plr.sendErrorMessage("Looks like you don't have enough vote entries!");
                return;
            }
            plr.VoteEntries -= amount;
            addActivation.accept(amount);
            plr.sendErrorMessage("You have contributed " + amount + " towards spawning the " + bossName + "!");
            PlayerHandler.executeGlobalMessage(String.format("[@red@Vote Entries@bla@] @pur@ %s @blu@has contributed %d entries Towards the %s!", plr.getDisplayName(), amount, bossName));
            PlayerHandler.executeGlobalMessage("[@red@Vote Entries@bla@] @blu@ get your Entries @ ::vote!");
        });
    }

    private static void checkAndActivateBosses() {
        if (HotDropActivation >= 100) {
            HotDropActivation = 0;
            HotDrops.handleHotDrop(VoteEntriesRandomBosses.values()[Misc.random(VoteEntriesRandomBosses.values().length-1)].getNpcId(), true);
        }
        if (DonationBossActivation >= 50) {
            DonationBossActivation = 0;
            dboss.spawnBoss();
        }
        if (VoteBossActivation >= 25) {
            VoteBossActivation = 0;
            vboss.spawnBoss();
        }
        if (DurialBossActivation >= 15) {
            DurialBossActivation = 0;
            Durial321.init();
        }
        if (GrootBossActivation >= 15) {
            GrootBossActivation = 0;
            Groot.spawnGroot();
        }
    }
}
