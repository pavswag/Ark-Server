package io.kyros.content.worldevent.impl;

import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.bosses.hespori.HesporiSpawner;
import io.kyros.content.commands.Command;
import io.kyros.content.commands.all.Worldevent;
import io.kyros.content.worldevent.WorldEvent;
import io.kyros.model.entity.MobList;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.util.discord.Discord;

public class HesporiWorldEvent implements WorldEvent {
    @Override
    public void init() {
        HesporiSpawner.spawnNPC();
    }

    @Override
    public void dispose() {
        if (!isEventCompleted()) {
            Hespori.rewardPlayers(false);
        }
    }

    @Override
    public boolean isEventCompleted() {
        return !HesporiSpawner.isSpawned();
    }

    @Override
    public String getCurrentStatus() {
        return "World Event: @gre@Hespori";
    }

    @Override
    public String getEventName() {
        return "Hespori";
    }

    @Override
    public String getStartDescription() {
        return "spawns";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return Worldevent.class;
    }

    @Override
    public void announce(MobList<Player> players) {
        Discord.writeBugMessage("Hespori world boss has spawned, use ::worldevent to fight! <@&1121100008884801667>");
        new Broadcast("Hespori world boss has spawned, use ::worldevent to fight!").addTeleport(new Position(2457, 3553, 0)).copyMessageToChatbox().submit();
    }
}
