package io.kyros.content.commands.punishment.impl;

import io.kyros.content.commands.punishment.OnlinePlayerPunishmentPCP;
import io.kyros.model.entity.player.Player;
import io.kyros.punishments.PunishmentType;
import io.kyros.util.dateandtime.TimeSpan;

public class Ban extends OnlinePlayerPunishmentPCP {

    @Override
    public String name() {
        return "ban";
    }

    @Override
    public PunishmentType getPunishmentType() {
        return PunishmentType.BAN;
    }

    @Override
    public void onPunishment(Player staff, Player player, TimeSpan duration) {
        player.forceLogout();
    }

    @Override
    public void onRemovePunishment(Player staff, Player player) { }

    @Override
    public String extract(Player player) {
        return player.getDisplayNameLower();
    }
}
