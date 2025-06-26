package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.specials.Special;
import io.kyros.content.skills.Skill;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;

public class AncientMace extends Special {

	public AncientMace() {
		super(10.0, 1.0, 1.0, new int[] { 11061 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx0(1052);
		player.startAnimation(6147);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (target.isPlayer()) {
			if (damage.getAmount() > 0) {
				int damageDealt = damage.getAmount();
				int prayerLevel = player.getLevelForXP(player.playerXP[Skill.PRAYER.getId()]);
				int currLevel = player.playerLevel[Skill.PRAYER.getId()];
				int maxBoostedLevel = prayerLevel + damageDealt;

				if (damageDealt + currLevel > maxBoostedLevel && currLevel < maxBoostedLevel) {
					target.asPlayer().playerLevel[Skill.PRAYER.getId()] -= maxBoostedLevel;
					player.playerLevel[Skill.PRAYER.getId()] = maxBoostedLevel;
				} else {
					target.asPlayer().playerLevel[Skill.PRAYER.getId()] -= damageDealt;
					player.playerLevel[Skill.PRAYER.getId()] += damageDealt;
				}
				player.getPA().refreshSkill(5);
				target.asPlayer().getPA().refreshSkill(5);
			}
		}
	}

}
