package io.kyros.content.combat.effects.damageeffect.impl.bows;

import java.util.Optional;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.kyros.content.combat.effects.damageeffect.DamageEffect;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class ToxicBlowpipeEffect implements DamageBoostingEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		defender.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(attacker));
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		defender.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(attacker));
	}

	@Override
	public boolean isExecutable(Player operator, Entity victim) {
		return operator.getItems().isWearingItem(12926) && Misc.random(3) == 0 || operator.getItems().isWearingItem(28688) && Misc.random(3) == 0;
	}

	@Override
	public double getMaxHitBoost(Player attacker, Entity defender) {
		return 0;
	}

	@Override
	public double getAccuracyBoost(Player attacker, Entity defender) {
		return 0;
	}
}
