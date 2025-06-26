package io.kyros.content.combat.effects.damageeffect.impl.armour;

import java.util.Optional;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.kyros.content.combat.effects.damageeffect.DamageEffect;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class SerpentineHelmEffect implements DamageBoostingEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		attacker.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(defender));
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		defender.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(attacker));
	}

	@Override
	public boolean isExecutable(Player operator, Entity victim) {
		return (operator.getItems().isWearingItem(12931) || operator.getItems().isWearingItem(13199) || operator.getItems().isWearingItem(13197)) && Misc.random(5) == 1;
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
