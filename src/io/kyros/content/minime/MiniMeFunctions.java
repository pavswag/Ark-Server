package io.kyros.content.minime;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.net.login.LoginReturnCode;
import io.kyros.net.login.RS2LoginProtocol;
import io.kyros.util.Misc;

public class MiniMeFunctions {

    public static void create(Player p) {
        Player mini = new Player(null);
        if (p.getMiniMe() != null) {
            p.sendMessage("@red@You call your mini-me!");
            startFollowing(p);
            return;
        }

        p.setMiniMe(mini);
        //TODO register in world?, add miniMe check to player file, Clone all skill levels
        p.sendMessage("@red@You call your mini-me!");

        mini.getRights().setPrimary(p.getRights().getPrimary());
        mini.setMode(p.getMode());
        mini.saveCharacter = true;
        mini.setCompletedTutorial(true);
        mini.setLoginName("Mini " + p.getDisplayName());
        mini.setDisplayName("Mini " + p.getDisplayName());
        mini.macAddress = "";
        mini.setNameAsLong(Misc.playerNameToInt64("Mini " + p.getDisplayName()));
        mini.playerPass = "playerbot123";
        mini.setIpAddress("");
        mini.isMiniMe = true;
        mini.bot = true;
        mini.MiniMeOwner = p;
        mini.playerAppearance = p.playerAppearance;
        mini.appearanceUpdateRequired = true;

        mini.addQueuedAction(plr -> plr.moveTo(p.getPosition()));

        p.getMiniMe().playerAttackingIndex = 0;
        p.getMiniMe().npcAttackingIndex = 0;
        p.getMiniMe().usingBow = false;
        p.getMiniMe().usingRangeWeapon = false;
        p.getMiniMe().followDistance = 1;
        p.getMiniMe().playerFollowingIndex = p.getIndex();
        p.getMiniMe().combatFollowing = false;

        p.getMiniMe().getPA().followPlayer();

        Server.getIoExecutorService().submit(() -> {
            try {
                LoginReturnCode code = RS2LoginProtocol.loadPlayer(mini, mini.getLoginNameLower(), LoginReturnCode.SUCCESS, true);
                if (code != LoginReturnCode.SUCCESS) {
                    System.out.println("Could not login bot, return code was "+ code);
                    return;
                }

                PlayerHandler.addLoginQueue(mini);
            } catch (Exception e) {
                System.out.println("Error loading MiniMe " + e);
            }
        });

    }


    public static void startFollowing(Player owner) {

        if (owner.getMiniMe() == null) {
            owner.sendMessage("You need to summon your mini me first.");
            return;
        }

        if (owner.getMiniMe().playerFollowingIndex == owner.getIndex()) {
            owner.sendMessage("Your minime is already following you.");
            return;
        }


        owner.getMiniMe().moveTo(owner.getPosition());
        owner.sendMessage("Your minime begins to follow you.");
        owner.getMiniMe().playerAttackingIndex = 0;
        owner.getMiniMe().npcAttackingIndex = 0;
        owner.getMiniMe().usingBow = false;
        owner.getMiniMe().usingRangeWeapon = false;
        owner.getMiniMe().followDistance = 1;
        owner.getMiniMe().playerFollowingIndex = owner.getIndex();
        owner.getMiniMe().combatFollowing = false;
        owner.getMiniMe().getPA().followPlayer();
    }

    public static void stopFollowing(Player owner) {
        if (owner.getMiniMe() == null) {
            owner.sendMessage("You need to summon your mini me first.");
            return;
        }

        owner.getMiniMe().playerAttackingIndex = 0;
        owner.getMiniMe().npcAttackingIndex = 0;
        owner.getMiniMe().usingBow = false;
        owner.getMiniMe().usingRangeWeapon = false;
        owner.getMiniMe().followDistance = 1;
        owner.getMiniMe().playerFollowingIndex = 0;
        owner.getMiniMe().combatFollowing = false;
        owner.sendMessage("Your minime stops following you.");

    }

}
