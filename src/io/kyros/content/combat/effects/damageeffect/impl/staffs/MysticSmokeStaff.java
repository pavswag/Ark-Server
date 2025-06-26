package io.kyros.content.combat.effects.damageeffect.impl.staffs;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.kyros.model.Items;
import io.kyros.model.SpellBook;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public class MysticSmokeStaff implements DamageBoostingEffect {
    @Override
    public double getMaxHitBoost(Player attacker, Entity defender) {
        return 0.1;
    }

    @Override
    public double getAccuracyBoost(Player attacker, Entity defender) {
        return 0.0;
    }

    @Override
    public void execute(Player attacker, Player defender, Damage damage) {

    }

    @Override
    public void execute(Player attacker, NPC defender, Damage damage) {

    }

    @Override
    public boolean isExecutable(Player operator, Entity victim) {
        return operator.getItems().isWearingItem(Player.playerWeapon, Items.MYSTIC_SMOKE_STAFF) && operator.getSpellBook() == SpellBook.MODERN;
    }
}
