package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;

public class GraniteMaulHandleSpecialAttack extends Special {

    public GraniteMaulHandleSpecialAttack() {
        super(5.0, 1, 1, new int[]{24225, 24227});
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {

    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}