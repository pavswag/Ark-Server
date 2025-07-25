package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.formula.rework.MeleeCombatFormula;
import io.kyros.content.combat.specials.Special;
import io.kyros.content.skills.Skill;
import io.kyros.model.CombatType;
import io.kyros.model.SoundType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerAssistant;
import io.kyros.util.Misc;

public class DragonClaws extends Special {

	public DragonClaws() {
		super(5.0, 2.0, 1.0, new int[] { 20784, 26708, 28534 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7514);
		player.gfx0(1171);
		player.getPA().sendSound(2537, SoundType.AREA_SOUND);
		int max = MeleeCombatFormula.get().getMaxHit(player, target, 1.0, 1.0);

		if (damage.getAmount() == 0) {
			int second = Misc.random(0, max);
			if (second == 0) {
				doHit(player, target, 0, 0);
				doHit(player, target, (int) (max * 0.75d), 1);
				doHit(player, target, (int) (max * 0.75d), 1);
			} else {
				doHit(player, target, second, 0);
				doHit(player, target, second / 2, 1);
				doHit(player, target, second / 2, 1);
			}
		} else {
			int halvedHit = damage.getAmount() == 0 ? 0 : damage.getAmount() / 2;
			int finalHit = halvedHit == 0 ? 0 : halvedHit / 2;
			doHit(player, target, halvedHit, 0);
			doHit(player, target, finalHit, 1);
			doHit(player, target, finalHit, 1);
		}
	}

	private void doHit(Player player, Entity target, int damage, int delay) {
		player.getDamageQueue().add(new Damage(target, damage, player.hitDelay + delay, player.playerEquipment, damage > 0 ? HitMask.HIT : HitMask.MISS, CombatType.MELEE));
		player.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage, Skill.ATTACK.getId()));
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
