package io.kyros.content.tournaments;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.combat.melee.MeleeData;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.skills.Skill;
import io.kyros.model.SkillExperience;
import io.kyros.model.controller.Controller;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.ContainerUpdate;
import io.kyros.util.logging.player.OutlastEntranceExitLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Set;

public class OutlastController implements Controller {

    private static final Logger logger = LoggerFactory.getLogger(OutlastController.class);

    @Override
    public String getKey() {
        return "outlast";
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(Boundary.OUTLAST, Boundary.FOREST_OUTLAST, Boundary.SNOW_OUTLAST,
                Boundary.ROCK_OUTLAST, Boundary.FALLY_OUTLAST, Boundary.LUMBRIDGE_OUTLAST,
                Boundary.SWAMP_OUTLAST);
    }

    @Override
    public void added(Player player) {
        player.debug("Enter outlast, spectator={}", player.spectatingTournament);
        if (player.spectatingTournament)
            return;
        // TODO log entered outlast area, backups set to x

        if (!player.getOutlastSkillBackup().isEmpty()) {
            player.sendMessage("@red@There was an error entering outlast.");
            logger.error("Player already has backup skills set, shouldn't be possible to join again: {}", player);
            player.moveTo(new Position(Configuration.RESPAWN_X, Configuration.RESPAWN_Y));
            return;
        } else {
            Arrays.stream(Skill.getCombatSkills()).forEach(skill ->
                    player.getOutlastSkillBackup().add(new SkillExperience(player, skill)));

            Server.getLogging().write(new OutlastEntranceExitLog(player, true, player.getOutlastSkillBackup(), player.getItems().getInventoryItems(), player.getItems().getEquipmentItems()));

            player.saveItemsForMinigame(); // TODO log this
            player.magicBookBackup = player.playerMagicBook;
        }

        player.getPotions().resetOverload();

        player.getPotions().resetInfPrayer();

        TourneyManager.getSingleton().outlastEquip(player);
        player.getPA().showOption(3, 0, "Attack");
        player.getPA().walkableInterface(264);
    }

    @Override
    public void removed(Player player) {
        player.debug("Leave outlast, spectator={}", player.spectatingTournament);
        if (player.spectatingTournament)
            return;

        TourneyManager.getSingleton().leaveLobby(player, false);

        if (player.getOutlastSkillBackup().isEmpty()) {
            player.sendMessage("@red@There was an error restoring your skills, contact staff.");
            logger.error("No skills backup for player: {}, resetting skills to default.", player);
            player.resetSkills();
            player.getPA().refreshSkills();
        } else {
            // TODO log here what levels were set to
            player.getOutlastSkillBackup().forEach(skill -> player.setLevel(skill.getSkill(), skill.getExperience(), true));
        }

        player.getItems().deleteAllItems();
        player.getItems().deleteEquipment();
        player.restoreItemsForMinigame();

        Server.getLogging().write(new OutlastEntranceExitLog(player, false, player.getOutlastSkillBackup(), player.getItems().getInventoryItems(), player.getItems().getEquipmentItems()));

        player.getOutlastSkillBackup().clear();
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
        TourneyManager.getSingleton().handleLogout(player);
    }
}
