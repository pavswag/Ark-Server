package io.kyros.content.worldevent.impl;

import io.kyros.content.commands.Command;
import io.kyros.content.commands.all.Outlast;
import io.kyros.content.tournaments.TourneyManager;
import io.kyros.content.worldevent.WorldEvent;
import io.kyros.model.entity.MobList;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;

public class TournamentWorldEvent implements WorldEvent {

    private final TourneyManager tourney = TourneyManager.getSingleton();

    @Override
    public void init() {
        tourney.openLobby();
    }

    @Override
    public void dispose() {
        tourney.endGame();
    }

    @Override
    public boolean isEventCompleted() {
        return !tourney.isLobbyOpen() && !tourney.isArenaActive();
    }

    @Override
    public String getCurrentStatus() {
        return tourney.getTimeLeft();
    }

    @Override
    public String getEventName() {
        return "Outlast";
    }

    @Override
    public String getStartDescription() {
        return "starts";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return Outlast.class;
    }

    @Override
    public void announce(MobList<Player> players) {
        String name = tourney.getTournamentType();
        if (tourney.getTournamentType().equalsIgnoreCase("DYOG")) {
            name = "Dig Your Own Grave";
        }
        new Broadcast("[@cr20@TOURNAMENT@cr20@] " + name + " style will begin soon, type ::outlast!").addTeleport(new Position(3109, 3480, 0)).copyMessageToChatbox().submit();
    }
}
