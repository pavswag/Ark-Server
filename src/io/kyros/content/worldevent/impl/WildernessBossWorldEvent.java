package io.kyros.content.worldevent.impl;

import io.kyros.content.commands.Command;
import io.kyros.content.commands.all.Wildyevent;
import io.kyros.content.events.monsterhunt.MonsterHunt;
import io.kyros.content.worldevent.WorldEvent;
import io.kyros.model.entity.MobList;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;

public class WildernessBossWorldEvent implements WorldEvent {

    @Override
    public void init() {
        MonsterHunt.spawnNPC();
    }

    @Override
    public void dispose() {
        if (!isEventCompleted()) {
            MonsterHunt.despawn();
        }
    }

    @Override
    public boolean isEventCompleted() {
        return !MonsterHunt.spawned;
    }

    @Override
    public String getCurrentStatus() {
        return MonsterHunt.getTimeLeft();
    }

    @Override
    public String getEventName() {
        return "Wildy Boss";
    }

    @Override
    public String getStartDescription() {
        return "spawns";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return Wildyevent.class;
    }

    @Override
    public void announce(MobList<Player> players) {
        new Broadcast(MonsterHunt.getName() + " has spawned at "
                + MonsterHunt.getCurrentLocation().getLocationName() + ", use ::wildyevent to teleport!").addTeleport(new Position(MonsterHunt.getCurrentLocation().getX(), MonsterHunt.getCurrentLocation().getY(), 0)).copyMessageToChatbox().submit();
    }
}
