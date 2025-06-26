package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/02/2024
 */
public class Varbit extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        try {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        int varbit = Integer.parseInt(args[0]);
        int state = Integer.parseInt(args[1]);
        player.getPA().sendConfig(varbit, state);
        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::varbit-id-state");
        }

    }
}
