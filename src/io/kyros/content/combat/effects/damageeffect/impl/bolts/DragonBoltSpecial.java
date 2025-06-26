package io.kyros.content.combat.effects.damageeffect.impl.bolts;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.kyros.content.combat.range.RangeData;
import io.kyros.model.Items;
import io.kyros.model.SoundType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class DragonBoltSpecial implements DamageBoostingEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		if (defender.antifireDelay > 0 || defender.getItems().isWearingAnyItem(11283, 11284, 1540) ||
				defender.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33118) && defender.wildLevel < 0) {
			return;
		}
		int change = Misc.random((int) (damage.getAmount() * 1.45));
		damage.setAmount(change);
		RangeData.createCombatGraphic(defender, 756, false);
		attacker.getPA().sendSound(2915, SoundType.AREA_SOUND);
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		if (defender.getDefinition().getName() != null && defender.getDefinition().getName().toLowerCase().contains("dragon")) {
			return;
		}
		attacker.ignoreDefence = true;
		RangeData.createCombatGraphic(defender, 756, false);
	}

	@Override
	public boolean isExecutable(Player operator, Entity victim) {
		return RangeData.boltSpecialAvailable(operator, Items.DRAGONSTONE_BOLTS_E, Items.DRAGONSTONE_DRAGON_BOLTS_E);
	}

	@Override
	public double getMaxHitBoost(Player attacker, Entity defender) {
		return 0.45;
	}

	@Override
	public double getAccuracyBoost(Player attacker, Entity defender) {
		return 0;
	}

}
