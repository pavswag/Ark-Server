package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.core.HitDispatcher;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.CombatType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public class DragonKnife extends Special {

    public DragonKnife() {
        super(2.5, 1.0, 1.0, new int[] { 22804 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(8291);
        int projectile = 699;
        int speed = 35;
        if (player.playerAttackingIndex > 0 && target instanceof Player) {
            RangeData.fireProjectilePlayer(player, (Player) target, 50, speed, projectile, 60, 31, 25, 25);
        } else if (player.npcAttackingIndex > 0 && target instanceof NPC) {
            RangeData.fireProjectileNpc(player, (NPC) target, 50, speed, projectile, 60, 31, 25, 25);
        }
        player.getItems().deleteArrow();
        player.getItems().deleteArrow();
        HitDispatcher.getHitEntity(player, target).playerHitEntity(CombatType.RANGE, null);
    }


    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}

