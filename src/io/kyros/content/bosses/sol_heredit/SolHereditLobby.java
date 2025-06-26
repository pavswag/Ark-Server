package io.kyros.content.bosses.sol_heredit;

import io.kyros.annotate.PostInit;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.util.Misc;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class SolHereditLobby {
    public static List<Player> party = new ArrayList<>();
    public static int countdown = 101;
    public static boolean superiorSpawnNext = false;
    private static final Coordinate centerCoord = Boundary.centre(Boundary.COLOSSEUM);

    public static SolHereditNpc boss = null;

    public static void onEnter(Player player) {
        TaskManager.submit(new Task(1) {
            @Override
            protected void execute() {
                party.add(player);
                player.getPA().startTeleport(1825, 3102, 0, "foundry", false);
                stop();
            }
        });
    }
    @PostInit
    public static void startProcess() {
        System.out.println(centerCoord);
        TaskManager.submit(new Task() {
            @Override
            protected void execute() {
                if(party.isEmpty() || boss != null) {
                    countdown = 101;
                    return;
                }
                countdown--;
                if(countdown == 400) {
                    party.forEach(player -> {
                        player.getPA().sendNotification("The boss fight will commence in 4 minutes!", superiorSpawnNext ? "Superior Spawn" : "Regular Spawn", "Sol Heredit");
                    });
                }
                if(countdown == 300) {
                    party.forEach(player -> {
                        player.getPA().sendNotification("Sol Heredit", superiorSpawnNext ? "Superior Spawn" : "Regular Spawn", "The boss fight will commence in 3 minutes!");
                    });
                    String message = new MessageBuilder()
                            .color(MessageColor.ORANGE)
                            .text("A ")
                            .color(MessageColor.RED)
                            .icon(194)
                            .text(" Superior Sol Heredit ")
                            .icon(194)
                            .color(MessageColor.ORANGE)
                            .text(" will spawn in ")
                            .bracketed(String.valueOf(3), MessageColor.RED)
                            .text(" minutes!")
                            .build();
                    if(superiorSpawnNext)
                        PlayerHandler.executeGlobalMessage(message);
                }
                if(countdown == 200) {
                    party.forEach(player -> {
                        player.getPA().sendNotification("Sol Heredit", superiorSpawnNext ? "Superior Spawn" : "Regular Spawn", "The boss fight will commence in 2 minutes!");
                    });
                }
                if(countdown == 100) {
                    party.forEach(player -> {
                        player.getPA().sendNotification("Sol Heredit", superiorSpawnNext ? "Superior Spawn" : "Regular Spawn", "The boss fight will commence in 1 minute!");
                    });
                    String message = new MessageBuilder()
                            .color(MessageColor.ORANGE)
                            .text("A ")
                            .color(MessageColor.RED)
                            .icon(194)
                            .text(" Superior Sol Heredit ")
                            .icon(194)
                            .color(MessageColor.ORANGE)
                            .text(" will spawn in ")
                            .bracketed(String.valueOf(1), MessageColor.RED)
                            .text(" minute!")
                            .build();
                    if(superiorSpawnNext)
                        PlayerHandler.executeGlobalMessage(message);
                }
                if(countdown == 0) {
                    spawnBoss();
                    countdown = 500;
                }
            }
        });
    }
    private static void spawnBoss() {
        party.stream().filter(Boundary.COLOSSEUM::in).forEach(player -> {
            int minutesToKill = superiorSpawnNext ? 15 : 10;
            player.sendMessage("The boss has spawned! You have " + minutesToKill + " minutes to kill it before it de-spawns!");
        });
        boss = new SolHereditNpc(superiorSpawnNext ? 12783 : 12821, new Position(1824, 3106, 0));
        NPCSpawning.spawn(boss);

        TaskManager.submit(new Task() {
            int ticks = 0;
            @Override
            protected void execute() {
                ticks++;
                if(boss == null)
                    stop();
                if(ticks == (superiorSpawnNext ? 100 * 15 : 100 * 10)) {
                    NPCHandler.despawn(boss.getNpcId(), 0);
                    boss = null;
                    party.forEach(plr -> {
                        plr.sendMessage("The boss was not killed in time!");
                    });
                }
            }
        });
        if(Misc.trueRand(100) > 95 && !superiorSpawnNext) {
            superiorSpawnNext = true;

            String message = new MessageBuilder()
                    .color(MessageColor.ORANGE)
                    .text("A ")
                    .color(MessageColor.RED)
                    .icon(194)
                    .text(" Superior Sol Heredit ")
                    .icon(194)
                    .color(MessageColor.ORANGE)
                    .text(" will spawn next boss spawn! ")
                    .build();
            PlayerHandler.executeGlobalMessage(message);
        } else {
            superiorSpawnNext = false;
        }
    }
    public static void onLeave(Player player) {
        party.remove(player);
        SolHereditNpc.pointsMap.remove(Misc.playerNameToInt64(player.getLoginName().toLowerCase()));
    }
}
