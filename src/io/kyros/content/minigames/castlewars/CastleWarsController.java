package io.kyros.content.minigames.castlewars;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.combat.melee.MeleeData;
import io.kyros.content.skills.Skill;
import io.kyros.model.SkillExperience;
import io.kyros.model.controller.Controller;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.ContainerUpdate;
import io.kyros.util.logging.player.CastlewarsEntranceExitLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;

public class CastleWarsController implements Controller {

    private static final Logger logger = LoggerFactory.getLogger(CastleWarsController.class);

    @Override
    public String getKey() {
        return "castlewars";
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(new Boundary(2368, 3072, 2431, 3135), new Boundary(2368, 9472, 2431, 9535));
    }

    @Override
    public void added(Player player) {
        player.debug("Enter castle wars");
        if (!player.getCastlewarsSkillBackup().isEmpty()) {
            player.sendMessage("@red@There was an error entering castle wars.");
            logger.error("Player already has backup skills set, shouldn't be possible to join again: {}", player);
            player.moveTo(new Position(Configuration.RESPAWN_X, Configuration.RESPAWN_Y));
        } else {
            Arrays.stream(Skill.getCombatSkills()).forEach(skill ->
                    player.getCastlewarsSkillBackup().add(new SkillExperience(player, skill)));

            Server.getLogging().write(new CastlewarsEntranceExitLog(player, true, player.getCastlewarsSkillBackup(), player.getItems().getInventoryItems(), player.getItems().getEquipmentItems()));

            player.saveItemsForMinigame(); // TODO log this
            player.magicBookBackup = player.playerMagicBook;
        }

        player.getPotions().resetOverload();

        player.getPotions().resetInfPrayer();

        //sendInterface to select equipment
        player.CastleWarsEquip = 0;
        for (int i = 0; i < 7; i++) {
            player.playerLevel[i] = 99;
            player.playerXP[i] = player.getPA().getXPForLevel(99) + 1;
            player.appendHeal(99, HitMask.ARMOUR_MAX);
            player.getPA().refreshSkill(i);
            player.getPA().setSkillLevel(i, player.playerLevel[i], player.playerXP[i]);
            player.getPA().levelUp(i);
        }
        CastleWarsEquipment.forceEquipOnJoin(player);
        CastleWarsEquipment.displayInterface(player);
        player.getPA().showOption(3, 0, "Attack");
    }

    @Override
    public void removed(Player player) {
        player.debug("Leave castle wars");

        if (player.getCastlewarsSkillBackup().isEmpty()) {
            player.sendMessage("@red@There was an error restoring your skills, contact staff.");
            logger.error("No skills backup for player: {}, resetting skills to default.", player);
            player.resetSkills();
            player.getPA().refreshSkills();
        } else {
            // TODO log here what levels were set to
            player.getCastlewarsSkillBackup().forEach(skill -> player.setLevel(skill.getSkill(), skill.getExperience(), true));
        }

        player.getItems().deleteAllItems();
        player.getItems().deleteEquipment();
        player.restoreItemsForMinigame();

        Server.getLogging().write(new CastlewarsEntranceExitLog(player, false, player.getCastlewarsSkillBackup(), player.getItems().getInventoryItems(), player.getItems().getEquipmentItems()));

        player.getCastlewarsSkillBackup().clear();
        player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        player.getItems().sendEquipmentContainer();
        MeleeData.setWeaponAnimations(player);
        player.getCombatPrayer().resetPrayers();
        player.getItems().calculateBonuses();
        player.getPA().showOption(3, 0, "null");

        // Only remove if the player has a walkable interface
        if (player.getPA().hasWalkableInterface())
            player.getPA().removeWalkableInterface();

        if (player.magicBookBackup == 0) {
            player.setSidebarInterface(6, 938);
            player.playerMagicBook = 0;
        } else if (player.magicBookBackup == 1) {
            player.playerMagicBook = 1;
            player.setSidebarInterface(6, 838);
        } else if (player.magicBookBackup == 2) {
            player.setSidebarInterface(6, 29999);
            player.playerMagicBook = 2;
        }

        CastleWarsLobby.deleteGameItems(player);
    }

    @Override
    public boolean onPlayerOption(Player player, Player clicked, String option) {
        return false;
    }

    @Override
    public boolean canMagicTeleport(Player player) {
        return false;
    }

    @Override
    public void onLogin(Player player) {
        player.moveTo(new Position(Configuration.RESPAWN_X, Configuration.RESPAWN_Y));
    }

    @Override
    public void onLogout(Player player) {
        if (CastleWarsLobby.isInCwWait(player)) {
            CastleWarsLobby.leaveWaitingRoom(player);
        }
        if (CastleWarsLobby.isInCw(player)) {
            CastleWarsLobby.removePlayerFromCw(player);
        }
    }
}
