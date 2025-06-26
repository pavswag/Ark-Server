package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;

public class OsmumtenFang extends Special {

    public OsmumtenFang() {
        super(5.0, 2.0, 1.50, new int[] { 26219, 27246, 33202, 33430 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.gfx0(2124);
        player.startAnimation(6118);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
