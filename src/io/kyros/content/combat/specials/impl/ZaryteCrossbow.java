package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public class ZaryteCrossbow extends Special {

    public ZaryteCrossbow() {
        super(7.5, 3.0, 1.5, new int[] { 26374 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.usingBow = true;
        player.startAnimation(9166);
        if (player.playerAttackingIndex > 0 && target instanceof Player) {
            RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 1995, 43, 31, 37, 10);
        } else if (player.npcAttackingIndex > 0 && target instanceof NPC) {
            RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 1995, 43, 31, 37, 10);
        }
        if (damage.isSuccess()) {
            player.usingZaryteSpec = true;
        }
    }


    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}