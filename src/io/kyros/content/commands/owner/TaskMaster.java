package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class TaskMaster extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
/*        c.getPA().sendString(38002, "Kill @whi@Nex @or1@55 times.");
        c.getPA().sendString(38003, "@yel@24H - 21M");
        c.getPA().sendString(38004, "@gre@Complete");
        c.getPA().sendString(38005, "Kill @whi@Nex @or1@55 times.");
        c.getPA().sendString(38006, "@yel@24H - 21M");
        c.getPA().sendString(38007, "@red@Incomplete");
        c.getPA().sendString(38008, "Complete @whi@Trials @or1@55 times.");
        c.getPA().sendString(38009, "4D - 24H - 21M");
        c.getPA().sendString(38010, "@gre@Complete");

        c.getPA().sendConfig(5000, 1);
        c.getPA().sendConfig(5001, 0);
        c.getPA().sendConfig(5002, 1);
        c.getPA().showInterface(38000);*/
        c.getTaskMaster().showInterface();
 }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Takes you to the AFK Zone.");
    }
}

