package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.upgrade.UpgradeMaterials;
import io.kyros.model.entity.player.Player;

public class Upgrade extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getUpgradeInterface().openInterface(UpgradeMaterials.UpgradeType.WEAPON);
        player.getPA().showInterface(35000);
    }
}
