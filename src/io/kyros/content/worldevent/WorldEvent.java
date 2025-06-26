package io.kyros.content.worldevent;

import io.kyros.content.commands.Command;
import io.kyros.content.worldevent.impl.TournamentWorldEvent;
import io.kyros.content.worldevent.impl.WGWorldEvent;
import io.kyros.model.entity.MobList;
import io.kyros.model.entity.player.Player;

public interface WorldEvent {

    default boolean isOutlast() {
        return getClass().equals(TournamentWorldEvent.class);
    }

    default boolean isWG() {
        return getClass().equals(WGWorldEvent.class);
    }

    void init();

    void dispose();

    boolean isEventCompleted();

    String getCurrentStatus();

    String getEventName();

    /**
     * Start description would be something like starts/spawns/begins in x minutes.
     */
    String getStartDescription();

    Class<? extends Command> getTeleportCommand();

    void announce(MobList<Player> players);
}
