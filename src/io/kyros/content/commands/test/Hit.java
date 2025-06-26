package io.kyros.content.commands.test;

import io.kyros.content.combat.HitMask;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class Hit extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length == 0) {
            player.sendMessage("Usage: ::hit 98");
            return;
        }

        int damage = Integer.parseInt(args[0]);
        player.appendDamage(null, damage, HitMask.HIT);
    }

    public Optional<String> getDescription() {
        return Optional.of("Damage your player");
    }
}
