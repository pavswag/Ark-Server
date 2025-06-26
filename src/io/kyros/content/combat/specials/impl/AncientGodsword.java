package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;

public class AncientGodsword extends Special {

    public AncientGodsword() {
        super(5.0, 1.0, 1.1, new int[] { 26233 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(9171);
        player.gfx0(1996);
        if (damage.getAmount() > 0) {
            if (target.isPlayer()) {
                target.asPlayer().sendMessage("You have been hit by @red@Blood Sacrifice!");
            }
            CycleEventHandler.getSingleton().addEvent(target, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (container.getTotalExecutions() == 5) {
                        int damage2 = 25;
                        player.getDamageQueue().add(new Damage(target, damage2, 2, player.playerEquipment, HitMask.HIT, CombatType.MAGE));
                        player.getHealth().increase(25);
                        target.startGraphic(new Graphic(2003, 0, Graphic.GraphicHeight.MIDDLE));
                        container.stop();
                    }
                }
            }, 1);
        }
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }

}