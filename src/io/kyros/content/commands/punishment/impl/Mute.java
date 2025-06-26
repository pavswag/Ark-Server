package io.kyros.content.commands.punishment.impl;

import io.kyros.content.StaffPanel;
import io.kyros.content.commands.punishment.OnlinePlayerPunishmentPCP;
import io.kyros.model.entity.player.Player;
import io.kyros.punishments.PunishmentType;
import io.kyros.util.dateandtime.TimeSpan;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mute extends OnlinePlayerPunishmentPCP {

    @Override
    public String name() {
        return "mute";
    }

    @Override
    public PunishmentType getPunishmentType() {
        return PunishmentType.MUTE;
    }

    @Override
    public void onPunishment(Player staff, Player player, TimeSpan duration) {
        player.muteEnd = System.currentTimeMillis() + duration.toMillis();
        player.sendMessage("@red@You have been muted by {} for {}.", staff.getDisplayNameFormatted(), duration.toString());

        StaffPanel.submitLog(player.getLoginName().toLowerCase(),
                player.getLoginName() + " was muted by " + staff.getDisplayNameFormatted() +
                        " on !date!.");
    }

    @Override
    public void onRemovePunishment(Player staff, Player player) {
        player.muteEnd = 0;
    }

    @Override
    public String extract(Player player) {
        return player.getDisplayNameLower();
    }
}
