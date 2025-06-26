package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.Graphic;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public class DemonxSpear extends Special {
    public DemonxSpear() {
        super(5.5, 1.5, 2, new int[] { 25979, 27287, 33204, 33432 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(8532);
        player.gfx0(1760);
        target.startGraphic(new Graphic(1759));

        damage.setAmount(100);

        if (player.getArboContainer().inArbo() && player.getInstance() != null && (player.getItems().isWearingItem(33204) || player.getItems().isWearingItem(33432))) {
            for (NPC npc : player.getInstance().getNpcs()) {
                if (npc.getNpcId() == 3233) {
                    npc.startGraphic(new Graphic(1759));
                    npc.appendDamage(player, npc.getHealth().getMaximumHealth(), HitMask.HIT);
                }
            }
        }
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
