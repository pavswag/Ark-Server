package io.kyros.content.combat.effects.damageeffect.impl.melee;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.Optional;

public class NoxiousHally implements DamageBoostingEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		boolean venom = false;
		if((attacker.getItems().isWearingItem(12931) || attacker.getItems().isWearingItem(13199) || attacker.getItems().isWearingItem(13197))) {
			venom = Misc.random(1D, 10D) <= 5;
		} else {
			venom = Misc.random(1D, 10D) <= 3.33;
		}
		if(venom) {
			defender.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(attacker));
		}
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		boolean venom = false;
		if((attacker.getItems().isWearingItem(12931) || attacker.getItems().isWearingItem(13199) || attacker.getItems().isWearingItem(13197))) {
			venom = Misc.random(1D, 10D) <= 5;
		} else {
			venom = Misc.random(1D, 10D) <= 3.33;
		}
		if(venom) {
			defender.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(attacker));
		}
	}

	@Override
	public boolean isExecutable(Player operator, Entity victim) {
		return operator.getItems().isWearingItem(29796);
	}

	@Override
	public double getMaxHitBoost(Player attacker, Entity defender) {
		return 0.0225;
	}

	@Override
	public double getAccuracyBoost(Player attacker, Entity defender) {
		return 0;
	}
}
