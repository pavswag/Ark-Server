package io.kyros.content.skills.slayer;

import io.kyros.Server;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.util.Misc;

public class DuoMode {

    public static void duoMode(Player player) {
        duoSettings(player);
    }

    public static boolean duoCheck(Player player, Player player2) {
        if (!checkMode(player, player2)) {
            player.sendMessage("You can only invite someone who is the same game mode!");
            return false;
        }

        if (!player.slayerPartner.isEmpty()) {
            player.sendMessage("You already have a partner.");
            return false;
        }

        //if (player.isSameComputer(player2)) {
        //    player.sendMessage("You cannot invite someone who is using the same network.");
        //    return false;
        //}

        if (!player2.slayerPartner.isEmpty()) {
            player.sendMessage(player2.getDisplayName() + " already has a partner.");
            return false;
        }

        if (!player.slayerParty) {
            player.sendMessage("Your duo mode is disabled!");
            return false;
        }

        if (!player2.slayerParty) {
            player.sendMessage(player2.getDisplayName() + "'s duo mode is disabled!");
            return false;
        }
        return true;
    }

    private static boolean checkMode(Player player, Player player2) {
         if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) && player2.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player2.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
             return true;
         } else if (player.getMode().equals(player2.getMode())) {
             return true;
         }
        return false;
    }

    public static void duoSettings(Player player) {
        new DialogueBuilder(player).option(
                        new DialogueOption(!player.slayerParty ? "Enable Duo Mode." : "Disable Duo Mode.", plr -> {
                            if (!plr.slayerParty) {
                                plr.slayerParty = true;
                                plr.sendMessage("@cya@Duo mode enabled!");
                            } else {
                                plr.slayerParty = false;
                                plr.sendMessage("@cya@Duo mode disabled!");
                            }
                            plr.getPA().closeAllWindows();
                        }),
                new DialogueOption("Leave DUO group.", plr -> {
                    if (!plr.slayerPartner.isEmpty()) {
                        for (Player p : Server.getPlayers()) {
                            if (p.getDisplayName().equalsIgnoreCase(plr.slayerPartner)) {
                                p.slayerPartner = "";
                            }
                        }
                        plr.slayerPartner = "";
                        plr.sendMessage("You have left your slayer DUO.");
                        plr.getPA().closeAllWindows();
                    }
                }),
                new DialogueOption("Invite DUO Partner.", plr -> {
                    if (plr.slayerPartner.isEmpty() && plr.slayerParty) {
                        duoInvite(plr);
                    }
                })
                ).send();
    }

    public static void checkPoints(Player player) {
        player.sendMessage("You currently have <col=a30027>" + Misc.insertCommas(player.getSlayer().getPoints()) + " </col>slayer points.");
    }

    public static void duoInvite(Player player) {
        player.getPA().sendEnterString("Enter friend's name", (plr1, str) -> {
            for (Player p : Server.getPlayers()) {
                if (p != null && p.getDisplayName().equalsIgnoreCase(str)) {
                    if (duoCheck(player, p)) {
                        p.slayerPartner = player.getDisplayName();
                        player.slayerPartner = p.getDisplayName();

                        p.sendMessage("You and " + plr1.getDisplayName() + " are now slayer partner!");
                        plr1.sendMessage("You and " + p.getDisplayName() + " are now slayer partner!");
                    }
                    plr1.getPA().closeAllWindows();
                }
            }
        });
    }
}
