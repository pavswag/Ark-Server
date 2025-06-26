package io.kyros.content.commands.all;

import com.google.common.collect.Lists;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 03/03/2024
 */
public class fp extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        c.getPA().spellTeleport(1771, 3571, 0, false);

        List<String> lines = Lists.newArrayList();
        lines.add("Welcome to Gambling on Kyros.");
        lines.add("We would like you warn you that when you start gambling,");
        lines.add("THIS IS AT YOUR OWN RISK!");
        lines.add("Our staff here at ArkCane are not responsible,");
        lines.add("for you losing any items.");
        lines.add("Please be responsible when gambling,");
        lines.add("as we want you to have fun and enjoy your time here.");

        c.getPA().openQuestInterface("FlowerPoker Gambling", lines.stream().limit(149).collect(Collectors.toList()));
    }

}
