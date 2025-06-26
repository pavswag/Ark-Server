package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.net.login.LoginRequestLimit;

public class GetLoginLimit extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
       player.sendMessage("Login rate limit is set to {}", "" + LoginRequestLimit.MAX_LOGINS_PER_TICK);
    }
}
