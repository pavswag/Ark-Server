package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.specials.Special;
import io.kyros.content.skills.Skill;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;

public class DragonHarpoon extends Special {
	public DragonHarpoon() {
		super(10.0, 1.0, 1.0, new int[] { 21028 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.forcedChat("Here fishy fishies!");
		player.gfx0(246);
		player.playerLevel[Skill.FISHING.getId()] = player.getLevelForXP(player.playerXP[Skill.FISHING.getId()]) + 3;
		player.getPA().refreshSkill(Skill.FISHING.getId());
	}


	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}
}
