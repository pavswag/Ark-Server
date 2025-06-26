package io.kyros;

import io.kyros.content.QuestTab;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.*;
import io.kyros.content.bosses.baldeagle.Eagle;
import io.kyros.content.bosses.wintertodt.Wintertodt;
import io.kyros.content.donationcampaign.DonationCampaign;
import io.kyros.content.events.monsterhunt.ShootingStars;
import io.kyros.content.instances.InstanceHeight;
import io.kyros.content.minigames.blastfurnance.BlastFurnace;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.minigames.pk_arena.Highpkarena;
import io.kyros.content.minigames.pk_arena.Lowpkarena;
import io.kyros.content.minigames.wanderingmerchant.Merchant;
import io.kyros.content.vote_panel.VoteTracker;
import io.kyros.content.votemanager.VoteManager;
import io.kyros.content.wilderness.ActiveVolcano;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.cycleevent.EventHandler;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.world.ItemHandler;
import io.kyros.model.world.ShopHandler;
import io.kyros.model.world.objects.GlobalObjects;
import io.kyros.net.ChannelHandler;
import io.kyros.net.login.RS2LoginProtocol;
import io.kyros.sql.dailytracker.DailyDataTracker;
import io.kyros.util.Misc;
import io.kyros.util.discord.DiscordIntegration;
import io.kyros.util.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

import java.util.List;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class GameThread {

    public static final String THREAD_NAME = "GameThread";
    private static final Logger logger = LoggerFactory.getLogger(GameThread.class);
    private final List<Consumer<GameThread>> tickables = new ArrayList<>();
    private final Runnable startup;
    private long totalCycleTime = 0;

    public static final int PRIORITY = Thread.NORM_PRIORITY + 1; // Or any other value you prefer

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ExecutorService taskExecutor = Executors.newCachedThreadPool(); // Or fixedThreadPool based on expected load

    public GameThread(Runnable startup) {
        this.startup = startup;

        this.startup.run();

        setTickables();
    }

    private void setTickables() {
        tickables.add(i -> Server.itemHandler.process());
        tickables.add(i -> Server.npcHandler.process());
        tickables.add(i -> Server.playerHandler.process());
        tickables.add(i -> Server.shopHandler.process());
        tickables.add(i -> Highpkarena.process());
        tickables.add(i -> Lowpkarena.process());
        tickables.add(i -> DiscordIntegration.givePoints());
        tickables.add(i -> ActiveVolcano.Tick());
        tickables.add(i -> ShootingStars.Tick());
        tickables.add(i -> Server.getGlobalObjects().pulse());
        tickables.add(i -> CycleEventHandler.getSingleton().process());
        tickables.add(i -> Server.getEventHandler().process());
        tickables.add(i -> Wintertodt.pulse());
        tickables.add(i -> DonorBoss.tick());
        tickables.add(i -> DonorBoss2.tick());
        tickables.add(i -> DonorBoss3.tick());
        tickables.add(i -> DonorBoss4.tick());
//        tickables.add(i -> SuperDz.tick());
        tickables.add(i -> CastleWarsLobby.process());
     //   tickables.add(i -> DonationCampaign.cleanDatabaseIfFirstOfMonth());
        tickables.add(i -> Pass.tick());
        tickables.add(i -> BlastFurnace.process());
        tickables.add(i -> TaskManager.sequence());
        tickables.add(i -> VoteTracker.periodicCleanup());
        tickables.add(i -> Eagle.tick());
        tickables.add(i -> Merchant.handleTick());
        tickables.add(i -> QuestTab.Tick());
        tickables.add(i -> Server.tickCount++);
        tickables.add(i -> DailyDataTracker.newDay());
        //tickables.add(i -> VoteManager.getInstance().tick());
    }

    public void startGameLoop() {
        // Schedule the game loop to run every 600ms
        scheduler.scheduleAtFixedRate(this::tick, 0, 600, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        long start = System.currentTimeMillis();
        for (Consumer<GameThread> tickable : tickables) {
            try {
                tickable.accept(this);
            } catch (Exception e) {
                logger.error("Error caught in GameThread, should be caught up the chain and handled.", e);
            }
        }

        // Log status periodically
        if (Server.getTickCount() % 50 == 0) {
            logStatus();
        }

        long cycleTime = System.currentTimeMillis() - start;
        totalCycleTime += cycleTime;

        if (cycleTime >= 600) {
            logger.error("Game loop took too long: " + cycleTime + "ms");
        }
    }

    private void logStatus() {
        StringJoiner joiner = new StringJoiner(", ");
        joiner.add("runtime=" + Misc.cyclesToTime(Server.getTickCount()));
        joiner.add("connections=" + ChannelHandler.getActiveConnections());
        joiner.add("players=" + Server.getPlayers().size());
        joiner.add("uniques=" + PlayerHandler.getUniquePlayerCount());
        joiner.add("npcs=" + (int) NPCHandler.nonNullStream().count());
        joiner.add("reserved-heights=" + InstanceHeight.getReservedCount());
        joiner.add("average-cycle-time=" + (totalCycleTime / Server.getTickCount()) + "ms");
        joiner.add("handshakes-per-tick=" + (RS2LoginProtocol.getHandshakeRequests() / Server.getTickCount()));

        long totalMemory = Runtime.getRuntime().totalMemory();
        long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
        joiner.add("memory=" + Misc.formatMemory(usedMemory) + "/" + Misc.formatMemory(totalMemory));

        logger.info("Status [" + joiner.toString() + "]");
    }

    public void stopGameLoop() {scheduler.shutdown();
        taskExecutor.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            if (!taskExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            taskExecutor.shutdownNow();
        }
    }
}
