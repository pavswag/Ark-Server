package io.kyros.content.bots;

import io.kyros.Server;
import io.kyros.model.Items;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.util.Misc;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 29/11/2023
 */
public class BasicBot {

    public static void startBots() {
        Player player = Player.createBot(generateUsername(), Right.PLAYER);

        if (generateMode().equals(ModeType.IRON_MAN)) {
            player.setMode(Mode.forType(ModeType.IRON_MAN));
            player.getRights().setPrimary(Right.IRONMAN);

            player.getItems().equipItem(Items.IRONMAN_HELM, 1, Player.playerHat);
            player.getItems().equipItem(Items.IRONMAN_PLATEBODY, 1, Player.playerChest);
            player.getItems().equipItem(Items.IRONMAN_PLATELEGS, 1, Player.playerLegs);
            player.getItems().equipItem(1323, 1, Player.playerWeapon);
        } else {
            player.setMode(Mode.forType(ModeType.STANDARD));

            player.getItems().equipItem(1323, 1, Player.playerWeapon);
            player.getItems().equipItem(1153, 1, Player.playerHat);
            player.getItems().equipItem(1115, 1, Player.playerChest);
            player.getItems().equipItem(1067, 1, Player.playerLegs);
        }

        player.autoRet = 1;

        int rockbot = 0;
        int cowbot = 0;
        for (Player player1 : Server.getPlayers().toPlayerArray()) {
            if (player1.isBot() && Boundary.isIn(player1, new Boundary(2655, 3712, 2687, 3741))) {
                rockbot++;
            } else if (player1.isBot() && Boundary.isIn(player1, new Boundary(3240, 3252, 3266, 3299))) {
                cowbot++;
            }
        }

        if (rockbot < 3) {
            miniRockBot(player);
        } else if (cowbot < 3) {
            miniCowBot(player);
        } else {
            afkBot(player);
        }
    }

    private static ModeType generateMode() {
        int rng = Misc.random(0, 10);

        if (rng > 0 && rng < 5) {
            return ModeType.IRON_MAN;
        } else if (rng > 5) {
            return ModeType.STANDARD;
        }

        return ModeType.STANDARD;
    }

    private static void afkBot(Player player) {
        Position afkSpot = new Position(2462, 2847, 2);

        player.addQueuedAction(plr -> plr.moveTo(afkSpot));

        CycleEventHandler.getSingleton().stopEvents(player);
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (player.isDisconnected()) {
                    container.stop();
                    return;
                }

                if (container.getTotalTicks() % 6 == 0) {
                    player.startAnimation(881);
                }

            }
        },6);
    }

    private static void miniCowBot(Player player) {
        Position rockCrabs1 = new Position(3249, 3290, 0);
        Position rockCrabs2 = new Position(3259, 3281, 0);
        Position rockCrabs3 = new Position(3259, 3262, 0);

        int rng = Misc.random(0, 2);

        switch (rng) {
            case 0:
                player.addQueuedAction(plr -> plr.moveTo(rockCrabs1));
                break;
            case 1:
                player.addQueuedAction(plr -> plr.moveTo(rockCrabs2));
                break;
            case 2:
                player.addQueuedAction(plr -> plr.moveTo(rockCrabs3));
                break;
        }

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                List<NPC> npcList = Server.getNpcs().nonNullStream()
                        .filter(npc1 -> npc1.underAttackBy <= 0 &&
                                npc1.getPosition().withinDistance(player.getPosition(), 10)).collect(Collectors.toList());


                NPC npc = npcList.get(Misc.random(npcList.size()-1));
                if (npc != null) {
                    if (player.getPosition().withinDistance(npc.getPosition(), 10)) {
                        if (player.attacking.attackEntityCheck(npc, false)) {
                            player.attackEntity(npc);
                        } else {
                            player.attacking.reset();
                        }
                    }
                }
            }
        },Misc.random(2,5));

    }
    private static void miniRockBot(Player player) {
        Position rockCrabs1 = new Position(2682, 3719, 0);
        Position rockCrabs2 = new Position(2682, 3729, 0);
        Position rockCrabs3 = new Position(2671, 3728, 0);

        int rng = Misc.random(0, 2);

        switch (rng) {
            case 0:
                player.addQueuedAction(plr -> plr.moveTo(rockCrabs1));
                break;
            case 1:
                player.addQueuedAction(plr -> plr.moveTo(rockCrabs2));
                break;
            case 2:
                player.addQueuedAction(plr -> plr.moveTo(rockCrabs3));
                break;
        }

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                List<NPC> npcList = Server.getNpcs().nonNullStream().filter(npc1 -> npc1.underAttackBy <= 0).filter(npc1 -> npc1.getPosition().withinDistance(player.getPosition(), 10)).collect(Collectors.toList());

                if(npcList.isEmpty()) {
                    System.out.println("NpcList is empty for basic bot");
                    container.stop();
                    return;
                }
                NPC npc = npcList.get(Misc.random(npcList.size()-1));
                if (npc != null) {
                    if (player.getPosition().withinDistance(npc.getPosition(), 10)) {
                        if (player.attacking.attackEntityCheck(npc, false)) {
                            player.attackEntity(npc);
                        } else {
                            player.attacking.reset();
                        }
                    }
                }
            }
        }, 5);

    }

    private static void handleRandomDonation() {
        if (Server.randomDonor > System.currentTimeMillis())
            return;

        Player p = Server.getPlayers().stream().filter(Player::isBot)
                .collect(Collectors.toList())
                .get(Misc.random((int) Server.getPlayers().stream().filter(Player::isBot).count()));

        Server.randomDonor = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(Misc.random(10,24));

    }

    private static final String[] PREFIXES = {"alpha", "beta", "gamma", "delta", "omega", "sigma"};
    private static final String[] SUFFIXES = {"_user", "_123", "_x", "_gen", "_42", "_java"};

    private static String generateUsername() {
        SecureRandom random = new SecureRandom();

        // Randomly choose a prefix and suffix
        String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
        String suffix = SUFFIXES[random.nextInt(SUFFIXES.length)];

        // Generate a random sequence of characters
        String randomChars = generateRandomChars(5);

        // Combine the elements to create the username
        return prefix + randomChars + suffix;
    }

    private static String generateRandomChars(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder randomChars = new StringBuilder();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            randomChars.append(chars.charAt(index));
        }

        return randomChars.toString();
    }
}
