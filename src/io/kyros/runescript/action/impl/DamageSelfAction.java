package io.kyros.runescript.action.impl;

import io.kyros.content.combat.HitMask;
import io.kyros.model.entity.player.Player;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class DamageSelfAction implements Action {
    private int damage;

    public DamageSelfAction(int damage) {
        this.damage = damage;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        System.out.println("Damaging player " + player.getName() + " for " + damage + " points");
        player.appendDamage(null, damage, HitMask.HIT);
        // Implement damage logic here
    }
}

