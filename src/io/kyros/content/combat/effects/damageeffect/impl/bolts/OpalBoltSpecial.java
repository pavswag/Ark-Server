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

public class OpalBoltSpecial implements DamageBoostingEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		int change = Misc.random((int) (damage.getAmount() * 1.25));
		damage.setAmount(change);
		RangeData.createCombatGraphic(defender, 749, false);
		attacker.getPA().sendSound(2918, SoundType.AREA_SOUND);
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		if (defender.getDefinition().getName() == null) {
			return;
		}
		RangeData.createCombatGraphic(defender, 749, false);
	}

	@Override
	public boolean isExecutable(Player operator, Entity victim) {
		return RangeData.boltSpecialAvailable(operator, Items.OPAL_BOLTS_E, Items.OPAL_DRAGON_BOLTS_E);
	}

	@Override
	public double getMaxHitBoost(Player attacker, Entity defender) {
		return 0.25;
	}

	@Override
	public double getAccuracyBoost(Player attacker, Entity defender) {
		return 0;
	}

}
