package io.kyros.content.commands.owner;

import io.kyros.content.combat.HitMask;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Max extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		for (int i = 0; i < 24; i++) {
			player.playerLevel[i] = 99;
			player.playerXP[i] = player.getPA().getXPForLevel(99) + 1;
			player.appendHeal(99, HitMask.ARMOUR_MAX);
			player.getPA().refreshSkill(i);
			player.getPA().setSkillLevel(i, player.playerLevel[i], player.playerXP[i]);
			player.getPA().levelUp(i);
		}
	}

}
