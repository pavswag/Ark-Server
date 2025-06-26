package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.combat.stats.MonsterKillLog;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class KillLog extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        MonsterKillLog.openInterface(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens the kill log.");
    }

}
