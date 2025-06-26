package io.kyros.content.combat.effects.damageeffect;

import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;

public interface DamageBoostingEffect extends DamageEffect {

    double getMaxHitBoost(Player attacker, Entity defender);

    double getAccuracyBoost(Player attacker, Entity defender);

}
