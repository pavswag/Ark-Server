package io.kyros.content.worldevent.impl;

import io.kyros.content.WeaponGames.WGManager;
import io.kyros.content.commands.Command;
import io.kyros.content.commands.all.WGames;
import io.kyros.content.worldevent.WorldEvent;
import io.kyros.model.entity.MobList;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;

public class WGWorldEvent implements WorldEvent {

    private final WGManager wgManager = WGManager.getSingleton();

    @Override
    public void init() {
        wgManager.openLobby();
    }

    @Override
    public void dispose() {
        wgManager.endGame();
    }

    @Override
    public boolean isEventCompleted() {
        return !wgManager.isLobbyOpen() && !wgManager.isArenaActive();
    }

    @Override
    public String getCurrentStatus() {
        return wgManager.getTimeLeft();
    }

    @Override
    public String getEventName() {
        return "WeaponGames";
    }

    @Override
    public String getStartDescription() {
        return "starts";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return WGames.class;
    }

    @Override
    public void announce(MobList<Player> players) {
        new Broadcast("@cr20@<col=ff7000><shad=ffff00>[WG]:</shad>@cr20@ <col=A0002C> starting soon speak to lisa outside outlast, ;;WGames").addTeleport(new Position(3094, 3501, 0)).copyMessageToChatbox().submit();
    }
}
