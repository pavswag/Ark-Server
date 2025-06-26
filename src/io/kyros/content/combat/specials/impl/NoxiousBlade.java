package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.Graphic;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.player.Player;

public class NoxiousBlade extends Special {

    public NoxiousBlade() {
        super(5.0, 1.00, 1.00, new int[] { 29796 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startGraphic(new Graphic(2930));
        player.startAnimation(11514);
        player.getHealth().resolveStatus(HealthStatus.VENOM, 300);
        player.getHealth().resolveStatus(HealthStatus.POISON, 300);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
